package com.wahana.wahanamobile.helper;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by donay on 12/11/19.
 */

public class hiddensoftkeyboard {

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
