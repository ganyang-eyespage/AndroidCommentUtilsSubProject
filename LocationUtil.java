package com.eyespage.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import java.util.Timer;
import java.util.TimerTask;

public class LocationUtil {

  public static final int WAIT_TIME = 20 * 1000;
  Timer mTimer;
  LocationManager mLocationManager;
  LocationCallback mCallback;
  boolean mGPSEnable = false;
  boolean mNetworkEnable = false;

  public boolean getLocation(Context context, LocationCallback callback) {
    mCallback = callback;
    if (mLocationManager == null) {
      mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    try {
      mGPSEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    } catch (Exception e) {
    }
    try {
      mNetworkEnable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    } catch (Exception e) {
    }
    if (!mGPSEnable && !mNetworkEnable) {
      callback.gotLocation(null);
      return false;
    }
    if (mGPSEnable) {
      mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
          locationListenerGps);
    }
    if (mNetworkEnable) {
      mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
          locationListenerNetwork);
    }
    mTimer = new Timer();
    mTimer.schedule(new LocationTimerTask(), WAIT_TIME);
    return true;
  }

  LocationListener locationListenerGps = new LocationListener() {
    public void onLocationChanged(Location location) {
      mTimer.cancel();
      mCallback.gotLocation(location);
      mLocationManager.removeUpdates(this);
      mLocationManager.removeUpdates(locationListenerNetwork);
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
  };

  LocationListener locationListenerNetwork = new LocationListener() {
    public void onLocationChanged(Location location) {
      mTimer.cancel();
      mCallback.gotLocation(location);
      mLocationManager.removeUpdates(this);
      mLocationManager.removeUpdates(locationListenerGps);
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
  };

  class LocationTimerTask extends TimerTask {
    @Override public void run() {
      mLocationManager.removeUpdates(locationListenerGps);
      mLocationManager.removeUpdates(locationListenerNetwork);

      Location gpsLocation = null;
      Location netLocation = null;
      if (mGPSEnable) {
        gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      }
      if (mNetworkEnable) {
        netLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      }
      if (gpsLocation != null && netLocation != null) {
        if (gpsLocation.getTime() > netLocation.getTime()) {
          mCallback.gotLocation(gpsLocation);
        } else {
          mCallback.gotLocation(netLocation);
        }
        return;
      }
      if (gpsLocation != null) {
        mCallback.gotLocation(gpsLocation);
        return;
      }
      if (netLocation != null) {
        mCallback.gotLocation(netLocation);
        return;
      }
      mCallback.gotLocation(null);
    }
  }

  public static interface LocationCallback {
    void gotLocation(Location location);
  }
}