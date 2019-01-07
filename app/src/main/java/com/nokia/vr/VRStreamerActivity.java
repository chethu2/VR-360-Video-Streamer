/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nokia.vr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;

import org.gearvrf.GVRActivity;
import org.gearvrf.gvr360video.R;
import org.gearvrf.io.GVRTouchPadGestureListener;
import org.gearvrf.scene_objects.GVRVideoSceneObjectPlayer;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class VRStreamerActivity extends GVRActivity {


    public static final String TAG = "VRStreamerActivity" ;
    /**
     * Called when the activity is first created.
     */
    private GVRVideoSceneObjectPlayer<?> videoSceneObjectPlayer;
    String rtmpUrl;
    String sdkPortIp;
    private boolean enableSocket = false;
    private Socket socket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(getApplicationContext(), BluetoothConnectionService.class);
        startService(serviceIntent);
        startActivityForResult(new Intent(getApplicationContext(),VideoPlayerActivity.class),999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 999){
            rtmpUrl = data.getStringExtra("rtmpUrl");
            sdkPortIp = data.getStringExtra("sdkServerIP");
            Log.i("Hiiiiiiiiiiiiii1 :URL", rtmpUrl);
            Log.i("Hiiiiiiiiiiiiii2 :URL", sdkPortIp);
            socket = createSocket(sdkPortIp);
          videoSceneObjectPlayer = makeExoPlayer(rtmpUrl);
            Streamer streamerActivity = new Streamer(videoSceneObjectPlayer);
            setMain(streamerActivity, "main.xml");
        }
    }

    public  GVRVideoSceneObjectPlayer<ExoPlayer> makeExoPlayer(String rtmpUrl) {

        final Context context = this;
        final DataSource.Factory dataSourceFactory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return new AssetDataSource(context);
            }
        };
        final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context,
                new DefaultTrackSelector());
        Log.i("Hiiiiiiiiiiiiii3 :URL", rtmpUrl);
        RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory();
        MediaSource videoSource = new ExtractorMediaSource.Factory(rtmpDataSourceFactory)
                .createMediaSource(Uri.parse(rtmpUrl));
        player.prepare(videoSource);
        player.setPlayWhenReady(true);

        return new GVRVideoSceneObjectPlayer<ExoPlayer>() {
            @Override
            public ExoPlayer getPlayer() {
                return player;
            }

            @Override
            public void setSurface(final Surface surface) {
                player.addListener(new Player.DefaultEventListener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        switch (playbackState) {
                            case Player.STATE_BUFFERING:
                                break;
                            case Player.STATE_ENDED:
                                player.seekTo(0);
                                break;
                            case Player.STATE_IDLE:
                                break;
                            case Player.STATE_READY:
                                break;
                            default:
                                break;
                        }
                    }
                });

                player.setVideoSurface(surface);

            }
            @Override
            public void release () {
                player.release();
            }

            @Override
            public boolean canReleaseSurfaceImmediately () {
                return false;
            }

            @Override
            public void pause () {
                player.setPlayWhenReady(false);
            }

            @Override
            public void start () {
                player.setPlayWhenReady(true);
            }

            @Override
            public boolean isPlaying() {
                return true;
            }

        };
    }

    @Override
    public  boolean dispatchKeyEvent(KeyEvent event){
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                enableSocket = true;
                Log.i("Hiiiiii :back", "clicked"+enableSocket);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                Log.i("Hiiiiiiiiiiiiii :V UP", "clicked");
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Log.i("Hiiiiiiiiiiiiii :V Down", "clicked");
                break;
        }
        return false;
    }
    float prevX = -1;
    float prevY = -1;
    long prevClickTime = 0;
    boolean isParked = true;
    String keyCode = "";

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent: called bro");
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent: socket create"+sdkPortIp);
        if(enableSocket) {
            long clickedTime = System.currentTimeMillis();
            double axisx = 1280 * 100000;
            double axisy = 720 * 100000;

            if ((1280 == event.getX() && 720 == event.getY())) {

                if (clickedTime - prevClickTime > 1500) {
                    prevClickTime = clickedTime;
                    if (isParked) {
                        Log.i("Hiiiiiiiiiiiiii :unpark", "clicked");
                        keyCode= "2";
                        sendKetPressedOverNetwork();
                        isParked = false;
                    } else {

                        Log.i("Hiiiiiiiiiiiiii :parked", "clicked");
                        keyCode = "3";
                        sendKetPressedOverNetwork();
                        isParked = true;

                    }
                }
            } else {
                if (axisx > prevX && axisy > prevY) {
                    Log.i("front", "clicked");
                    keyCode = "38";
                } else if (axisx > prevX && axisy < prevY) {

                    Log.i("Hiiiiiiiiiiiiii :right", "clicked");
                    keyCode = "39";
                } else if (axisx < prevX && axisy > prevY) {

                    Log.i("left", "clicked");
                    keyCode = "37";
                } else if (axisx < prevX && axisy < prevY) {

                    Log.i("Hiiiiiiiiiiiiii :back", "clicked");
                    keyCode = "40";
                }
                prevX = 100000 * event.getX();
                prevY = 100000 * event.getY();
                if(!TextUtils.isEmpty(keyCode)) {
                    sendKetPressedOverNetwork();
                }
            }
        }
        return false;
    }

    private void sendKetPressedOverNetwork() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                socket.emit("pressedKeyCode",keyCode);
                socket.disconnect();
            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
            }

        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                ((Exception)args[0]).printStackTrace();
            }

        });
        socket.connect();
    }
    public  Socket createSocket(String sdkPortIp){
        Socket socket = null;
        Log.i("Hiiiiiiiiiiiiii :create", "clicked");
        if(null == socket) {

            try {
                socket = IO.socket("http://"+ sdkPortIp +"/");
            } catch (URISyntaxException e) {
                Log.i("Hiiiiiiiiiiiiii :fail", "clicked");
                e.printStackTrace();
            }
        }
        return socket;
    }

}