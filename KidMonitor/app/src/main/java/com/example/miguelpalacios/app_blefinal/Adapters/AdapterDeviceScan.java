package com.example.miguelpalacios.app_blefinal.Adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.miguelpalacios.app_blefinal.R;

import java.util.ArrayList;

/**
 * Created by miguelpalacios on 27/07/15.
 */
public class AdapterDeviceScan extends BaseAdapter {
    private static final String TAG = AdapterDeviceScan.class.getSimpleName();
    private Context mContext;
    private ArrayList<BluetoothDevice> mLeDevices;

    public AdapterDeviceScan(Context context)
    {
        mContext = context;
        mLeDevices = new ArrayList<BluetoothDevice>();
    }

    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            if (device.getName() != null && device.getName().length() > 0) {
                mLeDevices.add(device);
            }
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater mInflator = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = mInflator.inflate(R.layout.activity_device_scan, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(R.id.textViewDeviceName);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0) {
            viewHolder.deviceName.setText(deviceName);
        }
        else
            viewHolder.deviceName.setText(R.string.unknown_device);

        return view;
    }

    static class ViewHolder {
        TextView deviceName;
    }
}
