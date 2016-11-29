package com.example.miguelpalacios.app_blefinal.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.miguelpalacios.app_blefinal.R;

import java.util.ArrayList;

/**
 * Created by X1 on 12/08/15.
 */
public class AdapterDeviceSaved extends BaseAdapter {
    private static final String TAG = AdapterDeviceSaved.class.getSimpleName();
    private Context mContext;
    private ArrayList<String> mNombres;
    private ArrayList<String> mClaves;

    public AdapterDeviceSaved(Context context)
    {
        mContext = context;
        mNombres = new ArrayList<String>();
        mClaves = new ArrayList<String>();
    }
    public void setClaves(ArrayList<String> macs){mClaves = macs;}
    public int getCountClave(){return mClaves.size();}

    public String getNombre(int position) {return mNombres.get(position);}
    public String getClave(int position) {return mClaves.get(position);}

    public void addNombres(String nombre) {mNombres.add(nombre);}

    public void clear() {
        mNombres.clear();
        mClaves.clear();
        Log.e(TAG, "clear");
    }
    @Override
    public int getCount() {
        return mNombres.size();
    }

    @Override
    public Object getItem(int position) {

        return mNombres.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if (view == null) {
            Log.e(TAG, "view");
            LayoutInflater mInflator = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = mInflator.inflate(R.layout.activity_device_save, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewDevice = (TextView) view.findViewById(R.id.textViewDevice);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final String nombreDevice = mNombres.get(position);
        viewHolder.textViewDevice.setText(nombreDevice);

        return view;
    }

    static class ViewHolder {
        TextView textViewDevice, textViewEstado;
    }
}
