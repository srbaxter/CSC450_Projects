package edu.ncsu.sample;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AlertMeActivity extends Activity {
  LocationManager locationManager;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    Criteria criteria = new Criteria();
    criteria.setAccuracy(Criteria.ACCURACY_FINE);
    criteria.setAltitudeRequired(false);
    criteria.setBearingRequired(false);
    criteria.setCostAllowed(true);
    criteria.setPowerRequirement(Criteria.POWER_LOW);

    //String provider = locationManager.getBestProvider(criteria, true);
      String provider = LocationManager.GPS_PROVIDER;
    Location location = locationManager.getLastKnownLocation(provider);

    //if (location != null)
      updateWithNewLocation(location);


      locationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() {
          @Override
          public void onLocationChanged(Location location) {
              if (location != null)
                  updateWithNewLocation(location);
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
      });

  }

  private void updateWithNewLocation(Location location) {
    TextView myLocationText = (TextView) findViewById(R.id.myLocationText);

    String latLongString;

    if (location != null) {
      double lat = location.getLatitude();
      double lng = location.getLongitude();
      latLongString = "Latitude: " + lat + "\nLongitude: " + lng;
    } else {
      latLongString = "No location found";
    }

    myLocationText.setText("Your Current Position is:\n" + latLongString);
  }
}
