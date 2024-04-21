package com.example.joyserial.ui.terminal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.joyserial.MainActivity;
import com.example.joyserial.R;
import com.example.joyserial.data.AppData;
import com.example.joyserial.databinding.FragmentTerminalBinding;
import com.example.joyserial.utils.UsbService;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

public class TerminalFragment extends Fragment implements View.OnClickListener {

    private FragmentTerminalBinding binding;
    private UsbService usbService;
    private UsbHandler mHandler;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentTerminalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mHandler = new UsbHandler((MainActivity) getContext());

        binding.send.setOnClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        usbService.setHandler(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        startService(usbConnection);
    }
    private void startService(ServiceConnection serviceConnection) {
        Context ctx = getContext();
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(ctx, UsbService.class);
            if(ctx != null) ctx.startService(startService);
        }
        Intent bindingIntent = new Intent(ctx, UsbService.class);
        if(ctx != null)
            ctx.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == binding.send.getId()) {
            if(usbService != null) {
                String data = String.valueOf(binding.terminalText.getText());
                usbService.write(data.getBytes(StandardCharsets.UTF_8));
                addToTerminal(data, 1);
                binding.terminalText.setText("");
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.terminal_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem startTerm = menu.findItem(R.id.start_terminal);
        if(startTerm != null) {
            if(AppData.IsTerminalOpen) startTerm.setIcon(R.drawable.baseline_terminal_active);
            else startTerm.setIcon(R.drawable.baseline_terminal_24);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        if (item.getItemId() == R.id.start_terminal) {
            AppData.IsTerminalOpen = !AppData.IsTerminalOpen;
            if(AppData.IsTerminalOpen) item.setIcon(R.drawable.baseline_terminal_active);
            else item.setIcon(R.drawable.baseline_terminal_24);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class UsbHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        public UsbHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    addToTerminal(data, 0);
                    binding.terminalMonitorScroll.fullScroll(View.FOCUS_DOWN);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
    public void addToTerminal(String data, int sender) {
        TextView tv = new TextView(this.getContext());
        tv.setText(data);
        tv.setPadding(0, 10, 0, 10);
        tv.setTextColor(sender == 0 ? Color.GREEN : Color.WHITE);
        binding.terminalTextMonitor.addView(tv);
    }

}