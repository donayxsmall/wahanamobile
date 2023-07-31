package com.wahana.wahanamobile.utils;

import android.os.Environment;

/**
 * Created by team-it on 05/10/18.
 */

public class CheckForSDCard {
    //Check If SD Card is present or not method
    public boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}
