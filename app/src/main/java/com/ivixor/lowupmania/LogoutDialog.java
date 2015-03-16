package com.ivixor.lowupmania;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.vk.sdk.VKSdk;


public class LogoutDialog extends DialogFragment {

    public interface ResultListener {
        void onPositiveResult();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity activity = getActivity();

        if (!(activity instanceof ResultListener)) {
            throw new IllegalStateException("Activity must implement ResultListener interface.");
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Logout")
                .setMessage("Do you really want to logout and quit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((LoginActivity) activity).onPositiveResult();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();

        return dialog;
    }
}
