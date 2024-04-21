package com.example.joyserial.ui.arm_control;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.joyserial.data.Gamepad;
import com.example.joyserial.databinding.FragmentArmControlBinding;

public class ArmControlFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private final int  INITIAL_PROGRESS = 127;
    private FragmentArmControlBinding binding;

    private Gamepad gamepad;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentArmControlBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        gamepad = Gamepad.getInstance();

        binding.baseRotation.setOnSeekBarChangeListener(this);
        binding.gripperRotation.setOnSeekBarChangeListener(this);
        binding.pillar.setOnSeekBarChangeListener(this);
        binding.primary.setOnSeekBarChangeListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == binding.baseRotation.getId()) gamepad.baseRotation(progress);
        if (seekBar.getId() == binding.primary.getId()) gamepad.primary(progress);
        if (seekBar.getId() == binding.pillar.getId()) gamepad.pillar(progress);
        if (seekBar.getId() == binding.gripperRotation.getId()) gamepad.gripper(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekBar.setProgress(INITIAL_PROGRESS);
    }
}