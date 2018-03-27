package com.uncc.mobileappdev.inclass09;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private CoordinateResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        response = parseMapJson(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for(Points points : response.getPoints()){
            LatLng tracker = new LatLng(points.getLatitude(), points.getLongitude());
            options.add(tracker);
            builder.include(tracker);
        }

        final LatLngBounds bounds = builder.build();

        mMap.addPolyline(options);

        Points pointStart = response.getPoints().get(0);
        LatLng start = new LatLng(pointStart.getLatitude(), pointStart.getLongitude());

        Points pointFinish = response.getPoints().get(response.getPoints().size() - 1);
        LatLng finish = new LatLng(pointFinish.getLatitude(), pointFinish.getLongitude());

        Points pointMiddle = response.getPoints().get(response.getPoints().size() / 2);
        LatLng middle = new LatLng(pointMiddle.getLatitude(), pointMiddle.getLongitude());

        mMap.addMarker(new MarkerOptions().position(start));
        mMap.addMarker(new MarkerOptions().position(finish));
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

    }

    protected CoordinateResponse parseMapJson(Context context){
        CoordinateResponse coordinateResponse = null;
        String json = "";

        try {
            InputStream is = context.getResources().openRawResource(R.raw.trip);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        coordinateResponse = gson.fromJson(json, CoordinateResponse.class);
        Log.d("Response: ", coordinateResponse.toString());

        return coordinateResponse;
    }
}
