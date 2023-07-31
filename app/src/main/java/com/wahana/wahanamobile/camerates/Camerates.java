package com.wahana.wahanamobile.camerates;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.wahana.wahanamobile.Absensi;
import com.wahana.wahanamobile.Ops.modaAngkutan.AddDataMAActivity;
import com.wahana.wahanamobile.Ops.modaAngkutan.SearchMobilActivity;
import com.wahana.wahanamobile.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class Camerates extends ActionBarActivity {

    private ImageSurfaceView mImageSurfaceView;
    private Camera camera;

    private FrameLayout cameraPreviewLayout;
//    private ImageView capturedImageHolder;
    Camera mCamera = null;
    int camBackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerates);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cameraPreviewLayout = (FrameLayout)findViewById(R.id.camera_preview);
//        capturedImageHolder = (ImageView)findViewById(R.id.captured_image);

        camera = checkDeviceCamera();
        camBackId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mImageSurfaceView = new ImageSurfaceView(Camerates.this, camera, camBackId);
        cameraPreviewLayout.addView(mImageSurfaceView);

        ImageView captureButton = (ImageView)findViewById(R.id.button);
        ImageView change = (ImageView)findViewById(R.id.change_camera);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageSurfaceView.takePicture();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageSurfaceView.switchCamera();
            }
        });
    }

    public void switchCamera(){
//        if (inPreview) {
        mCamera.stopPreview();
//        }

        //NB: if you don't release the current camera before switching, you app will crash
//        mCamera.release();

        //swap the id of the camera to be used
        if(camBackId == Camera.CameraInfo.CAMERA_FACING_BACK){
            camBackId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }else {
            camBackId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        mCamera = Camera.open(camBackId);

        setCameraDisplayOrientation(Camerates.this, camBackId, mCamera);

        mImageSurfaceView = new ImageSurfaceView(Camerates.this, camera, camBackId);
        cameraPreviewLayout.addView(mImageSurfaceView);
//        try {
//
//            mCamera.setPreviewDisplay(mHolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("hasil error camera", ""+e);
//        }
//        mCamera.startPreview();
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private Camera checkDeviceCamera(){
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }

//    PictureCallback pictureCallback = new PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//            Display display = getWindowManager().getDefaultDisplay();
//            int rotation = 0;
//            switch (display.getRotation()) {
//                case Surface.ROTATION_0: // This is display orientation
//                    rotation = 90;
//                    break;
//                case Surface.ROTATION_90:
//                    rotation = 0;
//                    break;
//                case Surface.ROTATION_180:
//                    rotation = 270;
//                    break;
//                case Surface.ROTATION_270:
//                    rotation = 180;
//                    break;
//            }
//
//            Intent pindah = new Intent(Camerates.this, Absensi.class);
//            pindah.putExtra("foto", data);
//            pindah.putExtra("rotation", rotation);
//            setResult(RESULT_OK, pindah);
//            finish();
//        }
//    };

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private Bitmap scaleDownBitmapImage(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return resizedBitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
