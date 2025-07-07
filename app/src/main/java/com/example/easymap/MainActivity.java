package com.example.easymap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String BACKEND_URL = "http://10.194.27.176:9000/chat";
    
    private MapView mapView;
    private AMap aMap;
    private LocationHelper locationHelper;
    private EditText searchInput;
    private MaterialButton searchButton;
    private RecyclerView resultsRecyclerView;
    private List<PlaceResult> searchResults;
    private AMapLocation currentLocation;
    private ResultsAdapter resultsAdapter;
    
    private OkHttpClient httpClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        
        Log.d(TAG, "MainActivity onCreate started");
        
        // Initialize views
        mapView = findViewById(R.id.mapView);
        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        
        if (mapView == null) {
            Log.e(TAG, "MapView is null!");
            Toast.makeText(this, "Map initialization failed", Toast.LENGTH_LONG).show();
            return;
        }
        
        Log.d(TAG, "Views initialized successfully");
        
        // Initialize HTTP client
        httpClient = new OkHttpClient();
        
        // Initialize location helper
        locationHelper = new LocationHelper(this);
        
        if (!locationHelper.isInitialized()) {
            Log.e(TAG, "LocationHelper failed to initialize");
            Toast.makeText(this, "Location service initialization failed", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "LocationHelper initialized successfully");
        }
        
        // Initialize map
        try {
            mapView.onCreate(savedInstanceState);
            aMap = mapView.getMap();
            
            if (aMap == null) {
                Log.e(TAG, "AMap is null after initialization!");
                Toast.makeText(this, "Map initialization failed", Toast.LENGTH_LONG).show();
                return;
            }
            
            Log.d(TAG, "Map initialized successfully");
            
            // Enable location button
            aMap.setMyLocationEnabled(true);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize map", e);
            Toast.makeText(this, "Map initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        
        // Setup RecyclerView
        searchResults = new ArrayList<>();
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsAdapter = new ResultsAdapter(searchResults, new ResultsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PlaceResult result) {
                LatLng latLng = new LatLng(result.getLatitude(), result.getLongitude());
                aMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.newLatLngZoom(latLng, 18));
            }
        });
        resultsRecyclerView.setAdapter(resultsAdapter);
        
        // Setup search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    Toast.makeText(MainActivity.this, R.string.enter_search_term, Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        Log.d(TAG, "Requesting permissions and location");
        // Request permissions and get location
        requestPermissionsAndGetLocation();
    }
    
    private void requestPermissionsAndGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }
    
    private void getCurrentLocation() {
        Log.d(TAG, "Getting current location...");
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationSuccess(AMapLocation location) {
                currentLocation = location;
                Log.d(TAG, "Location obtained: " + location.getLatitude() + ", " + location.getLongitude());
                
                runOnUiThread(() -> {
                    try {
                        // Move map to current location
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        aMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        
                        // Add current location marker
                        aMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(getString(R.string.your_location))
                            .snippet(location.getAddress()));
                        
                        Toast.makeText(MainActivity.this, "Location obtained successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to update map with location", e);
                        Toast.makeText(MainActivity.this, "Failed to update map: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            
            @Override
            public void onLocationFailed(String error) {
                Log.e(TAG, "Location failed: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Failed to get location: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void performSearch(String query) {
        // First, send query to AI chatbot backend
        sendToChatbot(query);
    }
    
    private void sendToChatbot(String query) {
        String jsonBody = "{\"message\": \"" + query + "\"}";
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
        
        Request request = new Request.Builder()
            .url(BACKEND_URL)
            .post(body)
            .build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "AI connection failed", e);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to connect to AI", Toast.LENGTH_LONG).show());
            }
            
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    List<PlaceResult> places = parseAIResponse(responseBody);
                    runOnUiThread(() -> showSearchResults(places));
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "AI service error", Toast.LENGTH_LONG).show());
                }
            }
        });
    }
    
    private List<PlaceResult> parseAIResponse(String response) {
        List<PlaceResult> results = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(response);
            JSONArray arr = json.getJSONArray("results");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String name = obj.getString("name");
                String address = obj.getString("address");
                double lat = obj.getDouble("latitude");
                double lng = obj.getDouble("longitude");
                results.add(new PlaceResult(name, address, lat, lng, address));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse AI response", e);
        }
        return results;
    }
    
    private void showSearchResults(List<PlaceResult> places) {
        searchResults.clear();
        aMap.clear();
        for (PlaceResult place : places) {
            searchResults.add(place);
            LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
            aMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(place.getName())
                .snippet(place.getAddress()));
        }
        resultsAdapter.updateResults(searchResults);
        if (!places.isEmpty()) {
            LatLng first = new LatLng(places.get(0).getLatitude(), places.get(0).getLongitude());
            aMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.newLatLngZoom(first, 15));
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, R.string.location_permission_required, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationHelper != null) {
            locationHelper.destroy();
        }
    }
    
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
} 