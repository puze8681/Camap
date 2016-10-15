package com.example.toshiba.myapplication;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by TOSHIBA on 2016-08-30.
 */

public class Fragment3 extends Fragment {

    private SharedPreferences pref;

    public Fragment3() {

    }

    // PlaceholderFragment.newInstance() 와 똑같이 추가
    static Fragment3 newInstance(int SectionNumber) {
        Fragment3 fragment = new Fragment3();
        Bundle args = new Bundle();
        args.putInt("section_number", SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    MapView mapView;
    GoogleMap map = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pref = PreferenceManager.getDefaultSharedPreferences(container.getContext());
        loadPathArray();
        loadLocationArray();

        View view = inflater.inflate(R.layout.map, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);


        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);

        loadMarkers();

        return view;
    }



    ArrayList<String> pathArray;
    ArrayList<Double> latArray;
    ArrayList<Double> lonArray;

    private void loadPathArray(){
        Type t = new TypeToken<ArrayList<String>>(){}.getType();
        pathArray = new Gson().fromJson(pref.getString("pathArray", "[]"), t);
    }

    private void loadLocationArray() {
        Type t = new TypeToken<ArrayList<Double>>() {}.getType();
        latArray = new Gson().fromJson(pref.getString("latArray", "[]"), t);
        lonArray = new Gson().fromJson(pref.getString("lonArray", "[]"), t);
    }

    public void loadMarkers() {
        map.clear();
        for (int i = 0; i < pathArray.size(); i++) {



            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bm = BitmapFactory.decodeFile(pathArray.get(i) + "_small", options);

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(latArray.get(i), lonArray.get(i)))
//                    .title("Melbourne")
//                    .snippet("Population: 4,137,400")
                    .icon(BitmapDescriptorFactory.fromBitmap(bm)));
        }
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}