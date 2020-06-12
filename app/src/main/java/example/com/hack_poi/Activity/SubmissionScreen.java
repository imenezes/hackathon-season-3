package example.com.hack_poi.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import example.com.hack_poi.DataBase.DB_FULL_DETAILS;
import example.com.hack_poi.Declaration.DataDeclaration;
import example.com.hack_poi.Model.FinalDetails;
import example.com.hack_poi.R;

public class SubmissionScreen extends AppCompatActivity {
    private static final String TAG = SubmissionScreen.class.getSimpleName();
    Context context;
    EditText et_merchant_Name,et_MerchantNumber,et_Time,ed_rating;
    Spinner spn_type;
    String merchantName = "",merchantNumber = "",Time = "",type = "",rating = "";
    Double lat,longitude ;
    String LatLong = "";
    Button btn_submit;

    private MapViewLite mapView;
    final List<MapMarker> mapMarkerList = new ArrayList<>();

    private FinalDetails finalDetails;
    private DB_FULL_DETAILS db_full_details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_screen);

        finalDetails = new FinalDetails();
        db_full_details = new DB_FULL_DETAILS();
        context = this;

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        et_merchant_Name = findViewById(R.id.et_merchant_Name);
        et_MerchantNumber = findViewById(R.id.et_MerchantNumber);
        et_Time = findViewById(R.id.et_Time);
        ed_rating = findViewById(R.id.ed_rating);

        spn_type = findViewById(R.id.spn_type);

        if (getIntent().getExtras() != null) {
            merchantName = getIntent().getExtras().getString("merchant_name");
            lat = getIntent().getExtras().getDouble("latitude");
            longitude = getIntent().getExtras().getDouble("longitude");
            LatLong = lat + ", "+longitude;
            et_merchant_Name.setText(merchantName);
        }
        loadMapScene();
        btn_submit = findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation()){
                    submit();
                }else {
                    Toast toast=Toast.makeText(getApplicationContext(),"Fill the data properly.",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void loadMapScene() {
        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode sceneError) {

                Log.e("latitude", lat.toString());
                Log.e("longitude", longitude.toString());

                /*latitude = DataDeclaration.LATITUDE;
                longitude = DataDeclaration.LANGITUDE;*/

                if (sceneError == null) {
                    mapView.getCamera().setTarget(new GeoCoordinates(lat, longitude));
                    mapView.getCamera().setZoomLevel(15);
                } else {
                    Log.d(TAG, "onLoadScene failed: " + sceneError.toString());
                }
                addPOIMapMarker(new GeoCoordinates(lat, longitude));
            }
        });
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

    private void submit() {
        finalDetails.setMerchant_name(merchantName);
        finalDetails.setLat_long(LatLong);
        finalDetails.setMobileNumber(merchantNumber);
        finalDetails.setTime(Time);
        finalDetails.setType(type);
        finalDetails.setRating(rating);

        if (finalDetails.getMerchant_name() != null){
            long txnid = db_full_details.insertData(context, finalDetails);
            finalDetails.setId(txnid + "");
            Intent intent = new Intent(SubmissionScreen.this, ReportActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean Validation() {
        merchantNumber = et_MerchantNumber.getText().toString().trim();
        Time = et_Time.getText().toString().trim();
        rating = ed_rating.getText().toString().trim();

        type = spn_type.getSelectedItem().toString();

        if (type.equalsIgnoreCase("Select Device")) {
            return false;
        }else if (merchantNumber.equalsIgnoreCase("")) {
            return false;
        } else if (Time.equalsIgnoreCase("")) {
            return false;
        } else if (rating.equalsIgnoreCase("")) {
            return false;
        }
        return true;
    }
}
