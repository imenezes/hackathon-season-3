package example.com.hack_poi.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Metadata;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;

import java.util.ArrayList;
import java.util.List;

import example.com.hack_poi.DataBase.DB_INITIAL;
import example.com.hack_poi.Declaration.DataDeclaration;
import example.com.hack_poi.Declaration.ResponseCodes;
import example.com.hack_poi.Model.InitialDetails;
import example.com.hack_poi.Model.PermissionsRequestor;
import example.com.hack_poi.R;
import example.com.hack_poi.SMS_Receiver;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    Context context;
    private PermissionsRequestor permissionsRequestor;
    EditText et_merchant_Name;
    Button btn_submit_partial;
    String merchant_Name, latitude, longitude;
    private LocationManager locationManager;
    static LocationListener mlocListener;
    static Location location = null;

    private InitialDetails initialDetails;
    private DB_INITIAL db_initial;

    private MapViewLite mapView;
    final List<MapMarker> mapMarkerList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestSMSPermission();
        initialDetails = (InitialDetails) getIntent().getSerializableExtra("InitialDetails");
        initialDetails = new InitialDetails();
        db_initial = new DB_INITIAL();
        context = this;
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        et_merchant_Name = findViewById(R.id.et_merchant_Name);

        btn_submit_partial = findViewById(R.id.btn_submit_partial);
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        handleAndroidPermissions();

        new SMS_Receiver().setEditText(et_merchant_Name);

        btn_submit_partial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation()){
                    submitPartialData();
                }else {
                    Toast toast=Toast.makeText(getApplicationContext(),"Merchant Name Should be appear",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    private boolean Validation() {
        merchant_Name = et_merchant_Name.getText().toString().trim();

        if (merchant_Name.equalsIgnoreCase("")) {
            return false;
        }
        return true;
    }

    private void submitPartialData() {

        initialDetails.setMerchant_name(DataDeclaration.MERCHANT_NAME);
        Log.e("setMerchant_name", initialDetails.getMerchant_name());
        initialDetails.setLatitude(DataDeclaration.LATITUDE.toString());
        initialDetails.setLongitude(DataDeclaration.LONGITUDE.toString());

        if (initialDetails.getMerchant_name() != null){
            long txnid = db_initial.insertData(context, initialDetails);
            initialDetails.setId(txnid + "");
            Log.e(TAG, "count = " + txnid);
            Intent intent = new Intent(MainActivity.this, SubmissionScreen.class);
            intent.putExtra("merchant_name", DataDeclaration.MERCHANT_NAME);
            intent.putExtra("latitude",DataDeclaration.LATITUDE);
            intent.putExtra("longitude", DataDeclaration.LONGITUDE);
            startActivity(intent);
            finish();

        }
    }
    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                loadMapScene();
            }

            @Override
            public void permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.");
            }
        });
    }

    private void loadLocation() {
        /*location = getLocation(locationManager);

        DataDeclaration.LATITUDE = location.getLatitude();
        DataDeclaration.LONGITUDE = location.getLongitude();

        Log.e("latitude", String.valueOf(DataDeclaration.LATITUDE));
        Log.e("longitude", String.valueOf(DataDeclaration.LONGITUDE));*/
    }

    private void addPOIMapMarker(GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.poi);

        MapMarker mapMarker = new MapMarker(geoCoordinates);

        // The bottom, middle position should point to the location.
        // By default, the anchor point is set to 0.5, 0.5.
        MapMarkerImageStyle mapMarkerImageStyle = new MapMarkerImageStyle();
        mapMarkerImageStyle.setAnchorPoint(new Anchor2D(0.5F, 1));

        mapMarker.addImage(mapImage, mapMarkerImageStyle);

        Metadata metadata = new Metadata();
        metadata.setString("key_poi", "This is a POI.");
        mapMarker.setMetadata(metadata);


        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private void loadMapScene() {
        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode sceneError) {
                location = getLocation(locationManager);
                DataDeclaration.LATITUDE = location.getLatitude();
                DataDeclaration.LONGITUDE = location.getLongitude();

                Log.e("latitude", String.valueOf(DataDeclaration.LATITUDE));
                Log.e("longitude", String.valueOf(DataDeclaration.LONGITUDE));

                /*latitude = DataDeclaration.LATITUDE;
                longitude = DataDeclaration.LANGITUDE;*/

                if (sceneError == null) {
                    mapView.getCamera().setTarget(new GeoCoordinates(DataDeclaration.LATITUDE, DataDeclaration.LONGITUDE));
                    mapView.getCamera().setZoomLevel(15);
                } else {
                    Log.d(TAG, "onLoadScene failed: " + sceneError.toString());
                }
                addPOIMapMarker(new GeoCoordinates(DataDeclaration.LATITUDE, DataDeclaration.LONGITUDE));
            }
        });
    }

    public Location getLocation(LocationManager mlocManager) {
        Log.e(TAG, "Reading device location...");
        try {
            long MIN_TIME_BW_UPDATES = 1; // 1 minute

            // The minimum distance to change Updates in meters
            long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

            // mlocManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            mlocListener = new MyLocationListener();

            // getting GPS status
            boolean isGPSEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.e("LocationListener", "No GPS or network available to provide location.");
            } else {

                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("LocationPermission", "Location Permission not given");
                    }
                    mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mlocListener);
                    mlocManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mlocListener, Looper.getMainLooper());
                    Log.e("Network", "Network");
                    if (mlocManager != null) {
                        location = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }else {
                        android.app.AlertDialog.Builder abBuilder = new android.app.AlertDialog.Builder(context);
                        abBuilder.setMessage(ResponseCodes.Response_null + ", Please Try again.");
                        abBuilder.setCancelable(false);
                        abBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        abBuilder.show();
                    }
                    // }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mlocListener);
                        //mlocManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mlocListener, Looper.getMainLooper());
                        Log.e("GPS Enabled", "GPS Enabled");
                        if (mlocManager != null) {
                            location = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }else {
                            android.app.AlertDialog.Builder abBuilder = new android.app.AlertDialog.Builder(context);
                            abBuilder.setMessage(ResponseCodes.Response_null + ", Please Try again.");
                            abBuilder.setCancelable(false);
                            abBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            abBuilder.show();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Location Exception: " + e.getMessage());
        }
        return location;
    }

    private void requestSMSPermission()
    {
        String permission = Manifest.permission.RECEIVE_SMS;

        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED)
        {
            String[] permission_list = new String[1];
            permission_list[0] = permission;

            ActivityCompat.requestPermissions(this, permission_list,1);
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
