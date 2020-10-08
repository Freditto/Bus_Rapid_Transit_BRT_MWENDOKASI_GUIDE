package com.example.fredherbert.brt;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.arsy.maps_library.MapRipple;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    AppLocationService appLocationService;

    TextView routeNote, routeNote2, station1, station2;
    TextView titleExplore, titleSubExplore;
    ImageView busCentre, busImageTITLE, aboutIMG;

    Location currentLocation;
    FusedLocationProviderClient mfusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;


    RelativeLayout route01, route02, route03, route04, route05, route06;
    FloatingActionButton routeFAB, viewAllBusses;


    private GoogleMap googleMap;
    private String TAG = "so47492459";

    private Polyline currentPolyline;


    String origin = "-6.792354, 39.208328";
    String destination = "-6.786875, 39.166927";


    private GoogleMap mMap;
    RelativeLayout bottomSheet;
    RelativeLayout sideSheet;
    FloatingActionButton btnToggle;

    int currentState = -1;
    int sidecurrentState = -1;
    private List<LatLng> pontos;

    private double longitude = 39.224822;
    private double latitude = -6.797102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        routeFAB = (FloatingActionButton) findViewById(R.id.routrFab);
        viewAllBusses = (FloatingActionButton) findViewById(R.id.viewallbus);


        routeNote = (TextView) findViewById(R.id.route_notify);
        routeNote2 = (TextView) findViewById(R.id.route_notifyright);
        station1 = (TextView) findViewById(R.id.route_station);
        station2 = (TextView) findViewById(R.id.route_stationtwo);
        busCentre = (ImageView) findViewById(R.id.buscenterd);
        busImageTITLE = (ImageView) findViewById(R.id.busImagetitle);
        titleExplore = (TextView) findViewById(R.id.titleExplore);
        titleSubExplore = (TextView) findViewById(R.id.titlesubexplore);
        aboutIMG = (ImageView) findViewById(R.id.aboutthis);


        route01 = (RelativeLayout) findViewById(R.id.route_kimara_kivukoni);
        route02 = (RelativeLayout) findViewById(R.id.route_kimara_morroco);
        route03 = (RelativeLayout) findViewById(R.id.route_kimara_gerezani);
        route04 = (RelativeLayout) findViewById(R.id.route_gerezani_muhimbili);
        route05 = (RelativeLayout) findViewById(R.id.route_ubungo_kivukoni);
        route06 = (RelativeLayout) findViewById(R.id.route_morroco_kivukoni);


        aboutIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, AboutActivity.class));
            }
        });

        viewAllBusses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyThis();
            }

        });


        View bottomSheet = findViewById(R.id.bottomsheet);
        btnToggle = (FloatingActionButton) findViewById(R.id.btnToggle);
        btnToggle.setBackgroundColor(getResources().getColor(R.color.colorFab));

        final BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        currentState = BottomSheetBehavior.STATE_COLLAPSED;

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                currentState = newState;

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

//       sidesheetview


//        View sideSheet = findViewById(R.id.sidesheet);
//        routeCard = (CardView) findViewById(R.id.cardroute);

//        final BottomSheetBehavior<View> sidebottomSheetBehavior = BottomSheetBehavior.from(sideSheet);
//        sidebottomSheetBehavior.setHideable(false);
//        sidebottomSheetBehavior.setPeekHeight(0);
//        sidebottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        sidecurrentState = BottomSheetBehavior.STATE_COLLAPSED;
//
//        sidebottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                sidecurrentState = newState;
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });
//
//
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentState == BottomSheetBehavior.STATE_EXPANDED) {

                    currentState = BottomSheetBehavior.STATE_COLLAPSED;

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else if (currentState == BottomSheetBehavior.STATE_COLLAPSED) {

                    currentState = BottomSheetBehavior.STATE_EXPANDED;

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }


                //hide polyline here


            }
        });


