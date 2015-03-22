package com.ivixor.lowupmania;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.tv.TvContract;
import android.os.Bundle;

import com.vk.sdk.VKSdk;


public class LogoutDialog extends DialogFragment {

    private NoticeDialogListener listener;

    public interface NoticeDialogListener {
        void onDialogPositiveResult(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        listener = (NoticeDialogListener) getActivity();

        if (!(listener instanceof NoticeDialogListener)) {
            throw new IllegalStateException("Activity must implement ResultListener interface.");
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Logout")
                .setMessage("Do you really want to logout and quit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogPositiveResult(LogoutDialog.this);
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
