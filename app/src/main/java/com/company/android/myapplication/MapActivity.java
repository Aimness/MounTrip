package com.company.android.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolDragListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.turf.TurfMeasurement;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;
import io.ticofab.androidgpxparser.parser.domain.WayPoint;

import static com.mapbox.turf.TurfConstants.UNIT_KILOMETERS;

public class MapActivity extends AppCompatActivity{
    private MapView mMapView;
    private List<Point> routeCoordinates;
    private Gpx parsedGPX = null;
    private GPXParser parser = new GPXParser();
    private int id = 0;
    private SymbolManager symbolManager;
    private TextView measureDistance, marker_info;
    private ImageButton backButton;
    private TrackPoint trackPoint;
    private  Random rand;

    //measure
    private static final int CIRCLE_COLOR = Color.RED;

    private static final String DISTANCE_UNITS = UNIT_KILOMETERS;
    private double totalTrackDistance = 0, distanceBetweenLastAndSecond = 0, totalPathDistance = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Mapbox.getInstance(this, getString(R.string.access_token));



        setContentView(R.layout.activity_map);
        File file = (File)getIntent().getExtras().get("MapCoordinates");

        mMapView = findViewById(R.id.mapView);
        marker_info = findViewById(R.id.marker_info);
        measureDistance = findViewById(R.id.measure_distance);
        backButton = findViewById(R.id.map_back);

        mMapView.onCreate(savedInstanceState);

        mMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker_info.setVisibility(View.GONE);
            }
        });
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {




                mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        getDataFromFile(file);



                        if(parsedGPX != null) {
                            symbolManager = new SymbolManager(mMapView, mapboxMap, style);
                            style.addImage("My marker", BitmapFactory.decodeResource(getResources(), R.drawable.mapbox_marker_icon_default));
                            List<WayPoint> wayPoints = parsedGPX.getWayPoints();
                            List<Track> tracks = parsedGPX.getTracks();
                            for (int i = 0; i < tracks.size(); i++) {
                                Track track = tracks.get(i);

                                Log.i("Tracks", "Number of tracks: " + tracks.size());
                                List<TrackSegment> segments = track.getTrackSegments();
                                for (int j = 0; j < segments.size(); j++) {
                                    TrackSegment segment = segments.get(j);
                                    for (int k = 0; k < segment.getTrackPoints().size(); k++) {
                                        trackPoint = segment.getTrackPoints().get(k);
                                        if (k == 0 || k == (segment.getTrackPoints().size() - 1)) {


                                            Log.i("TrackInfo", String.format("getTrackCmt: %s\ngetTrackName: %s\ngetTrackNumber: %s\n", track.getTrackCmt(), track.getTrackName(),
                                                    track.getTrackType()));
                                            SymbolOptions symbolOptions = new SymbolOptions()
                                                    .withLatLng(new LatLng(trackPoint.getLatitude(), trackPoint.getLongitude()))
                                                    .withIconImage("My marker")
                                                    .withIconSize(1.0f)
                                                    .withSymbolSortKey(3.0f)
                                                    .withDraggable(false);

                                            Symbol symbol = symbolManager.create(symbolOptions);
                                            symbol.setTextField(String.format("Track Name: %s\nElevation: %.2f\nDistance: %.2f",
                                                    track.getTrackName(), trackPoint.getElevation(), totalTrackDistance));
                                            symbol.setTextOffset(new PointF(0.0f, -5.0f));
                                            symbol.setTextSize(8.0f);
                                            symbol.setTextColor(Color.BLACK);

                                        }

                                        routeCoordinates.add(Point.fromLngLat(trackPoint.getLongitude(), trackPoint.getLatitude()));
                                        measureTrackDistance();
                                    }

                                }



                                if (i % 2 > 0) {

                                    style.addSource(new GeoJsonSource(Integer.toString(i) + "source",
                                            FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                                                    LineString.fromLngLats(routeCoordinates)
                                            )})));

                                    style.addLayer(new LineLayer(Integer.toString(i) + "linelayer", Integer.toString(i) + "source").withProperties(
                                            PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                            PropertyFactory.lineWidth(5f),
                                            PropertyFactory.lineColor(generateRandomColor())
                                    ));
                                } else {
                                    style.addSource(new GeoJsonSource(Integer.toString(i) + "source",
                                            FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                                                    LineString.fromLngLats(routeCoordinates)
                                            )})));

                                    style.addLayer(new LineLayer(Integer.toString(i) + "linelayer", Integer.toString(i) + "source").withProperties(
                                            PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                            PropertyFactory.lineWidth(5f),
                                            PropertyFactory.lineColor(generateRandomColor())
                                    ));
                                }





                                symbolManager.setIconAllowOverlap(true);
                                symbolManager.setTextAllowOverlap(true);


                                totalPathDistance += totalTrackDistance;
                                totalTrackDistance = 0;

                                if(i == (tracks.size() - 1)) {
                                    CameraPosition position = new CameraPosition.Builder()
                                            .target(new LatLng(trackPoint.getLatitude(), trackPoint.getLongitude()))
                                            .zoom(10)
                                            .tilt(20)
                                            .build();
                                    measureDistance.setText(String.format("Total path distance: %.2f km", totalPathDistance));
                                }
                                routeCoordinates.clear();

                            }


                            style.addImage("My marker", BitmapFactory.decodeResource(getResources(), R.drawable.mapbox_marker_icon_default));



                            for (int i = 0; i < wayPoints.size(); i++) {
                                WayPoint wayPoint = wayPoints.get(i);

                                SymbolOptions symbolOptions = new SymbolOptions()
                                        .withLatLng(new LatLng(wayPoint.getLatitude(), wayPoint.getLongitude()))
                                        .withIconImage("My marker")
                                        .withIconSize(1.0f)
                                        .withSymbolSortKey(3.0f)
                                        .withDraggable(false);

                                Symbol symbol = symbolManager.create(symbolOptions);
                                symbol.setTextOffset(new PointF(0.0f, -5.0f));
                                symbol.setTextSize(8.0f);
                                symbol.setTextColor(Color.BLACK);

                            }

                            symbolManager.addClickListener(new OnSymbolClickListener() {
                                @Override
                                public boolean onAnnotationClick(Symbol symbol) {

                                    symbol.setTextField("haha");
                                    marker_info.setVisibility(View.VISIBLE);
                                    marker_info.setText(String.format(String.format(Locale.ENGLISH, "LatLng: %f, %f\n",
                                            symbol.getLatLng().getLatitude(), symbol.getLatLng().getLongitude())));
                                    return true;
                                }
                            });



                        }else
                        {
                            Log.i("Track", "Error");
                        }

                    }

                });
            }
        });




        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void measureTrackDistance()
    {
        if(routeCoordinates.size() >= 2)
        {
            distanceBetweenLastAndSecond = TurfMeasurement
                    .distance(routeCoordinates.get(routeCoordinates.size() -2),
                            routeCoordinates.get(routeCoordinates.size() - 1));
        }

        if(routeCoordinates.size() >= 2 && distanceBetweenLastAndSecond > 0)
        {
            totalTrackDistance += distanceBetweenLastAndSecond;
        }
    }


    private void getDataFromFile(File file)
    {
        routeCoordinates = new ArrayList<>();

        try {
            InputStream in = new FileInputStream(file);
            parsedGPX = parser.parse(in);

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private int generateRandomColor()
    {
       rand = new Random();

        int red = rand.nextInt() + 250;
        int green = rand.nextInt() + 250;
        int blue = rand.nextInt() + 250;

       int color = Color.rgb(red, green, blue);

        return color;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

}