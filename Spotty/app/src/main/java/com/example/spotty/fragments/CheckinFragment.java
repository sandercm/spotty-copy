package com.example.spotty.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotty.ViewModels.MapViewmodel;
import com.example.spotty.adapter.LocationAdapter;
import com.example.spotty.databinding.CheckinFragmentBinding;
import com.example.spotty.service.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckinFragment extends Fragment {
    private MapViewmodel model;
    private RecyclerView recyclerView;
    private List<Location> dataset;
    private LocationAdapter mAdapter;
    private List<Location> restore;
    private CheckinFragmentBinding binding;



    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = CheckinFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        recyclerView = binding.friendsRecycle;
        dataset = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new LocationAdapter(dataset);
        recyclerView.setAdapter(mAdapter);
        model = ViewModelProviders.of(Objects.requireNonNull(this)).get(MapViewmodel.class);
        restore = new ArrayList<>();
        initSearch(binding.friendsSearchInput);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter.setNavController(Navigation.findNavController(view));
        initRecyleView();
    }

    private void initRecyleView() {
        model.getLocationList().observe(this, (locs) -> {
                    dataset.clear();
                    dataset.addAll(locs);
                    restore.clear();
                    restore.addAll(locs);
                    mAdapter.notifyDataSetChanged();
                    emptyRecycler();
                }
        );
    }

    private void initSearch(EditText input) {
        input.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                emptyRecycler();
                mAdapter.notifyDataSetChanged();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before > count) {
                    dataset.clear();
                    if (s.length() > 0) {
                        dataset.addAll(filter(restore, s.toString()));
                    } else {
                        dataset.addAll(restore);
                    }
                } else if (s.length() > 0) {
                    filter(s.toString());
                }
            }
        });
    }

    private void emptyRecycler() {
        TextView empty = binding.emptyView;
        if (dataset == null || dataset.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }

    private void filter(String filter) {
        final int size = dataset.size();
        for (int i = size - 1; i >= 0; i--) {
            if (!contains(filter, dataset.get(i).name)) {
                dataset.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    private List<Location> filter(List<Location> data, String filter) {
        List<Location> dataset = new ArrayList<>(data);
        final int size = dataset.size();
        for (int i = size - 1; i >= 0; i--) {
            if (!contains(filter, dataset.get(i).name)) {
                dataset.remove(i);
            }
        }
        return dataset;
    }

    private String applyRules(String str) {
        str = str.toLowerCase();
        str = str.replaceAll("\\s+", "");
        return str;
    }

    private boolean contains(String filter, String name) {
        name = applyRules(name);
        filter = applyRules(filter);
        return name.contains(filter);
    }
}
