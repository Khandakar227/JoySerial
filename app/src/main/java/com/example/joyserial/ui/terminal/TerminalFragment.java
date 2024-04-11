package com.example.joyserial.ui.terminal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.joyserial.R;
import com.example.joyserial.data.AppData;
import com.example.joyserial.databinding.FragmentTerminalBinding;

public class TerminalFragment extends Fragment implements View.OnClickListener {

    private FragmentTerminalBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentTerminalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.send.setOnClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == binding.send.getId()) {
            Toast.makeText(v.getContext(), binding.terminalText.getText(), Toast.LENGTH_SHORT).show();
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
}