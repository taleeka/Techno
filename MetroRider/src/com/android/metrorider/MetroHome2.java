package com.android.metrorider;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.app.AppConst;
import com.android.app.AppController;
import com.android.common.GPSTracker;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MetroHome2 extends ActionBarActivity implements
		FragmentDrawer.FragmentDrawerListener,ConnectionCallbacks,
		OnConnectionFailedListener,android.location.LocationListener{

	private Toolbar mToolbar;
	private GoogleMap googleMap;
	private FragmentDrawer drawerFragment;
	Fragment fragment = null;
	GPSTracker gps;
	double latitude;
	double longtitude;
	String id;
	private static String URL_GEOUPDATE = AppConst.GEOMETRY_UPDATE;
	private GoogleApiClient mGoogleApiClient;
	private static final String TAG = "error";
	LocationManager locationmanager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metro_home2);

		mToolbar = (Toolbar) findViewById(R.id.toolbar);

		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		fragment = new NewHomeFragment2();
		drawerFragment = (FragmentDrawer) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_navigation_drawer);
		drawerFragment.setUp(R.id.fragment_navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
		drawerFragment.setDrawerListener(this);

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.container_body, fragment);
			fragmentTransaction.commit();

		}
	
		
		//
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API, new Plus.PlusOptions.Builder().build()) // note
																	// the
																	// options
		.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	
		locationmanager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//		Thread thread = new Thread() {
//		    @Override
//		    public void run() {
//		        try {
//		            while(true) {
//		                sleep(4000);
//		            	
//		        		if (gps.canGetLocation()) {
//		        			latitude = gps.getLatitude();
//		        			longtitude = gps.getLongtitude();
//		        		} else {
//		        			gps.showSettingAllerts();
//		        		}
//		        		Log.d("map","hhh"+latitude);
//		            }
//		        } catch (InterruptedException e) {
//		            e.printStackTrace();
//		        }
//		    }
//		};
//		thread.start();
		new LoadGeo().execute();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.action_signout) {
			
			final ProgressDialog ringProgressDialog = new ProgressDialog(
					this,
					ProgressDialog.THEME_HOLO_LIGHT);
			ringProgressDialog
					.setTitle("Please wait ...");
			ringProgressDialog.setMessage("Log Out...");
			ringProgressDialog.show();
			ringProgressDialog.setCancelable(true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
						signOutFromGplus();
						
					} catch (Exception e) {
					}
					ringProgressDialog.dismiss();
				}

			}).start();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// //
	public void signOutFromGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			Intent i4 = new Intent(this, MetroLogin.class);
			startActivity(i4);

		}
	}
	
	@Override
	public void onDrawerItemSelected(View view, int position) {
		Fragment fragment = null;
		String title = getString(R.string.app_name);
		switch (position) {
		case 0:
			fragment = new NewHomeFragment2();
			break;
		case 1:
			fragment = new HomeFragment();
			title = getString(R.string.title_busroute);
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.container_body, fragment);
			fragmentTransaction.commit();

			getSupportActionBar().setTitle(title);
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}
	
	private class LoadGeo extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			//
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			Thread thread = new Thread() {
			    @Override
			    public void run() {
			        try {
			            while(true) {
			               sleep(4000);
			              
			               PostRequeset();
			            }
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
			    }
			};

			thread.start();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
		}
					

	}
	
	public void PostRequeset(){
		Log.d("lat", "lat"+latitude);
//		
//		gps = new GPSTracker(this);
//		 gps.getLocation();
//         if (gps.canGetLocation()) {
//  			latitude = gps.getLatitude();
//  			longtitude = gps.getLongtitude();
//  		} else {
//  			gps.showSettingAllerts();
//  		}
		SharedPreferences prefs = getSharedPreferences("conetext",
				Context.MODE_PRIVATE);
		id = prefs.getString("userid", null);
		Map<String, String> params = new HashMap<String, String>();
		params.put("userid", id);
		params.put("usertype", "driver");
		params.put("lat", String.valueOf(latitude));
		params.put("lng", String.valueOf(longtitude));
		
		JsonObjectRequest req = new JsonObjectRequest(
				URL_GEOUPDATE, new JSONObject(params),
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							Log.d("map", "msg");
							VolleyLog.v("Response:%n %s",
									response.toString(4));
							Log.d("map", "msg"+response );
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(
							VolleyError error) {
						VolleyLog.e("Error: ",
								error.getMessage());
						Log.d("map", "msg");
					}
				});
		
		// add the request object to the queue to be executed
		AppController.getInstance().addToRequestQueue(req);
	
	}

	@Override
	public void onLocationChanged(Location location) {
		latitude=location.getLatitude();
		longtitude=location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void onLocationChanged(final Location location) {
//		Thread thread = new Thread() {
//		    @Override
//		    public void run() {
//		        try {
//		            while(true) {
//		                sleep(4000);
//		               PostRequeset(location);
//		            }
//		        } catch (InterruptedException e) {
//		            e.printStackTrace();
//		        }
//		    }
//		};
//
//		thread.start();
//		
//	}
	
}
