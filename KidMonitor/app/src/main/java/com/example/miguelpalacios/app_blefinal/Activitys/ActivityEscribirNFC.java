package com.example.miguelpalacios.app_blefinal.Activitys;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.miguelpalacios.app_blefinal.R;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class ActivityEscribirNFC extends AppCompatActivity {
    private static final String TAG = ActivityEscribirNFC.class.getSimpleName();
    EditText mEditTextNombre, mEditTextDireccion, mEditTextTelefono, mEditTextPadecimientos;
    NfcAdapter mNfcAdapter;
    String Mensaje = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_nfc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(!mNfcAdapter.isEnabled())
        {
            AlertDialog.Builder activarNFC = new AlertDialog.Builder(this);
            activarNFC.setTitle("NFC actualmente se encuentra desactivado")
                    .setCancelable(false)
                    .setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            finish();
                            return;
                        }
                    })
                    .show();
        }

        mEditTextNombre = (EditText)findViewById(R.id.editTextNombre);
        mEditTextDireccion = (EditText)findViewById(R.id.editTextDireccion);
        mEditTextTelefono = (EditText)findViewById(R.id.editTextTelefono);
        mEditTextPadecimientos = (EditText)findViewById(R.id.editTextPadecimientos);

        FloatingActionButton mfab = (FloatingActionButton) findViewById(R.id.fab);
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Nombre = mEditTextNombre.getText().toString();
                String Direccion = mEditTextDireccion.getText().toString();
                String Telefono = mEditTextTelefono.getText().toString();
                String Padecimientos = mEditTextPadecimientos.getText().toString();
                if(Nombre.isEmpty() || Direccion.isEmpty() || Telefono.isEmpty() || Padecimientos.isEmpty())
                {
                    Snackbar.make(view, "Debe llenar todos los campos", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    Mensaje = "Nombre: " + Nombre + "\n," + "Direccion: " +  Direccion + "\n," + "Telefono: " + Telefono + "\n," + "Padecimientos: " + Padecimientos;
                    Snackbar.make(view, "Mensaje completo, coloque el tag NFC", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        enabledForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(!Mensaje.isEmpty()) {
            if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                //Toast.makeText(this, "NFC Intent", Toast.LENGTH_SHORT).show();

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage ndefMessage = createNdefMessage(Mensaje);

                writeNdefMessage(tag, ndefMessage);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "El mensaje esta vacio", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void enabledForegroundDispatchSystem()
    {
        Intent intent = new Intent(this, ActivityEscribirNFC.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem()
    {
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage)
    {
        try{
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if(ndefFormatable == null)
            {
                Toast.makeText(this, "No es un NDEF tag formateable", Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag escrita :)", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage)
    {
        try{

            if(tag == null)
            {
                Toast.makeText(this, "Tag vacia", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if(ndef == null)
            {
                formatTag(tag, ndefMessage);
            }
            else
            {
                ndef.connect();

                if(!ndef.isWritable())
                {
                    Toast.makeText(this, "Tag no puede ser escrita", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this, "Tag escrita :)", Toast.LENGTH_SHORT).show();
                Mensaje = "";
                mEditTextNombre.setText("");
                mEditTextTelefono.setText("");
                mEditTextDireccion.setText("");
                mEditTextPadecimientos.setText("");
            }

        }catch (Exception e)
        {
            Log.e("writeNdefMessafe", e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String contenido)
    {
        try
        {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = contenido.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0 , textLength);

            return  new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private NdefMessage createNdefMessage(String contenido)
    {
        NdefRecord ndefRecord = createTextRecord(contenido);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ ndefRecord});

        return ndefMessage;
    }
}
