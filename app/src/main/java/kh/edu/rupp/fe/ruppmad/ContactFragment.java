package kh.edu.rupp.fe.ruppmad;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * RUPPMAD
 * Created by leapkh on 11/13/17.
 */

public class ContactFragment extends Fragment implements OnMapReadyCallback {

    private final double ruppLat = 11.569028;
    private final double ruppLng = 104.890610;

    private MapView mapView;
    private GoogleMap mapObject;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        mapView = (MapView)rootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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
    public void onMapReady(GoogleMap googleMap) {
        mapObject = googleMap;

        // Move camera to RUPP
        LatLng ruppLatLng = new LatLng(ruppLat, ruppLng);
        CameraUpdate ruppCameraUpdate = CameraUpdateFactory.newLatLngZoom(ruppLatLng, 17);
        mapObject.animateCamera(ruppCameraUpdate);

        // Add marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Royal University of Phnom Penh");
        markerOptions.snippet("RUPP");
        markerOptions.position(ruppLatLng);
        mapObject.addMarker(markerOptions);

    }
}
