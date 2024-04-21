package com.example.joyserial.ui.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.joyserial.MainActivity;
import com.example.joyserial.R;
import com.example.joyserial.data.AppData;
import com.example.joyserial.data.Gamepad;
import com.example.joyserial.databinding.FragmentHomeBinding;
import com.example.joyserial.utils.UsbService;

import java.lang.ref.WeakReference;
import java.util.Set;

public class HomeFragment extends Fragment implements View.OnTouchListener {

    private FragmentHomeBinding binding;
    private  Gamepad gamepadData;
    private UsbService usbService;
    private Thread dataThread;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        gamepadData = Gamepad.getInstance();

        initializeControls();

        if(dataThread == null || (!dataThread.isAlive() && !AppData.IsTerminalOpen)) {
            sendDataInBackground();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("ClickableViewAccessibility")
    void initializeControls() {
        binding.Up.setOnTouchListener(this);
        binding.Down.setOnTouchListener(this);
        binding.Left.setOnTouchListener(this);
        binding.Right.setOnTouchListener(this);

        binding.A.setOnTouchListener(this);
        binding.B.setOnTouchListener(this);
        binding.X.setOnTouchListener(this);
        binding.Y.setOnTouchListener(this);
        binding.pauseButton.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int touchId = v.getId();
        if (touchId == R.id.Up) {
            if (event.getAction() == MotionEvent.ACTION_UP) gamepadData.hat(0);
            else gamepadData.hat(1);
        } else if (touchId == R.id.Down) {
            if (event.getAction() == MotionEvent.ACTION_UP) gamepadData.hat(0);
            else gamepadData.hat(2);
        } else if (touchId == R.id.Left) {
            if (event.getAction() == MotionEvent.ACTION_UP) gamepadData.hat(0);
            else gamepadData.hat(3);
        } else if (touchId == R.id.Right) {
            if (event.getAction() == MotionEvent.ACTION_UP) gamepadData.hat(0);
            else gamepadData.hat(4);
        }

        if (touchId == R.id.A) {
            gamepadData.A(event.getAction() != MotionEvent.ACTION_UP);
        }
        if (touchId == R.id.B) {
            gamepadData.B(event.getAction() != MotionEvent.ACTION_UP);
        }
        if (touchId == R.id.X) {
            gamepadData.X(event.getAction() != MotionEvent.ACTION_UP);
        }
        if (touchId == R.id.Y) {
            gamepadData.Y(event.getAction() != MotionEvent.ACTION_UP);
        }
        if (touchId == R.id.pause_button)
            gamepadData.pause(event.getAction() != MotionEvent.ACTION_UP);

        return false;
    }

    void sendDataInBackground() {
        dataThread = new DataThread();
        dataThread.start();
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
         }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };
    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        Context ctx = getContext();
        if(ctx != null) ctx.registerReceiver(mUsbReceiver, filter);
        startService(usbConnection);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();
    }

    @Override
    public void onPause() {
        super.onPause();
        Context ctx = getContext();
        if(ctx != null) {
            ctx.unregisterReceiver(mUsbReceiver);
            ctx.unbindService(usbConnection);
        }
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

    private class DataThread extends Thread {
        @Override
        public void run() {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Continuous data transmission started", Toast.LENGTH_SHORT).show();
            });
            while (!AppData.IsTerminalOpen) {
                    if (usbService != null && !gamepadData.pause()) {
                        usbService.write(gamepadData.toByteArray());
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
            }
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Terminal data transmission started", Toast.LENGTH_SHORT).show();
            });
        }
    }
}