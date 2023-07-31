package com.wahana.wahanamobile.camerates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Created by team-it on 09/02/18.
 */

public class BitmapTools {

    public static Bitmap toBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data , 0, data.length);
    }

    public static Bitmap rotate(Bitmap in, int angle, int cameraId) {
        if (cameraId == 0){
            Matrix mat = new Matrix();
            mat.postRotate(angle);
            return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), mat, true);
        }else {
            Matrix rotateRight = new Matrix();
            rotateRight.preRotate(90);

            float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
            rotateRight = new Matrix();
            Matrix matrixMirrorY = new Matrix();
            matrixMirrorY.setValues(mirrorY);

            rotateRight.postConcat(matrixMirrorY);

            rotateRight.preRotate(270);

            return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), rotateRight, true);
        }
    }
}
