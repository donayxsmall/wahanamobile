package com.wahana.wahanamobile.helper;

import android.os.Environment;

/**
 * Created by donay on 06/07/20.
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
