package com.example.spotty;

import com.example.spotty.service.model.Location;
import com.example.spotty.service.model.LocationDataDay;
import com.example.spotty.service.model.LocationDataWeek;
import com.example.spotty.service.model.User;
import com.example.spotty.service.repository.ServerService;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServerServiceTest {
    private ServerService serverService;
    private int location_id = 1;
    private String testmail = "Test";
    private String testpw = "testmail";

    @Before
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerService.HTTPS_LOCATION_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serverService = retrofit.create(ServerService.class);
    }

    @Test
    public void test00NetworkCheck() throws Exception {
        assertNull("Something went wrong connecting to the server.", serverService.testNetwork().execute().errorBody());
    }

    @Test
    public void test01GetLocationList() throws Exception {
        List<Location> locs = serverService.getLocations().execute().body();
        assertNotNull(locs);
        assertNotEquals(0, locs.size());
        System.out.println("the current locations are: ");
        System.out.println(locs);
    }

    @Test
    public void test02GetLocationDataDay() throws Exception {
        List<LocationDataDay> datas = serverService.getLocationDayData(location_id).execute().body();
        assertNotNull(datas);
        assertNotEquals(0, datas.size());
        System.out.println("the current crowdynesses are: ");
        System.out.println(datas);
    }

    @Test
    public void test03GetLocationDataWeek() throws Exception {
        List<LocationDataWeek> datas = serverService.getLocationWeekData(location_id).execute().body();
        assertNotNull(datas);
        assertNotEquals(0, datas.size());
        System.out.println("the current crowdynesses are: ");
        System.out.println(datas);
    }

    @Test
    public void test04GetAllUsers() throws Exception {
        List<User> users = serverService.getAllUsers().execute().body();
        assertNotNull(users);
        assertNotEquals(0, users.size());
        System.out.println("all users are: ");
        System.out.println(users);
    }
}