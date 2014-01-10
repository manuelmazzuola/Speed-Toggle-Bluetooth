package com.manuelmazzuola.speedtogglebluetooth;

import android.app.AlertDialog;
import android.content.Context;

/**
 * @author Manuel Mazzuola
 */
public class Utils {
    public static void showAlert(Context ctx, String alertMessage, String alertTitle) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder
                .setTitle(alertTitle)
                .setMessage(alertMessage);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
