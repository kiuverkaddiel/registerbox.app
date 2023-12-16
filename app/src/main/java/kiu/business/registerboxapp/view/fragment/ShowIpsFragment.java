package kiu.business.registerboxapp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ips.model.Ips;
import kiu.business.registerboxapp.databinding.FragmentShowIpsBinding;

public class ShowIpsFragment extends Fragment {

    private FragmentShowIpsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentShowIpsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String content = Ips.getInstance().toString();
        binding.textViewIpsContent.setText(content);
    }

}