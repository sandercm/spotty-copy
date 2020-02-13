package com.example.spotty.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.spotty.R;
import com.example.spotty.ViewModels.MapViewmodel;
import com.example.spotty.databinding.SearchCompBinding;
import com.example.spotty.service.model.Location;

import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {
    private SearchCompBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SearchCompBinding.inflate(inflater, container, false);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ListView searchOutput = binding.searchOutput;
        SearchView searchInput = binding.searchInput;

        ArrayAdapter<Location> customAdapter = initAdapter(searchOutput);
        // Set a listener that makes relevant search suggestions appear.
        // Tries to navigate to the most relevant suggestion on pressing enter.
        searchInput.setOnQueryTextListener(initSearch(customAdapter));
    }

    private SearchView.OnQueryTextListener initSearch(ArrayAdapter<Location> customAdapter) {
        return new SearchView.OnQueryTextListener() {
            private List<Location> locations;

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (locations.size() > 0) {
                    NavController host = Navigation.findNavController(getParentFragment().getView());
                    Bundle bundle = new Bundle();
                    bundle.putInt("loc-id", locations.get(0).id);
                    host.navigate(R.id.action_global_spotFragment, bundle);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MapViewmodel model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(MapViewmodel.class);
                if ("".equals(newText)) {
                    customAdapter.clear();
                    return false;
                }
                locations = model.getFilteredLocationList(newText);
                customAdapter.clear();
                // If list is too large, truncate and show only first 6 results.
                if (locations.size() > 6) {
                    customAdapter.addAll(locations.subList(0, 6).toArray(new Location[0]));
                } else {
                    customAdapter.addAll(locations.toArray(new Location[0]));
                }
                return false;
            }
        };
    }

    private ArrayAdapter<Location> initAdapter(ListView searchOutput) {
        // Make an adapter that places locations names into TextViews
        // with the location name that can be clicked to open their spot fragment.
        ArrayAdapter<Location> customAdapter = new ArrayAdapter<Location>(getParentFragment().getActivity(), R.layout.search_item) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                Location location = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
                }
                if (location != null) {
                    TextView textView = convertView.findViewById(R.id.search_item);
                    textView.setText(location.name);
                    textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    textView.setOnClickListener((view1) -> {
                        NavController host = Navigation.findNavController(getParentFragment().getView());
                        Bundle bundle = new Bundle();
                        bundle.putInt("loc-id", location.id);
                        host.navigate(R.id.action_global_spotFragment, bundle);
                    });

                }
                return convertView;
            }
        };
        searchOutput.setAdapter(customAdapter);
        return customAdapter;
    }

}
