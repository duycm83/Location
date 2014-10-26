package jp.co.spidersoft.location;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.TextView;

public class MainActivity extends Activity implements LocationListener {
	private static final String TAG = "MainActivity";
	LocationManager lm;
	private static TextView lt;
	private static TextView ln;
	private static TextView ac;
	private static WebView webview;
	String provider;
	Location l;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ln = (TextView) findViewById(R.id.lng);
		lt = (TextView) findViewById(R.id.lat);
		ac = (TextView) findViewById(R.id.acc);
		webview = (WebView) findViewById(R.id.webview);
		// get location service
		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		boolean isGPSProviderEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNETProviderEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if( !isGPSProviderEnabled || !isNETProviderEnabled) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("Location is off");  // GPS not found
	        builder.setMessage("Location setting open"); // Want to enable?
	        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialogInterface, int i) {
	                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	            }
	        });
	        builder.setNegativeButton(android.R.string.no, null);
	        builder.create().show();
	        return;
	    }
		Criteria c = new Criteria();
		// criteria object will select best service based on
		// Accuracy, power consumption, response, bearing and monetary cost
		// set false to use best service otherwise it will select the default
		// Sim network
		// and give the location based on sim network
		// now it will first check satellite than Internet than Sim network
		// location
		provider = lm.getBestProvider(c, false);
		
		// now you have best provider
		// get location
		l = lm.getLastKnownLocation(provider);
		if (l != null) {
			// get latitude and longitude of the location
			double lng = l.getLongitude();
			double lat = l.getLatitude();
			double acc = l.getAccuracy();
			// display on text view
			updateview(lng, lat, acc);
		} else {
			// Register the listener with the Location Manager to receive location updates
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, this);
			ln.setText("No Provider");
			lt.setText("No Provider");
			ac.setText("No Provider");
		}
	}

	// If you want location on changing place also than use below method
	// otherwise remove all below methods and don't implement location listener
	@Override
	public void onLocationChanged(Location l) {
		Log.v(TAG, "onLocationChanged");
		double lng = l.getLongitude();
		double lat = l.getLatitude();
		double acc = l.getAccuracy();
		updateview(lng, lat, acc);
		
	}

	private void updateview(final double lng, final double lat, final double acc) {
		Log.v(TAG, "updateview");
		Log.v(TAG, "lng:"+lng);
		Log.v(TAG, "lat:"+lat);
		Log.v(TAG, "acc:"+acc);
		ln.setText(String.valueOf(lng));
		lt.setText(String.valueOf(lat));
		ac.setText(String.valueOf(acc));
		String URL = "http://maps.google.com/maps/api/staticmap?center="+lat+","+lng+"&zoom=15&size=500x200&markers=color:red|"+lat+","+lng+"&sensor=false";
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.loadUrl(URL);
		webview.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				  switch (event.getAction()) {
				  case MotionEvent.ACTION_UP:
					  String uri = String.format(Locale.getDefault(), "geo:%f,%f", lat, lng);
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
						startActivity(intent);	
						return v.performClick();
				  }
				return false;
			}
		});
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Log.v(TAG, "onProviderDisabled"+arg0);
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Log.v(TAG, "onProviderEnabled"+arg0);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.v(TAG, "onStatusChanged"+arg0);
	}
	

}