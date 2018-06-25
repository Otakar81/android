package com.bobo.iamhere;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.LocationDao;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Aggiungo i listener sugli eventi OnClick alla mappa
        addListenersOnMap();

        //Aggiungo i marker relativi ai luoghi "memorabili" precedentemente salvati
        addMarkersOnMap();

        //Avvio il tracking della posizione utente sulla mappa
        startListening();


        //E centro la mappa nell'ultima posizione nota
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Se arrivo da un'altra activity, centro la mappa sulle coordinate che mi sono state passate
            Intent intent = getIntent();
            double latitudine = intent.getDoubleExtra("latitudine", -1);
            double longitudine = intent.getDoubleExtra("longitudine", -1);

            if(latitudine != -1 && longitudine != -1)
            {
                LatLng posizioneCoordinate = new LatLng(latitudine, longitudine);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneCoordinate, 15)); //Zoom della camera. Va da 1 (il mondo) a 20

            }else{ //Altrimenti centro la posizione sull'utente

                Location lastKnowLocation = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastKnowLocation != null)
                {
                    LatLng posizioneUtente = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneUtente, 15)); //Zoom della camera. Va da 1 (il mondo) a 20
                }
            }

            mMap.setMyLocationEnabled(true);
        }

    }


    private void startListening()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            MainActivity.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, MainActivity.locationListener);
    }

    /***
     * Aggiunge i listener alla mappa
     */
    private void addListenersOnMap()
    {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            @Override
            public void onMapClick(LatLng point) {
                //Nulla (per ora)
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                //Utilizzo il Geocoder per ottenere info sugli indirizzi in zona
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {


                    List<Address> listaIndirizzi = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); //Voglio un solo risultato


                    if(listaIndirizzi != null && listaIndirizzi.size() > 0)
                    {
                        Address indirizzo = listaIndirizzi.get(0);

                        //Recupero le info della zona
                        String country = indirizzo.getCountryName();
                        String locality = indirizzo.getLocality();
                        String adminArea = indirizzo.getAdminArea();
                        String subAdminArea = indirizzo.getSubAdminArea();
                        String postalCode = indirizzo.getPostalCode();
                        String indirizzoPostale = indirizzo.getThoroughfare();

                        //Salvo sul database il luogo selezionato
                        LocationDao locationDao = new LocationDao(latLng.latitude, latLng.longitude, country, adminArea, subAdminArea, locality, postalCode, indirizzoPostale);
                        DatabaseManager.insertLocation(MainActivity.database, locationDao);

                        //Aggiungo il marker sulla mappa
                        mMap.addMarker(new MarkerOptions().position(latLng).title(locationDao.toString()));

                        Toast.makeText(getApplicationContext(), "Luogo aggiunto", Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /***
     * Aggiunge i marker relativi ai luoghi memorabili salvati precedentemente su DB
     */
    private void addMarkersOnMap()
    {
        //Recupero la lista del luoghi salvati dal database
        ArrayList<LocationDao> elencoLuoghi = DatabaseManager.getAllLocation(MainActivity.database);

        for (LocationDao location:elencoLuoghi)
        {
            LatLng latLng = new LatLng(location.getLatitudine(), location.getLongitudine());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location.toString()));
        }
    }
}