//        routeCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (sidecurrentState == BottomSheetBehavior.STATE_EXPANDED) {
//
//                    sidecurrentState = BottomSheetBehavior.STATE_COLLAPSED;
//                    sidebottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//
//                    currentState = BottomSheetBehavior.STATE_EXPANDED;
//
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//
//
//                }else if (sidecurrentState == BottomSheetBehavior.STATE_COLLAPSED) {
//
//                    sidecurrentState = BottomSheetBehavior.STATE_COLLAPSED;
//
//                    sidebottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//
//                    currentState = BottomSheetBehavior.STATE_COLLAPSED;
//
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//
//                }
//
//            }
//        });
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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        int height = 50;
        int width = 50;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.gd);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


        //ROUTE MARKER HERE
        BitmapDrawable bitmapdrawtwo = (BitmapDrawable) getResources().getDrawable(R.drawable.first_tag);
        Bitmap btwo = bitmapdrawtwo.getBitmap();
        Bitmap smallMarkertwo = Bitmap.createScaledBitmap(btwo, width, height, false);


        // Add a marker in Sydney and move the camera
        LatLng ubungo = new LatLng(-6.793464, 39.211282);
        LatLng kimara = new LatLng(-6.786875, 39.166927);
        LatLng kivukoni = new LatLng(-6.817929, 39.298996);
        LatLng morroco = new LatLng(-6.778340, 39.263823);
        LatLng gerezani = new LatLng(-6.825417, 39.273990);
        LatLng muhimbili = new LatLng(-6.805580, 39.273727);
        LatLng fire = new LatLng(-6.813318, 39.274496);


        LatLng magomenicenterview = new LatLng(-6.797102, 39.224822);
        LatLng jangwani = new LatLng(-6.808219, 39.260081);


        //kimara routes coordinates
        LatLng resort = new LatLng(-6.786660, 39.175220);
        LatLng korogwe = new LatLng(-6.797102, 39.224822);
        LatLng kimarabucha = new LatLng(-6.798213, 39.230781);
        LatLng kimarabaruti = new LatLng(-6.789977, 39.193249);
        LatLng kona = new LatLng(-6.799541, 39.235815);
        LatLng ubungomaji = new LatLng(-6.791128, 39.203034);


        mMap.addMarker(new MarkerOptions().position(ubungo).title("Ubungo BRT Terminal").snippet("Morogoro Road").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mMap.addMarker(new MarkerOptions().position(kimara).title("Kimara BRT Terminal").snippet("Morogoro Road").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mMap.addMarker(new MarkerOptions().position(kivukoni).title("Kivukoni BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mMap.addMarker(new MarkerOptions().position(morroco).title("Morroco BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mMap.addMarker(new MarkerOptions().position(gerezani).title("Gerezani BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mMap.addMarker(new MarkerOptions().position(muhimbili).title("Muhimbili BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mMap.addMarker(new MarkerOptions().position(fire).title("Fire BRT Terminal").snippet("Morogoro Road").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(magomenicenterview, 12));


        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude,longitude))      // Sets the center of the map to location user
                .zoom(12)                   // Sets the zoom
                .bearing(60)                // Sets the orientation of the camera to east
                .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("it is me").anchor(0.5f,0.5f));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 14));
//
//
//
//
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//
//        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
//        if (location != null)
//        {
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
//
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
//                    .zoom(17)                   // Sets the zoom
//                    .bearing(90)                // Sets the orientation of the camera to east
//                    .tilt(60)                   // Sets the tilt of the camera to 30 degrees
//                    .build();                   // Creates a CameraPosition from the builder
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        }


        routeFAB.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                googleMap.clear();
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("it is me").anchor(0.5f,0.5f));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 14));




                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (location != null)
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                //ripple effec t is here

//                MapRipple mapRipple = new MapRipple(googleMap, new LatLng(latitude,longitude), getApplicationContext());
//                mapRipple.withNumberOfRipples(3);
//                mapRipple.withFillColor(Color.BLUE);
//                mapRipple.withStrokeColor(Color.WHITE);
//                mapRipple.withStrokewidth(10);     // 10dp
//                mapRipple.withDistance(2000);      // 2000 metres radius
//                mapRipple.withRippleDuration(22000);    //12000ms
//                mapRipple.withTransparency(0.5f);
//                mapRipple.startRippleMapAnimation();


//
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubungo, 12));


                //durection API is here

//                new GetPathFromLocation(ubungo, kimara, new DirectionPointListener() {
//                    @Override
//                    public void onPath(PolylineOptions polyLine) {
//                        googleMap.addPolyline(polyLine);
//                    }
//                }).execute();


            }



        });

        route01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleMap.clear();
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(magomenicenterview, 11));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(magomenicenterview, 11));

