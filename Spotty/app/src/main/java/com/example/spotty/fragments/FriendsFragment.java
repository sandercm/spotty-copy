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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotty.ViewModels.UserProfileViewModel;
import com.example.spotty.adapter.FriendsAdapter;
import com.example.spotty.databinding.FriendsViewBinding;
import com.example.spotty.service.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendsFragment extends Fragment {
    private UserProfileViewModel model;
    private RecyclerView recyclerView;
    private List<User> dataset;
    private FriendsAdapter mAdapter;
    private List<User> users;
    private List<User> restore;
    private FriendsViewBinding binding;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FriendsViewBinding.inflate(inflater, container, false);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        dataset = new ArrayList<>();
        users = new ArrayList<>();
        recyclerView =  binding.friendsRecycle;
        model = ViewModelProviders.of(Objects.requireNonNull(this)).get(UserProfileViewModel.class);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new FriendsAdapter(dataset, model);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        initSearch(binding.friendsSearchInput);
        restore = new ArrayList<>();
        initUsers();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyleView();
        emptyRecycler();
    }

    private void initSearch(EditText input) {
        input.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                mAdapter.notifyDataSetChanged();
                emptyRecycler();
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
                }
                if (s.length() > 0) {
                    //haal de gewone users er weer uit om overlap met friends te vermijden
                    dataset.clear();
                    //restore bevat enkel friends
                    dataset.addAll(filter(restore, s.toString()));
                    if (nofriends(dataset)) {
                        dataset.clear();
                        if (users != null) {
                            dataset.addAll(filter(users, s.toString()));
                        }
                    }
                }
            }
        });
    }

    private boolean nofriends(List<User> dataset) {
        int i = dataset.size() - 1;
        while (i > -1) {
            if (dataset.get(i).isFriend == 1) return false;
            i--;
        }
        return true;
    }

    private void initUsers() {
        model.getUsers().observe(this, (users) -> {
            this.users.clear();
            this.users.addAll(users);
        });
    }

    private List<User> filter(List<User> data, String filter) {
        List<User> dataset = new ArrayList<>(data);
        final int size = dataset.size();
        for (int i = size - 1; i >= 0; i--) {
            if (!contains(filter, dataset.get(i).mail)) {
                dataset.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
        return dataset;
    }

    /**
     * Give data to adpater and give it to the recycleview or give an no friends found warning
     */
    private void initRecyleView() {
        model.getFriends().observe(this, (friends) -> {
                    dataset.clear();
                    dataset.addAll(friends);
                    mAdapter.notifyDataSetChanged();
                    restore.clear();
                    restore.addAll(friends);
                    emptyRecycler();
                }
        );
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
}
