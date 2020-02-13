package com.example.spotty.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotty.R;
import com.example.spotty.ViewModels.MapViewmodel;
import com.example.spotty.ViewModels.SettingsViewModel;
import com.example.spotty.ViewModels.SpotViewmodel;
import com.example.spotty.ViewModels.UserProfileViewModel;
import com.example.spotty.adapter.LocationFriendsAdapter;
import com.example.spotty.databinding.SpotFragmentBinding;
import com.example.spotty.service.model.LocationDataWeek;
import com.example.spotty.service.model.OpeningHours;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class SpotFragment extends Fragment {
    private MapViewmodel model;
    private SpotViewmodel spotViewmodel;
    private Map<String, List<DataPointWithTimeStamp>> week = new TreeMap<>();
    private GraphView graph;
    private UserProfileViewModel usermodel;
    private SettingsViewModel settingsViewModel;
    private SpotFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SpotFragmentBinding.inflate(inflater, container, false);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get xml elements
        graph = binding.graph;
        TextView tv = binding.locationText;
        TextView adress = binding.adressText;
        Spinner spinner = binding.spinner;
        ImageView iv = binding.imageView;
        usermodel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        //Check the arguments
        assert getArguments() != null;
        //Set locationid
        int locationid = getArguments().getInt("loc-id");

        //This has no need to be internationalized
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateToNumber = new SimpleDateFormat("u");
        //Set picture en text
        model.getPictureById(locationid, getResources()).observe(this, image -> iv.setImageBitmap(image.bitmap));
        model.getLocationInfo(locationid).observe(this, x -> {
            tv.setText(x.name);
            List<OpeningHours> opening_hours_json = x.opening_hours;
            String opening_hours = getString(R.string.unknown_opening_hours);
            if (opening_hours_json != null && opening_hours_json.size() > 0)
                opening_hours = opening_hours_json.get(opening_hours_json.indexOf(new OpeningHours(Integer.parseInt(dateToNumber.format(new Date()))))).hours;
            adress.setText(getString(R.string.spot_fragment_text, x.street, x.number, x.municipality, x.postcode, opening_hours));
        });

        //init graph
        graphInit();

        //init spinner
        initSpinnerClick(spinner);

        // get the week and put in the treemap
        getWeek(spinner, spotViewmodel.getLocationCrowdednessWeek(locationid));

        //init checkin button
        Button button = binding.checkinButton;
        initCheckinButton(button, locationid);
        Button refreshButton = binding.refreshButton;
        refreshButton.setOnClickListener(click -> getWeek(spinner, spotViewmodel.getLocationCrowdednessWeek(locationid)));

        //init recycle view
        initRecyleView(locationid);
    }

    private void initCheckOutButton(Button button, int locationId) {
        //enable only if user is logged in

        if (usermodel.isLoggedIn(getContext())) {
            button.setEnabled(true);
            button.setText(getString(R.string.CheckOut));
            button.setOnClickListener((click) -> {
                button.setEnabled(false);
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.CheckOut))
                        .setMessage(getString(R.string.check_out_are_yuo_sure))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(getString(R.string.CheckOut), (DialogInterface dialog, int which) -> {
                            usermodel.checkUserOut(getContext());
                            initCheckinButton(button, locationId);
                        })
                        .setNegativeButton(getString(R.string.cancel), (DialogInterface dialog, int which) -> {
                            dialog.cancel();
                            button.setEnabled(true);
                        })
                        .show();
            });
        } else {
            button.setEnabled(false);
        }
    }

    private void initCheckinButton(Button button, int locationId) {
        //enable only if user is logged in
        if (usermodel.isLoggedIn(getContext())) {
            button.setEnabled(true);
            button.setText(getString(R.string.check_in));
            if (!usermodel.isCheckedIn(getContext(), locationId)) {
                button.setOnClickListener((click) -> {
                    button.setEnabled(false);
                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.check_in))
                            .setMessage(getString(R.string.location_check_in))
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton(getString(R.string.check_in), (DialogInterface dialog, int which) -> {
                                usermodel.checkUserIn(getContext(), locationId);
                                initCheckOutButton(button, locationId);
                            })
                            .setNegativeButton(getString(R.string.cancel), (DialogInterface dialog, int which) -> {
                                dialog.cancel();
                                button.setEnabled(true);
                            })
                            .show();
                });
            } else {
                initCheckOutButton(button, locationId);
            }
        } else {
            button.setEnabled(false);
        }

    }

    private void initSpinnerClick(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drawGraph((parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // sometimes you need nothing here
            }
        });
    }

    private void getWeek(Spinner spinner, LiveData<List<LocationDataWeek>> listLiveData) {
        Locale defaultLocale = Locale.getDefault();
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(defaultLocale, "EEEE d MMMM");
        SimpleDateFormat bestDateTimeFormat = new SimpleDateFormat(bestDateTimePattern, defaultLocale);
        SimpleDateFormat serverDateFormat = new SimpleDateFormat("dd/MM/yyyy", defaultLocale);

        listLiveData.observe(this, list -> {
            week = new TreeMap<>();
            //Steek alles in een map, zodat makkelijk dagen opvraagbaar zijn + keys voor spinner
            for (LocationDataWeek item : list) {
                try {
                    String key = bestDateTimeFormat.format(serverDateFormat.parse(item.day));
                    if (!week.containsKey(key)) {
                        week.put(key, new ArrayList<>());
                    }
                    List<DataPointWithTimeStamp> datapoints = week.get(key);
                    datapoints.add(new DataPointWithTimeStamp(item));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            List<String> weekdayList = new ArrayList<>(week.keySet());
            // sort the keys based on their inferred dates
            Collections.sort(weekdayList, (a, b) -> {
                try {
                    return bestDateTimeFormat.parse(a).compareTo(bestDateTimeFormat.parse(b));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return -1;
                }
            });
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    Objects.requireNonNull(getActivity()),
                    R.layout.support_simple_spinner_dropdown_item,
                    weekdayList
            );
            spinner.setAdapter(adapter);
            spinner.setSelection(weekdayList.size() - 1);
        });

    }

    private void graphInit() {
        //set graph settings
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        model = ViewModelProviders.of(Objects.requireNonNull(this)).get(MapViewmodel.class);
        spotViewmodel = ViewModelProviders.of(Objects.requireNonNull(this)).get(SpotViewmodel.class);
    }

    private String toTime(int x) {
        int hour = x / 6;
        int minute = (x % 6) * 10;
        String time = String.format(getString(R.string.time_format_hhmm), hour, minute);
        String retVal = "";
        try {
            Locale defaultLocale = Locale.getDefault();
            String bestTimePattern = DateFormat.getBestDateTimePattern(defaultLocale, "HH:mm");
            SimpleDateFormat bestTimeFormat = new SimpleDateFormat(bestTimePattern, defaultLocale);
            SimpleDateFormat parseFormat = new SimpleDateFormat("H:m", defaultLocale);
            retVal = bestTimeFormat.format(parseFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    private void fadeIn(Spinner spinner, TextView people) {

        if (graph.getAlpha() == 0f) {
            graph.animate().alpha(1f).setDuration(500);
        }
        if (spinner.getAlpha() == 0f) {
            spinner.animate().alpha(1f).setDuration(500);
        }
        if (people.getAlpha() == 0f) {
            people.animate().alpha(1f).setDuration(500);
        }
    }

    private void show(Spinner spinner, TextView people) {
        if (graph.getAlpha() == 0f) {
            graph.setAlpha(1f);
        }
        if (spinner.getAlpha() == 0f) {
            spinner.setAlpha(1f);
        }
        if (people.getAlpha() == 0f) {
            people.setAlpha(1f);
        }
    }

    private void drawGraph(String key) {
        TextView people = binding.peopleText;
        Spinner spinner = binding.spinner;
        if (settingsViewModel.getAnimate(getContext())) {
            fadeIn(spinner, people);
        } else {
            show(spinner, people);
        }
        graph.removeAllSeries();
        LineGraphSeries lineGraphSeries = new LineGraphSeries<>(week.get(key).toArray(new DataPointWithTimeStamp[0]));
        graph.addSeries(lineGraphSeries);
        graph.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                return isValueX ? toTime((int) value) : (int) value + "";
            }

            @Override
            public void setViewport(Viewport viewport) {
                viewport.setMinX(lineGraphSeries.getLowestValueX());
                viewport.setMaxX(lineGraphSeries.getHighestValueX());
                viewport.setMinY(lineGraphSeries.getLowestValueY());
                viewport.setMaxY(lineGraphSeries.getHighestValueY());
            }
        });

        lineGraphSeries.setOnDataPointTapListener((series, dataPoint) -> {
            int quantity = (int) dataPoint.getY();
            String timeStamp = ((DataPointWithTimeStamp) dataPoint).getTimeStamp();
            people.setText(getResources().getQuantityString(R.plurals.graph_string_extended, quantity, quantity, timeStamp));
        });
    }

    private void initRecyleView(int id) {
        RecyclerView recyclerView = binding.recyclerView;
        TextView textView = binding.friendsAtLocation;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        TextView empty = binding.emptyView;
        if (!usermodel.isLoggedIn(getContext())) {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
        }
        usermodel.getFriendsInLocation(id).observe(this, (friends) -> {
                    if (!usermodel.isLoggedIn(getContext())) {
                        recyclerView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        empty.setVisibility(View.INVISIBLE);
                    } else if (friends == null || friends.size() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.GONE);
                        LocationFriendsAdapter mAdapter = new LocationFriendsAdapter(friends);
                        recyclerView.swapAdapter(mAdapter, false);
                    }
                }
        );
    }

    private class DataPointWithTimeStamp extends DataPoint {
        private LocationDataWeek locationDataWeek;

        private DataPointWithTimeStamp(LocationDataWeek locationDataWeek) {
            super(((locationDataWeek.hour) * 6) + (locationDataWeek.minute / 10), locationDataWeek.people);
            this.locationDataWeek = locationDataWeek;
        }

        public String getTimeStamp() {
            return String.format(getString(R.string.time_format_hhmm), locationDataWeek.hour, locationDataWeek.minute);
        }
    }
}