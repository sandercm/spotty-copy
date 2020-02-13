package com.example.spotty;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.example.spotty.ViewModels.ConnectionViewModel;
import com.example.spotty.ViewModels.UserProfileViewModel;
import com.example.spotty.fragments.CheckinFragment;
import com.example.spotty.fragments.FriendsFragment;
import com.example.spotty.fragments.LoginFragment;
import com.example.spotty.fragments.RegisterFragment;
import com.example.spotty.fragments.SettingsFragment;
import com.example.spotty.fragments.SpottyMapFragment;
import com.example.spotty.service.model.User;
import com.example.spotty.service.repository.LocalPreferencesRepository;
import com.example.spotty.service.repository.ServerRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class MainActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavHostFragment host;
    private UserProfileViewModel model;
    private ConnectionViewModel connectionViewModel;
    private UserProfileViewModel userProfileViewModel;
    private LocalPreferencesRepository localPreferencesRepository;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private boolean loggedin;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Firebase setup
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_maps);
        host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        model = ViewModelProviders.of(Objects.requireNonNull(this)).get(UserProfileViewModel.class);
        connectionViewModel = ViewModelProviders.of(Objects.requireNonNull(this)).get(ConnectionViewModel.class);
        userProfileViewModel = ViewModelProviders.of(Objects.requireNonNull(this)).get(UserProfileViewModel.class);
        localPreferencesRepository = LocalPreferencesRepository.getInstance(ServerRepository.getInstance());
        drawerLayout = findViewById(R.id.activity_map);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        setUpMenuLayout(navigationView, bottomNavigationView);
        setDestinationListener(bottomNavigationView);
        setNavigationViewListners(navigationView, bottomNavigationView);
    }

    public void drawerToglle() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(Gravity.START, true);
        }
    }

    /**
     * Setup drawer_indicator layout depending user state
     *
     * @param navigationView       drawer
     * @param bottomNavigationView bottom nav
     */
    private void setUpMenuLayout(NavigationView navigationView, BottomNavigationView bottomNavigationView) {
        if (model.getUser().getValue() == null) {
            User user = localPreferencesRepository.getUser(this);
            if (user != null) {
                ServerRepository.getInstance().setLoggedInUser(user, this);
            }
        }
        model.getUser().observe(this, user -> {
            if (user != null) {
                // log in
                loggedin = true;
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.drawer_items_logged_in);
                bottomNavigationView.getMenu().clear();
                bottomNavigationView.inflateMenu(R.menu.navigation_loggedin);
            } else {
                //log out
                loggedin = false;
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.drawer_items_not_logged_in);
                bottomNavigationView.getMenu().clear();
                bottomNavigationView.inflateMenu(R.menu.navigation);
            }
        });
        // uses firebase for user login check
    }

    /**
     * Do custom instructions when on specified Destinations.
     * mostly to make the bottom nav disappear
     *
     * @param bottomNavigationView bottom navigation
     */
    private void setDestinationListener(BottomNavigationView bottomNavigationView) {
        host.getNavController().addOnDestinationChangedListener((@NonNull NavController controller,
                                                                 @NonNull NavDestination destination,
                                                                 @Nullable Bundle arguments) -> {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
                    if (destination.getId() == R.id.splash || destination.getId() == R.id.offline) {
                        bottomNavigationView.setVisibility(View.GONE);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    } else {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                }
        );
    }

    /**
     * Internet check
     */
    public void startInternetCheck() {
        connectionViewModel.isConnected().observe(this, (connected) -> {
            if (!connected) host.getNavController().navigate(R.id.action_global_offline);
        });
    }

    /**
     * Set their listeners to navigate
     *
     * @param navigationView       navigation view
     * @param bottomNavigationView bottom navigation
     */
    private void setNavigationViewListners(NavigationView navigationView, BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Override of on back pressed, close drawers if needed
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /**
     * helper to abstract the navigation listener
     *
     * @param targetFragment      id of layout
     * @param targetFragmentclass target class
     */
    private void navigationHelper(int targetFragment, Class targetFragmentclass) {
        boolean state = false;
        for (Fragment frag : host.getChildFragmentManager().getFragments()) {
            if (targetFragmentclass.isInstance(frag)) {
                state = true;
                break;
            }
        }
        if (!state) {
            host.getNavController().navigate(targetFragment);
        }
    }

    /**
     * navigate to home
     */
    public void moveToHome() {
        boolean state = false;
        for (Fragment frag : host.getChildFragmentManager().getFragments()) {
            if (frag instanceof SpottyMapFragment) {
                state = true;
                break;
            }
        }
        if (!state) {
            host.getNavController().popBackStack(R.id.spottyMapFragment, false);
        }
    }

    /**
     * when a drawer_indicator item has been clicked either in bottomnav or drawer nav, navigate
     *
     * @param menuItem The item in the drawer_indicator that was clicked
     * @return success
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawers();
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.navigation_home:
            case R.id.drawer_nav_home:
                moveToHome();
                break;
            case R.id.navigation_settings:
                navigationHelper(R.id.action_global_settings, SettingsFragment.class);
                break;
            case R.id.navigation_friends:
                if (loggedin) {
                    navigationHelper(R.id.action_global_friendsFragment, FriendsFragment.class);
                } else {
                    navigationHelper(R.id.action_global_loginFragment, LoginFragment.class);
                }
                break;
            case R.id.navigation_checkin:
                if (loggedin) {
                    navigationHelper(R.id.checkin_fragment, CheckinFragment.class);
                } else {
                    navigationHelper(R.id.action_global_loginFragment, LoginFragment.class);
                }
                break;
            case R.id.drawer_nav_log_out:
                userProfileViewModel.logOut(this);
                navigationHelper(R.id.action_global_spottyMapFragment, SpottyMapFragment.class);
                break;
            case R.id.navigation_login:
            case R.id.drawer_nav_log_in:
                navigationHelper(R.id.action_global_loginFragment, LoginFragment.class);
                break;
            case R.id.drawer_nav_settings:
                navigationHelper(R.id.action_global_settings, SettingsFragment.class);
                break;
            case R.id.drawer_register:
                navigationHelper(R.id.action_global_registerFragment, RegisterFragment.class);
                break;
        }
        return true;
    }

    public void exit(View view) {
        this.finish();
    }
}
