package drucc.sittichok.heyheybread;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng ltgMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    public void onBackPressed(){
        finish();
    }

    public void ClickBottomMap(View view) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMap = mapFragment.getMap();
        mMap.setMyLocationEnabled(true);

        ltgMap = new LatLng(13.604760, 100.614848);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(ltgMap).zoom(15).bearing(270).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }   // ClickButtomMap


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera


        LatLng sydney = new LatLng(13.604760, 100.614848);
        mMap.addMarker(new MarkerOptions().position(sydney).title("ร้านปังเว้ยเฮ้ย สาขาศรีนครินทร์-ปากน้ำ").snippet("(เปิดบริการ 7:00 - 19:00 น.)"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(15).bearing(270).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }


}