//                CameraPosition cameraPosition = new CameraPosition.Builder()
//                        //.target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
//                        //.zoom(17)                   // Sets the zoom
//                        .bearing(90)                // Sets the orientation of the camera to east
//                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
//                        .build();                   // Creates a CameraPosition from the builder
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                mMap.addMarker(new MarkerOptions().position(kimara).title("Kimara BRT Terminal").snippet("Morogoro Road").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f).rotation(90));
                mMap.addMarker(new MarkerOptions().position(kivukoni).title("Kivukoni BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));

                routeNote.setText("KIMARA");
                routeNote2.setText("KIVUKONI");
                station1.setText("STATION");
                station2.setText("STATION");
                busCentre.setVisibility(View.VISIBLE);
                busImageTITLE.setVisibility(View.INVISIBLE);
                titleExplore.setVisibility(View.INVISIBLE);
                titleSubExplore.setVisibility(View.INVISIBLE);



                Polyline line_route_kimara1 = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.786875, 39.166927), new LatLng(-6.786660, 39.175220),new LatLng(-6.787119, 39.176839), new LatLng(-6.790808,39.186447),new LatLng(-6.789977,39.193249),new LatLng(-6.791128,39.203034),new LatLng(-6.793464,39.211282))
                        .width(5)
                        .color(Color.rgb(128,0,0)));
                

                Polyline line_route_magomeni1 = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.793464,39.211282), new LatLng(-6.795649, 39.217893), new LatLng(-6.797102, 39.224822), new LatLng(-6.798213, 39.230781), new LatLng(-6.799541, 39.235815), new LatLng(-6.799541, 39.235815),new LatLng(-6.800858,39.239829),new LatLng(-6.802783,39.245084),new LatLng(-6.804473,39.249784),new LatLng(-6.806043,39.254173),new LatLng(-6.807372,39.257746))
                        .width(5)
                        .color(Color.rgb(128,0,0)));

                Polyline line_route_fire1 = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.807372,39.257746), new LatLng(-6.808219, 39.260081),new LatLng(-6.811433,39.268982),new LatLng(-6.813319,39.274493),new LatLng(-6.813792,39.275783))
                        .width(5)
                        .color(Color.rgb(128,0,0)));

                Polyline line_route_kivukoni1 = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.813792,39.275783), new LatLng(-6.814477, 39.277601),new LatLng(-6.815506,39.279400),new LatLng(-6.817009,39.281950),new LatLng(-6.819058,39.285331),new LatLng(-6.819939,39.286333),new LatLng(-6.819286,39.287002),new LatLng(-6.819614,39.287387),new LatLng(-6.820056,39.287798),new LatLng(-6.818950,39.289067),new LatLng(-6.818331,39.290334),new LatLng(-6.817659,39.292386),new LatLng(-6.817436,39.293874),new LatLng(-6.818113,39.296650),new LatLng(-6.819451,39.297869),new LatLng(-6.818788,39.298715))
                        .width(5)
                        .color(Color.rgb(128,0,0)));







            }
        });


        route02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleMap.clear();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(magomenicenterview, 12));

                mMap.addMarker(new MarkerOptions().position(kimara).title("Kimara BRT Terminal").snippet("Morogoro Road").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));
                mMap.addMarker(new MarkerOptions().position(morroco).title("Morroco BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));


                routeNote.setText("KIMARA");
                routeNote2.setText("MORROCO");
                station1.setText("STATION");
                station2.setText("STATION");
                busCentre.setVisibility(View.VISIBLE);
                busImageTITLE.setVisibility(View.INVISIBLE);
                titleExplore.setVisibility(View.INVISIBLE);
                titleSubExplore.setVisibility(View.INVISIBLE);

                Polyline line_route_kimara = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.786875, 39.166927), new LatLng(-6.786660, 39.175220),new LatLng(-6.787119, 39.176839), new LatLng(-6.790808,39.186447),new LatLng(-6.789977,39.193249),new LatLng(-6.791128,39.203034),new LatLng(-6.793464,39.211282))
                        .width(5)
                        .color(Color.	rgb(255,255,0)));



                Polyline line_route_magomeni = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.793464,39.211282), new LatLng(-6.795649, 39.217893), new LatLng(-6.797102, 39.224822), new LatLng(-6.798213, 39.230781), new LatLng(-6.799541, 39.235815), new LatLng(-6.799541, 39.235815),new LatLng(-6.800858,39.239829),new LatLng(-6.802783,39.245084),new LatLng(-6.804473,39.249784),new LatLng(-6.806043,39.254173),new LatLng(-6.807372,39.257746))
                        .width(5)
                        .color(Color.	rgb(255,255,0)));

                Polyline line_route_morroco = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.807372,39.257746), new LatLng(-6.805985, 39.258312), new LatLng(-6.804050, 39.260512), new LatLng(-6.795971, 39.264041),new LatLng(-6.790796,39.263780),new LatLng(-6.785913,39.263524),new LatLng(-6.778610,39.263807))
                        .width(5)
                        .color(Color.	rgb(255,255,0)));






            }
        });


        route03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleMap.clear();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(magomenicenterview, 12));

                mMap.addMarker(new MarkerOptions().position(kimara).title("Kimara BRT Terminal").snippet("Morogoro Road").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));
                mMap.addMarker(new MarkerOptions().position(gerezani).title("Gerezani BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));



                routeNote.setText("KIMARA");
                routeNote2.setText("GEREZANI");
                station1.setText("STATION");
                station2.setText("STATION");
                busCentre.setVisibility(View.VISIBLE);
                busImageTITLE.setVisibility(View.INVISIBLE);
                titleExplore.setVisibility(View.INVISIBLE);
                titleSubExplore.setVisibility(View.INVISIBLE);

                Polyline line_route_kimara = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.786875, 39.166927), new LatLng(-6.786660, 39.175220),new LatLng(-6.787119, 39.176839), new LatLng(-6.790808,39.186447),new LatLng(-6.789977,39.193249),new LatLng(-6.791128,39.203034),new LatLng(-6.793464,39.211282))
                        .width(5)
                        .color(Color.	rgb(135,206,250)));



                Polyline line_route_magomeni = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.793464,39.211282), new LatLng(-6.795649, 39.217893), new LatLng(-6.797102, 39.224822), new LatLng(-6.798213, 39.230781), new LatLng(-6.799541, 39.235815), new LatLng(-6.799541, 39.235815),new LatLng(-6.800858,39.239829),new LatLng(-6.802783,39.245084),new LatLng(-6.804473,39.249784),new LatLng(-6.806043,39.254173),new LatLng(-6.807372,39.257746))
                        .width(5)
                        .color(Color.	rgb(135,206,250)));

                Polyline line_route_fire = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.807372,39.257746), new LatLng(-6.808219, 39.260081),new LatLng(-6.811433,39.268982),new LatLng(-6.813319,39.274493),new LatLng(-6.813792,39.275783))
                        .width(5)
                        .color(Color.	rgb(135,206,250)));


                Polyline line_route_gerezani = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.813792,39.275783), new LatLng(-6.819857, 39.272951),new LatLng(-6.820541,39.272814),new LatLng(-6.823712,39.273755),new LatLng(-6.825294,39.274538))
                        .width(5)
                        .color(Color.	rgb(135,206,250)));


                // remove other polylines

            }
        });


        route04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleMap.clear();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fire, 14));

                mMap.addMarker(new MarkerOptions().position(gerezani).title("Gerezani BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));
                mMap.addMarker(new MarkerOptions().position(muhimbili).title("Muhimbili BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));


                routeNote.setText("GEREZANI");
                routeNote2.setText("MUHIMBILI");
                station1.setText("STATION");
                station2.setText("STATION");
                busCentre.setVisibility(View.VISIBLE);
                busImageTITLE.setVisibility(View.INVISIBLE);
                titleExplore.setVisibility(View.INVISIBLE);
                titleSubExplore.setVisibility(View.INVISIBLE);


                Polyline line_route_muhimbili = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.812904,39.273253), new LatLng(-6.811556, 39.273734),new LatLng(-6.809792,39.274568),new LatLng(-6.807907,39.275737),new LatLng(-6.806915,39.274990),new LatLng(-6.805467,39.274622),new LatLng(-6.805485,39.273745))
                        .width(5)
                        .color(Color.rgb(50,205,50)));


                Polyline line_route_fire = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.812904,39.273237),new LatLng(-6.813319,39.274493),new LatLng(-6.813792,39.275783))
                        .width(5)
                        .color(Color.rgb(50,205,50)));



                Polyline line_route_gerezani = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.813792,39.275783), new LatLng(-6.819857, 39.272951),new LatLng(-6.820541,39.272814),new LatLng(-6.823712,39.273755),new LatLng(-6.825294,39.274538))
                        .width(5)
                        .color(Color.rgb(50,205,50)));
            }
        });

        route05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jangwani, 12));

                mMap.addMarker(new MarkerOptions().position(ubungo).title("Ubungo BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));
                mMap.addMarker(new MarkerOptions().position(kivukoni).title("Kivukoni BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));


                routeNote.setText("UBUNGO");
                routeNote2.setText("KIVUKONI");
                station1.setText("STATION");
                station2.setText("STATION");
                busCentre.setVisibility(View.VISIBLE);
                busImageTITLE.setVisibility(View.INVISIBLE);
                titleExplore.setVisibility(View.INVISIBLE);
                titleSubExplore.setVisibility(View.INVISIBLE);


                Polyline line_route_magomeni = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.793464,39.211282), new LatLng(-6.795649, 39.217893), new LatLng(-6.797102, 39.224822), new LatLng(-6.798213, 39.230781), new LatLng(-6.799541, 39.235815), new LatLng(-6.799541, 39.235815),new LatLng(-6.800858,39.239829),new LatLng(-6.802783,39.245084),new LatLng(-6.804473,39.249784),new LatLng(-6.806043,39.254173),new LatLng(-6.807372,39.257746))
                        .width(5)
                        .color(Color.	rgb(0,0,139)));

                Polyline line_route_fire = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.807372,39.257746), new LatLng(-6.808219, 39.260081),new LatLng(-6.811433,39.268982),new LatLng(-6.813319,39.274493),new LatLng(-6.813792,39.275783))
                        .width(5)
                        .color(Color.	rgb(0,0,139)));

                Polyline line_route_kivukoni = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.813792,39.275783), new LatLng(-6.814477, 39.277601),new LatLng(-6.815506,39.279400),new LatLng(-6.817009,39.281950),new LatLng(-6.819058,39.285331),new LatLng(-6.819939,39.286333),new LatLng(-6.819286,39.287002),new LatLng(-6.819614,39.287387),new LatLng(-6.820056,39.287798),new LatLng(-6.818950,39.289067),new LatLng(-6.818331,39.290334),new LatLng(-6.817659,39.292386),new LatLng(-6.817436,39.293874),new LatLng(-6.818113,39.296650),new LatLng(-6.819451,39.297869),new LatLng(-6.818788,39.298715))
                        .width(5)
                        .color(Color.	rgb(0,0,139)));
            }
        });

        route06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleMap.clear();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fire, 13));

                mMap.addMarker(new MarkerOptions().position(morroco).title("Morroco BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));
                mMap.addMarker(new MarkerOptions().position(kivukoni).title("Kivukoni BRT Terminal").icon(BitmapDescriptorFactory.fromBitmap(smallMarkertwo)).anchor(0.5f,0.5f));


                routeNote.setText("MORROCO");
                routeNote2.setText("KIVUKONI");
                station1.setText("STATION");
                station2.setText("STATION");
                busCentre.setVisibility(View.VISIBLE);
                busImageTITLE.setVisibility(View.INVISIBLE);
                titleExplore.setVisibility(View.INVISIBLE);
                titleSubExplore.setVisibility(View.INVISIBLE);

                Polyline line_route_fire = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.807372,39.257746), new LatLng(-6.808219, 39.260081),new LatLng(-6.811433,39.268982),new LatLng(-6.813319,39.274493),new LatLng(-6.813792,39.275783))
                        .width(5)
                        .color(Color.rgb(75,0,130)));

                Polyline line_route_kivukoni = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.813792,39.275783), new LatLng(-6.814477, 39.277601),new LatLng(-6.815506,39.279400),new LatLng(-6.817009,39.281950),new LatLng(-6.819058,39.285331),new LatLng(-6.819939,39.286333),new LatLng(-6.819286,39.287002),new LatLng(-6.819614,39.287387),new LatLng(-6.820056,39.287798),new LatLng(-6.818950,39.289067),new LatLng(-6.818331,39.290334),new LatLng(-6.817659,39.292386),new LatLng(-6.817436,39.293874),new LatLng(-6.818113,39.296650),new LatLng(-6.819451,39.297869),new LatLng(-6.818788,39.298715))
                        .width(5)
                        .color(Color.rgb(75,0,130)));

                Polyline line_route_morroco = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(-6.807372,39.257746), new LatLng(-6.805985, 39.258312), new LatLng(-6.804050, 39.260512), new LatLng(-6.795971, 39.264041),new LatLng(-6.790796,39.263780),new LatLng(-6.785913,39.263524),new LatLng(-6.778610,39.263807))
                        .width(5)
                        .color(Color.rgb(75,0,130)));
            }
        });
    }




//    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
//        String mode = "mode=" + directionMode;
//        String str_orign = "origin" + origin.latitude + "," + origin.longitude;
//        String str_dest = "destination" + dest.latitude + "," + dest.longitude;
//        String parameters = str_orign + "&" + str_dest + "&" + mode;
//        String output = "json";
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&keys=" + getString(R.string.google_maps_key);
//        return url;
//    }



    public void notifyThis() {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this.getApplicationContext());
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.frontbusview)
                .setTicker("BRT")
                .setContentTitle("Next Station")
                .setContentText("POSTA BRT STATION")
                .setContentInfo("INFO");

        NotificationManager nm = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }
}

