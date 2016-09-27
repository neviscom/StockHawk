package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author Nikita Simonov
 */

public final class MyToast  {

    public static void showToast(Context context, @StringRes int stringId) {
        showToast(context, context.getString(stringId));
    }

    public static void showToast(Context context, CharSequence text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
        toast.show();
    }
}
