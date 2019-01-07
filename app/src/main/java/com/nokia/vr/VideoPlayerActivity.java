

package com.nokia.vr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.gearvrf.gvr360video.R;

import static android.content.ContentValues.TAG;


/**
 * Created by chea on 5/28/2018.
 */



public class VideoPlayerActivity extends Activity {

    EditText rtmp;
    EditText sdkServer;


    static Button videoButton;
    static String rtmpUrl;
    static String sdkServerIP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gvr);
        videoButton = (Button) findViewById(R.id.video_button);
            videoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rtmp = (EditText) findViewById(R.id.rtmp);
                    sdkServer = (EditText) findViewById(R.id.sdkserver);
                    rtmpUrl = rtmp.getText().toString();
                    sdkServerIP = sdkServer.getText().toString();
                    if (!TextUtils.isEmpty(rtmpUrl)) {
                        Intent intent = new Intent();
                        intent.putExtra("rtmpUrl", rtmpUrl);
                        intent.putExtra("sdkServerIP", sdkServerIP);
                        setResult(999, intent);
                        finish();
                    } else {
                        Toast.makeText(VideoPlayerActivity.this, "empty url!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }



}