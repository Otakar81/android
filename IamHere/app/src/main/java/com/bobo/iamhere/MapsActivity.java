package com.bobo.iamhere;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.LocationDao;
import com.bobo.iamhere.utils.PermissionUtils;
import com.bobo.iamhere.ws.google.PlaceDao;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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


        //Ricerca delle location
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18.0f));
            }

            @Override
            public void onError(Status status) {

            }
        });
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

        //Recupero le informazioni eventualmente passate dalle altre activity, se sono arrivato qui dopo essere stato chiamato
        Intent intent = getIntent();
        double latitudine = intent.getDoubleExtra("latitudine", -1); //Se arrivo da un'altra activity, centro la mappa sulle coordinate che mi sono state passate
        double longitudine = intent.getDoubleExtra("longitudine", -1);

        boolean mostraPostiInteressanti = intent.getBooleanExtra("mostra_posti_interessanti", false); //Se true, mi aspetto che vengano aggiunti anche i marker relativi ai posti segnalati da google places
        boolean inserisciMarker = intent.getBooleanExtra("inserisci_marker", false); //Chiedo di aggiungere un marker nella posizione corrente

        //Aggiungo i listener sugli eventi OnClick alla mappa
        addListenersOnMap();

        //Aggiungo i marker relativi ai luoghi "memorabili" precedentemente salvati
        addMarkersOnMap(mostraPostiInteressanti);

        //Avvio il tracking della posizione utente sulla mappa
        startListening();


        //E centro la mappa nell'ultima posizione nota
        if ((Build.VERSION.SDK_INT < 23) || //Sulle vecchie versioni non si deve chiedere il permesso
                (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            if(latitudine != -1 && longitudine != -1)
            {
                LatLng posizioneCoordinate = new LatLng(latitudine, longitudine);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneCoordinate, 15)); //Zoom della camera. Va da 1 (il mondo) a 20

                //Se richiesto, metto anche un marker nella posizione corrente
                if(inserisciMarker)
                {
                    String nomeLuogo = intent.getStringExtra("nome_luogo");

                    LatLng latLng = new LatLng(latitudine, longitudine);
                    mMap.addMarker(new MarkerOptions().position(latLng).title(nomeLuogo).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }


            }else{ //Altrimenti centro la posizione sull'utente

                Location lastKnowLocation = MainActivity.getLastKnownLocation(this); //MainActivity.locationManager.getLastKnownLocation(MainActivity.getLocationProviderName());

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
            MainActivity.locationManager.requestLocationUpdates(MainActivity.getLocationProviderName(), SettingsActivity.getMinTime(), SettingsActivity.getMinDistance(), MainActivity.locationListener);
    }

    /**
     * Ottiene l'ultima posizione conosciuta
     * @return

    private Location getLastKnownLocation()
    {
        Location lastKnowLocation = null;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            lastKnowLocation = MainActivity.locationManager.getLastKnownLocation(MainActivity.getLocationProviderName());

            if(lastKnowLocation == null)
                lastKnowLocation = MainActivity.locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return lastKnowLocation;
    }

    */

    /***
     * Aggiunge i listener alla mappa
     */
    private void addListenersOnMap()
    {
        /*
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            @Override
            public void onMapClick(LatLng point) {
                //Nulla (per ora)
            }
        });
        */

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

                        //Alcune di queste info potrebbero essere NULL. Le gestisco
                        if(country == null)
                            country = "";

                        if(locality == null)
                            locality = "";

                        if(adminArea == null)
                            adminArea = "";

                        if(subAdminArea == null)
                            subAdminArea = "";

                        if(postalCode == null)
                            postalCode = "";

                        if(indirizzoPostale == null)
                            indirizzoPostale = "";

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
    private void addMarkersOnMap(boolean mostraPostiInteressanti)
    {
        //Recupero la lista del luoghi salvati dal database
        ArrayList<LocationDao> elencoLuoghi = DatabaseManager.getAllLocation(MainActivity.database, false);

        for (LocationDao location:elencoLuoghi)
        {
            LatLng latLng = new LatLng(location.getLatitudine(), location.getLongitudine());

            if(location.getLuogoPreferito() == 1)
                mMap.addMarker(new MarkerOptions().position(latLng).title(location.toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            else
                mMap.addMarker(new MarkerOptions().position(latLng).title(location.toString()));
        }

        //Se richiesto, stampo anche i marker relativi ai posti segnalati da google places
        if(mostraPostiInteressanti)
        {
            ArrayList<PlaceDao> elencoPostiInteressanti = GooglePlacesActivity.elencoPostiInteressanti;

            if(elencoPostiInteressanti != null)
            {
                for (PlaceDao place: elencoPostiInteressanti)
                {
                    LatLng latLng = new LatLng(place.getLatitudine(), place.getLongitudine());
                    String uriIcon = place.getIcon();
                    Bitmap icon = getBitmapFromURL(uriIcon); //Recupero la bitmat dall'url

                    if(icon != null)
                        mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()).icon(BitmapDescriptorFactory.fromBitmap(icon)));
                    else
                        mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }
        }
    }


    private Bitmap getBitmapFromURL(String imageUrl) {

        try{

            //Faccio il parsing dell'url in modo da estrarne il nome file
            String nomeFile = "google_place_" + imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
            nomeFile = nomeFile.replaceAll("-", "_");

            //Recupero l'ID dell'immagine
            int resID = getResources().getIdentifier(nomeFile, "drawable",  getPackageName());

            //Se il resID = 0, vuol dire che non ho trovato l'immagine. In quel caso userò l'icona di default
            if(resID == 0)
                resID = getResources().getIdentifier("google_place_generic_business_71", "drawable",  getPackageName());

            //Recupero la bitmap e la restituisco
            Bitmap icon = BitmapFactory.decodeResource(getResources(), resID); //Recupero la bitmap dell'icona
            icon = Bitmap.createScaledBitmap(icon, 40, 40, false); //La rimpicciolisco

            return icon; //La restituisco

        }catch (Exception e)
        {
            return null;
        }


        /*
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            //Faccio resize della bitmap, che è troppo grossa
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 50, 50, false);

            return myBitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        */
    }
}
