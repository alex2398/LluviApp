package com.avalladares.lluviapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.avalladares.lluviapp.R;

/**
 * Created by avalladares on 19/06/2015.
 * Returns an dialog object with default title (string resource),
 * string parameter and an OK button
 */

public class AlertDialogFragment extends DialogFragment {

    private String mText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.title_Error))
                .setMessage(mText)
                .setPositiveButton(context.getString(R.string.ok_Button), null);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    public void setText(String text) {
        mText = text;
    }
}
