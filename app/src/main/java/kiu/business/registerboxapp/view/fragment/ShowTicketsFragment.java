package kiu.business.registerboxapp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kiu.business.registerboxapp.databinding.FragmentShowTicketsListBinding;
import kiu.business.registerboxapp.view.adapter.ShowTicketsAdapter;


public class ShowTicketsFragment extends Fragment {

    FragmentShowTicketsListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentShowTicketsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = binding.recycleViewTickets;
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(new ShowTicketsAdapter(binding, getChildFragmentManager()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}