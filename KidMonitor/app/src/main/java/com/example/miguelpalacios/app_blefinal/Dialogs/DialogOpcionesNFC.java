package com.example.miguelpalacios.app_blefinal.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.miguelpalacios.app_blefinal.Activitys.ActivityEscribirNFC;
import com.example.miguelpalacios.app_blefinal.Activitys.ActivityLeerNFC;
import com.example.miguelpalacios.app_blefinal.R;

/**
 * Created by miguelpalacios on 06/11/15.
 */
public class DialogOpcionesNFC {
    private static final String TAG = DialogOpcionesNFC.class.getSimpleName();
    private Context mContext;
    private Dialog opcionesDialogNFC;

    public DialogOpcionesNFC(Context context)
    {
        mContext = context;
    }

    public Dialog crearDialogOpcionesNFC()
    {
        opcionesDialogNFC = new Dialog(mContext, R.style.Theme_Dialog_Translucent);
        opcionesDialogNFC.show();
        opcionesDialogNFC.setCancelable(false);
        opcionesDialogNFC.setContentView(R.layout.dialog_opciones_nfc);

        ((Button)opcionesDialogNFC.findViewById(R.id.buttonEscribirNFC)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ActivityEscribirNFC.class);
                mContext.startActivity(intent);
                opcionesDialogNFC.dismiss();
            }
        });

        ((Button) opcionesDialogNFC.findViewById(R.id.buttonLeerNFC)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ActivityLeerNFC.class);
                mContext.startActivity(intent);
                opcionesDialogNFC.dismiss();
            }
        });

        ((ImageButton)opcionesDialogNFC.findViewById(R.id.imageButtonSalir)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opcionesDialogNFC.dismiss();
            }
        });

        return opcionesDialogNFC;
    }
}
