package com.example.miguelpalacios.app_blefinal.Activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miguelpalacios.app_blefinal.R;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by miguelpalacios on 06/11/15.
 */
public class ActivityLeerNFC extends AppCompatActivity {
    private static final String TAG = ActivityLeerNFC.class.getSimpleName();
    EditText mEditTextNombre, mEditTextDireccion, mEditTextTelefono, mEditTextPadecimientos;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private PendingIntent mPendingIntent;
    private TextView mTextViewOpcion;
    NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_nfc);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Build.VERSION.SDK_INT < 19)
        {
            FrameLayout mFrameLayoutToolBar = (FrameLayout)findViewById(R.id.FrameLayoutToolbar);
            mFrameLayoutToolBar.setVisibility(View.GONE);
        }

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

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        mTextViewOpcion = (TextView)findViewById(R.id.textViewOpcion);
        mTextViewOpcion.setText(R.string.leer_datos_nfc);

        mEditTextNombre = (EditText)findViewById(R.id.editTextNombre);
        mEditTextDireccion = (EditText)findViewById(R.id.editTextDireccion);
        mEditTextTelefono = (EditText)findViewById(R.id.editTextTelefono);
        mEditTextPadecimientos = (EditText)findViewById(R.id.editTextPadecimientos);

        mEditTextNombre.setFocusable(false);
        mEditTextNombre.setEnabled(false);
        mEditTextDireccion.setFocusable(false);
        mEditTextDireccion.setEnabled(false);
        mEditTextTelefono.setFocusable(false);
        mEditTextTelefono.setEnabled(false);
        mEditTextPadecimientos.setFocusable(false);
        mEditTextPadecimientos.setEnabled(false);

        FloatingActionButton mfab = (FloatingActionButton) findViewById(R.id.fab);
        mfab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupForegroundDispatch(this, mNfcAdapter, mPendingIntent);
        //mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onPause() {

        //stopForegroundDispatch(this, mNfcAdapter);
        if (mNfcAdapter != null) {
            stopForegroundDispatch(this, mNfcAdapter);
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Toast.makeText(this, "Tag NFC encontrada", Toast.LENGTH_SHORT).show();
        setIntent(intent);
        handleIntent(intent);
    }


    private void handleIntent(Intent intent){
        String action = intent.getAction();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Log.e(TAG, "NDEF");
            String type = intent.getType();
            if(MIME_TEXT_PLAIN.equals(type))
            {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            } else{
                Log.d(TAG, "Mime type: " + type);
            }
        }else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
        {
            Log.e(TAG, "TECH");
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for(String tech : techList){
                if(searchedTech.equals(tech))
                {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
        else if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
        {
            Parcelable[] tagMessage = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(tagMessage != null)
            {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String[] techList = tag.getTechList();
                String searchedTech = Ndef.class.getName();

                for(String tech : techList){
                    if(searchedTech.equals(tech))
                    {
                        new NdefReaderTask().execute(tag);
                        break;
                    }
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "no hay datos", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static void setupForegroundDispatch(final Activity activity, NfcAdapter nfcAdapter, PendingIntent pendingIntent)
    {
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, null, null);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter nfcAdapter)
    {
        nfcAdapter.disableForegroundDispatch(activity);
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

    private class NdefReaderTask extends AsyncTask<Tag, Void, String>
    {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            Log.e(TAG, "Ndef: " + tag);
            Log.e(TAG, "id: " + tag.getId());
            if(ndef == null) {
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for(NdefRecord ndefRecord : records)
            {
                if(ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)){
                    try{
                        return readText(ndefRecord);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();

            String textEncoding;
            if ((payload[0] & 128) == 0) textEncoding = "UTF-8";
            else textEncoding = "UTF-16";

            int languageCodeLength = payload[0] & 0063;

            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null)
            {
                try {
                    String[] Datos = result.split(",");
                    mEditTextNombre.setText(Datos[0]);
                    mEditTextDireccion.setText(Datos[1]);
                    mEditTextTelefono.setText(Datos[2]);
                    mEditTextPadecimientos.setText(Datos[3]);
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Sin formato de lectura", Toast.LENGTH_SHORT).show();
                    mEditTextNombre.setText("");
                    mEditTextDireccion.setText("");
                    mEditTextTelefono.setText("");
                    mEditTextPadecimientos.setText("");
                }
            }
        }
    }
}
