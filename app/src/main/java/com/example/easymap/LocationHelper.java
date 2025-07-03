package com.example.easymap;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    
    private AMapLocationClient locationClient;
    private LocationCallback callback;
    private boolean isInitialized = false;
    
    public interface LocationCallback {
        void onLocationSuccess(AMapLocation location);
        void onLocationFailed(String error);
    }
    
    public LocationHelper(Context context) {
        Log.d(TAG, "Initializing LocationHelper...");
        try {
            // Initialize AMapLocationClient
            AMapLocationClient.updatePrivacyShow(context, true, true);
            AMapLocationClient.updatePrivacyAgree(context, true);
            
            locationClient = new AMapLocationClient(context);
            
            if (locationClient == null) {
                Log.e(TAG, "Failed to create AMapLocationClient");
                return;
            }
            
            locationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    Log.d(TAG, "Location changed callback received");
                    if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                        Log.d(TAG, "Location success: " + aMapLocation.getLatitude() + ", " + aMapLocation.getLongitude());
                        if (callback != null) {
                            callback.onLocationSuccess(aMapLocation);
                        }
                    } else {
                        String errorMsg = "Location failed: " + (aMapLocation != null ? 
                            aMapLocation.getErrorInfo() : "Unknown error");
                        Log.e(TAG, errorMsg);
                        if (callback != null) {
                            callback.onLocationFailed(errorMsg);
                        }
                    }
                }
            });
            
            isInitialized = true;
            Log.d(TAG, "LocationHelper initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize AMapLocationClient", e);
            isInitialized = false;
        }
    }
    
    public void getCurrentLocation(LocationCallback callback) {
        Log.d(TAG, "getCurrentLocation called, isInitialized: " + isInitialized);
        this.callback = callback;
        
        if (!isInitialized || locationClient == null) {
            Log.e(TAG, "Location client not initialized");
            if (callback != null) {
                callback.onLocationFailed("Location client not initialized");
            }
            return;
        }
        
        try {
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setOnceLocation(true);
            option.setOnceLocationLatest(true);
            option.setNeedAddress(true);
            option.setWifiActiveScan(true);
            option.setMockEnable(false);
            option.setHttpTimeOut(20000);
            option.setLocationCacheEnable(false);
            
            locationClient.setLocationOption(option);
            Log.d(TAG, "Starting location request...");
            locationClient.startLocation();
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to start location", e);
            if (callback != null) {
                callback.onLocationFailed("Failed to start location: " + e.getMessage());
            }
        }
    }
    
    public void stopLocation() {
        if (locationClient != null && isInitialized) {
            try {
                locationClient.stopLocation();
                Log.d(TAG, "Location stopped");
            } catch (Exception e) {
                Log.e(TAG, "Failed to stop location", e);
            }
        }
    }
    
    public void destroy() {
        if (locationClient != null && isInitialized) {
            try {
                locationClient.onDestroy();
                locationClient = null;
                isInitialized = false;
                Log.d(TAG, "LocationHelper destroyed");
            } catch (Exception e) {
                Log.e(TAG, "Failed to destroy location client", e);
            }
        }
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
} 