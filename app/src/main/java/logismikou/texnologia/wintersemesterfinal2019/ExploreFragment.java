package logismikou.texnologia.wintersemesterfinal2019;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.support.constraint.Constraints.TAG;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    FusedLocationProviderClient mFusedLocationProviderClient;

    final int REQUEST_CODE = 1234;
    Boolean mLocationPermissionsGranted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        mMapView = v.findViewById(R.id.map);
        getPermissions();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onPause(); //den uphrxe
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    public void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();

                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f);

                    }
                    else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(getActivity(), "Unable to get current location",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocationL: "+ e.getMessage());
        }
    }

    public void moveCamera(LatLng latLng, float zoom){
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void getPermissions(){
        String[] permissions = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION};

        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch(requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }
}
