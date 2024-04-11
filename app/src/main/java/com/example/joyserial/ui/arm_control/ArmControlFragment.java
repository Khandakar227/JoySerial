package com.example.joyserial.ui.arm_control;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.joyserial.databinding.FragmentArmControlBinding;

public class ArmControlFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private final int  INITIAL_PROGRESS = 127;
    private FragmentArmControlBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentArmControlBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.baseRotation.setOnSeekBarChangeListener(this);
        binding.gripperRotation.setOnSeekBarChangeListener(this);
        binding.lower.setOnSeekBarChangeListener(this);
        binding.upper.setOnSeekBarChangeListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekBar.setProgress(INITIAL_PROGRESS);
    }
}