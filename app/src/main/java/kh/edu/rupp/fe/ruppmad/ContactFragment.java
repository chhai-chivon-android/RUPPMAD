package kh.edu.rupp.fe.ruppmad;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * RUPPMAD
 * Created by leapkh on 11/13/17.
 */

public class ContactFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private final double ruppLat = 11.569028;
    private final double ruppLng = 104.890610;

    private MapView mapView;
    private GoogleMap mapObject;

    private GoogleApiClient googleApiClient;

    private Marker userMarker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(getActivity());
        builder.addApi(LocationServices.API);
        builder.addConnectionCallbacks(this);
        googleApiClient = builder.build();
        googleApiClient.connect();

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();

        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("rupp", "Granted length: " + grantResults.length);
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            boolean isAllAllowed = true;
            for(int result: grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    isAllAllowed = false;
                    break;
                }
            }
            if(isAllAllowed){
                addCurrentUserMarker();
            } else {
                Toast.makeText(getActivity(), "Some features will not work unless you allow location permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapObject = googleMap;

//        // Move camera to RUPP
        LatLng ruppLatLng = new LatLng(ruppLat, ruppLng);
//        CameraUpdate ruppCameraUpdate = CameraUpdateFactory.newLatLngZoom(ruppLatLng, 17);
//        mapObject.animateCamera(ruppCameraUpdate);

        // Add marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Royal University of Phnom Penh");
        markerOptions.snippet("RUPP");
        markerOptions.position(ruppLatLng);
        mapObject.addMarker(markerOptions);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        addCurrentUserMarker();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void addCurrentUserMarker() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission from user
            String[] permissionsToBeRequested = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissionsToBeRequested, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastKnownLocation != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("You are here");
            LatLng userLatLgn = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLatitude());
            markerOptions.position(userLatLgn);
            userMarker = mapObject.addMarker(markerOptions);
            CameraUpdate ruppCameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLgn, 17);
            mapObject.animateCamera(ruppCameraUpdate);

            // Request location update
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000 * 10);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }else{
            Log.d("rupp", "Last known location not found");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        userMarker.setPosition(currentLocation);
        CameraUpdate ruppCameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 17);
        mapObject.animateCamera(ruppCameraUpdate);
    }
}
