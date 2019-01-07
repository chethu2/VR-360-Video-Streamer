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

import android.util.Log;
import android.view.MotionEvent;

import org.gearvrf.GVRContext;
import org.gearvrf.GVREventListeners;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRScene;
import org.gearvrf.IActivityEvents;
import org.gearvrf.io.GVRControllerType;
import org.gearvrf.io.GVRCursorController;
import org.gearvrf.io.GVRInputManager;
import org.gearvrf.io.GVRTouchPadGestureListener;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject.GVRVideoType;
import org.gearvrf.scene_objects.GVRVideoSceneObjectPlayer;

import java.util.ArrayList;

public class Streamer extends GVRMain
{
    private GVRContext mGVRContext;
    private ArrayList<GVRCursorController> controllerList = new ArrayList<GVRCursorController>();
    Streamer(GVRVideoSceneObjectPlayer<?> player) {
        mPlayer = player;
    }


    @Override
    public void onSwipe(GVRTouchPadGestureListener.Action action, float vx) {
        super.onSwipe(action, vx);
        Log.i("onSwipe","onSwipe");
    }

    /** Called when the activity is first created. */
    @Override
    public void onInit(GVRContext gvrContext) {
        mGVRContext = gvrContext;
        GVRScene scene = gvrContext.getMainScene();


        // set up camerarig position (default)
        scene.getMainCameraRig().getTransform().setPosition(0.0f, 0.0f, 0.0f);

        // create sphere / mesh
        GVRSphereSceneObject sphere = new GVRSphereSceneObject(gvrContext, 72, 144, false);
        GVRMesh mesh = sphere.getRenderData().getMesh();

        // create video scene
        GVRVideoSceneObject video = new GVRVideoSceneObject(gvrContext, mesh, mPlayer, GVRVideoType.MONO);
        video.getTransform().setScale(100f, 100f, 100f);
        video.setName("video");

        // apply video to scene
        scene.addSceneObject(video);
    }

    @Override
    public void onStep() {
    }

    private final GVRVideoSceneObjectPlayer<?> mPlayer;
}