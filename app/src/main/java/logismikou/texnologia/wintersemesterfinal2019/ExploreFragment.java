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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.support.constraint.Constraints.TAG;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    FusedLocationProviderClient mFusedLocationProviderClient;

    DatabaseReference databaseReference;

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
            load_shops();

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    public void load_shops(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products")
                .child("Shops");

        final ValueEventListener get_shop_latlong = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    String shop_title = postSnapshot.getKey();
                    String description = postSnapshot.child("Description").getValue().toString();
                    String address = postSnapshot.child("Location")
                            .child("address").getValue().toString();
                    double shop_lat = Double.parseDouble(postSnapshot.child("Location")
                            .child("lat").getValue().toString());
                    double shop_long = Double.parseDouble(postSnapshot.child("Location")
                            .child("long").getValue().toString());

                    add_marker(shop_title, description, address, shop_lat,shop_long );
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(get_shop_latlong);
    }

    public void add_marker(String shop_title, String description, String address,
                           double shop_lat, double shop_long){

        LatLng lat_lng = new LatLng(shop_lat, shop_long);

        MarkerOptions options = new MarkerOptions()
                .position(lat_lng)
                .title(shop_title)
                .snippet(address);
        //.snippet(description);
        mGoogleMap.addMarker(options);
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
