package com.wahana.wahanamobile.camerates;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wahana.wahanamobile.Absensi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int cameraId;
    Activity activity;

    public ImageSurfaceView(Context context, Camera camera){
        super(context);

        mCamera = camera;
        setCameraDisplayOrientation(activity, cameraId, mCamera);
        //get the holder and set this class as the callback, so we can get camera data here
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    public ImageSurfaceView(Activity activity, Camera camera, int cameraId){
        super(activity);
        this.activity = activity;
        mCamera = camera;
        this.cameraId = cameraId;
//        mCamera.setDisplayOrientation(90);
        setCameraDisplayOrientation(activity, cameraId, mCamera);
        //get the holder and set this class as the callback, so we can get camera data here
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            //when the surface is created, we can set the camera to draw images in this surfaceholder
            Camera.Parameters params=mCamera.getParameters();
//            params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            if(activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)){
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            params.setExposureCompensation(0);
            params.setPictureFormat(ImageFormat.JPEG);
            params.setJpegQuality(100);
            List<Camera.Size> sizes = params.getSupportedPictureSizes();
            Camera.Size size = sizes.get(0);
            for (Camera.Size sizee : params.getSupportedPictureSizes()) {
                Log.d("hasil camera","w:"+sizee.width+" h"+sizee.height);
            }
            for(int i=0;i<sizes.size();i++)
            {
                if(sizes.get(i).width > size.width)
                    size = sizes.get(i);
            }

            params.setPictureSize(640, 480);
//            params.setPictureSize(size.width,size.height);
            mCamera.setParameters(params);
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        //before changing the application orientation, you need to stop the preview, rotate and then start it again
        if(mHolder.getSurface() == null)//check if the surface is ready to receive camera data
            return;

        try{
            mCamera.stopPreview();
        } catch (Exception e){
            //this will happen when you are trying the camera if it's not running
        }

        //now, recreate the camera preview
        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //our app has only one screen, so we'll destroy the camera in the surface
        //if you are unsing with more screens, please move this code your activity
        mCamera.stopPreview();
        mCamera.release();
    }

    public void switchCamera(){
//        if (inPreview) {
            mCamera.stopPreview();
//        }

        //NB: if you don't release the current camera before switching, you app will crash
        mCamera.release();

        //swap the id of the camera to be used
        if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }else {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        mCamera = Camera.open(cameraId);

        setCameraDisplayOrientation(activity, cameraId, mCamera);
        try {
//            Camera.Parameters params=mCamera.getParameters();
//            if(activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)){
//                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//            }
//            params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
//            params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
//            params.setExposureCompensation(0);
//            params.setPictureFormat(ImageFormat.JPEG);
//            params.setJpegQuality(100);
//            List<Camera.Size> sizes = params.getSupportedPictureSizes();
//            Camera.Size size = sizes.get(0);
//            for (Camera.Size sizee : params.getSupportedPictureSizes()) {
//                Log.d("hasil camera","w:"+sizee.width+" h"+sizee.height);
//            }
//            for(int i=0;i<sizes.size();i++)
//            {
//                if(sizes.get(i).width > size.width)
//                    size = sizes.get(i);
//            }

//            params.setPictureSize(640, 480);
//            params.setPictureSize(size.width,size.height);
//
//            mCamera.setParameters(params);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("hasil error camera", ""+e);
        }
    }

    public void takePicture(){
        mCamera.takePicture(null, null, pictureCallback);
    }

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            int rotation = 0;
            switch (display.getRotation()) {
                case Surface.ROTATION_0: // This is display orientation
                    rotation = 90;
                    break;
                case Surface.ROTATION_90:
                    rotation = 0;
                    break;
                case Surface.ROTATION_180:
                    rotation = 270;
                    break;
                case Surface.ROTATION_270:
                    rotation = 180;
                    break;
            }

            Bitmap bitmap2 = BitmapTools.toBitmap(data);
            bitmap2 = BitmapTools.rotate(bitmap2, rotation, cameraId);
            FileOutputStream out = null;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);
                //ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.JPEG, 80, out);
                Log.d("hasil gambar1", ""+filename);
                //  bArray = bos.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("hasil error", ""+e);
            }

            Log.d("hasil gambar", ""+filename);
            Intent pindah = new Intent(activity, Absensi.class);
            pindah.putExtra("foto", filename);
            activity.setResult(-1, pindah);
            activity.finish();
        }
    };

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/AbsenImages");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"+ System.currentTimeMillis() + ".jpg");
        return uriSting;

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
}
