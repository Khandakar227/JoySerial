package com.example.joyserial.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.joyserial.R;
import com.example.joyserial.data.AppData;
import com.example.joyserial.databinding.FragmentSettingsBinding;
import com.example.joyserial.utils.UsbService;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private FragmentSettingsBinding binding;
    private Spinner spinner;
    private static final Integer[] baudRates = {4800, 9600, 19200, 38400, 57600, 115200};
    private int baudRate = 9600;
    AppData appData;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        spinner = binding.baudRateDropdown;
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, baudRates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Context ctx = getContext();
        if(ctx != null) {
            appData = AppData.getInstance(ctx);
            baudRate = appData.getBaudRate();
            Log.v("Settings", String.valueOf(baudRate));
        }
        int spinnerPosition = adapter.getPosition(baudRate);
        spinner.setSelection(spinnerPosition);
        spinner.setOnItemSelectedListener(this);
        binding.saveSettings.setOnClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if(parent.getId() == R.id.baud_rate_dropdown) {
            baudRate = baudRates[position%baudRates.length];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        Context ctx = v.getContext();
        if(v.getId() == R.id.save_settings) {
            Toast.makeText(v.getContext(), "Saved", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UsbService.ACTION_BAUD_RATE_CHANGED);
            intent.putExtra("baud_rate", baudRate);
            ctx.sendBroadcast(intent);

            appData.saveBaudRate(baudRate);
        }
    }
}