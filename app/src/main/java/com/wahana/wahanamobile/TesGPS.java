package com.wahana.wahanamobile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Guillaume Agis
 * 24 Mars 2015
 *
 * Extending from geolocationActivity provide the user to get his location in real time
 * if he is connected on wifi/3G
 * The callback "onLocationChanged" is called everytimes a new location has been found
 */
public class TesGPS extends Activity implements LocationListener {

    private final String TAG = "Geolocation";
    protected Double _longitude;
    protected Double _latitude;

    /**
     * System location services
     */
    private LocationManager _locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocation();
    }

    public void getLocation()
    {
        //init
        _locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // if there is a GPS provider, request the location of the user
        if (_locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        // if there is a Network provider, request the location of the user
        if (_locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        // Ask for update
        Location  mobileLocation = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mobileLocation != null)
        {
            onLocationChanged(mobileLocation);
        }

//        Location  netLocation = _locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        if (netLocation != null)
//        {
//            onLocationChanged(netLocation);
//        }
    }


    /**
     * Location found , get the new location of the user
     * @param userLocation user's location
     */
    @Override
    public void onLocationChanged(Location userLocation) {


        if (userLocation == null)
        {
            Log.e(TAG, "NO Internet");
            return;
        }

        _longitude = Double.valueOf(userLocation.getLongitude());
        //Log.v(TAG, longitude);
        _latitude = Double.valueOf(userLocation.getLatitude());
        // Log.v(TAG, latitude);

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;


        try {
            addresses = gcd.getFromLocation(userLocation.getLatitude(), userLocation.getLongitude(), 1);
            if (addresses.size() > 0)
                cityName = addresses.get(0).getLocality();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String fullLocation = "Longitude : " + userLocation.getLongitude() + "\nLatitude : " + userLocation.getLatitude() + "\n\nMy Current City is: "  + cityName;
//        Log.e(TAG, "location : " + fullLocation);

        Toast.makeText(TesGPS.this, fullLocation,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "provider disabled : " + provider);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG,"provider enabled : " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "status (" +status + ") changed : " + provider);
    }

    protected void onPause() {
        if (_locationManager != null)
        {
            _locationManager.removeUpdates(this);
            _locationManager = null;
        }
        super.onPause();
    }

    protected void onResume() {
        // ask for location once app resumed
        getLocation();
        super.onResume();
    }

    public void onDestroy() {
        if (_locationManager != null)
        {
            // clean services
            _locationManager.removeUpdates(this);
            _locationManager = null;
        }
        super.onDestroy();
        finish();
    }
}