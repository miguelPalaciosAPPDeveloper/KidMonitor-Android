package com.example.miguelpalacios.app_blefinal.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;

import com.example.miguelpalacios.app_blefinal.R;

/**
 * Created by miguelpalacios on 27/07/15.
 */
public class DialogProgress {
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private int mMensaje;

    public DialogProgress(Context context, int idString)
    {
        mContext = context;
        mMensaje = idString;
    }

    public ProgressDialog generarProgressDialog()
    {
        mProgressDialog = new ProgressDialog(mContext, R.style.Theme_Dialog_Translucent);
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.dialog_progress);
        mProgressDialog.setCancelable(false);

        final TextView textViewTituloProgress = (TextView)mProgressDialog.findViewById(R.id.textViewTituloProgress);
        textViewTituloProgress.setText(mMensaje);

        return  mProgressDialog;
    }
}
