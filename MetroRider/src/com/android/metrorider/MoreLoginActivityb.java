package com.android.metrorider;

import java.io.InputStream;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MoreLoginActivityb extends Activity implements
		ConnectionCallbacks, OnConnectionFailedListener{

	private static final String TAG = "error";
	private GoogleApiClient mGoogleApiClient;
	private boolean mSignInClicked;
	ImageView imgProfil;
	private static final int RC_SIGN_IN = 0;
	private static final int PROFILE_PIC_SIZE = 400;
	TextView txtName;
	Button btn;
	EditText text;
	EditText text2;
	EditText text3;
	String personName="";
	String personID="";
	String email="";
	String from="";
	private ProgressDialog pDialog;
	private static String URL_Insert = AppConst.USER_UPDATE;
	GPSTracker gps;
	double latitude;
	double longtitude;
	private static String URL_GEOUPDATE= AppConst.GEOMETRY_UPDATE;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driveranduserlogin);

		txtName = (TextView) findViewById(R.id.textView1);
		imgProfil = (ImageView) findViewById(R.id.proimg);
		text = (EditText) findViewById(R.id.edtxt);
		text2 = (EditText) findViewById(R.id.edtxt2);
		text3 = (EditText) findViewById(R.id.edtxt3);
		btn = (Button) findViewById(R.id.btnlogin);
		
		
		Bundle bundle = getIntent().getExtras();
        from= bundle.getString("from");
        
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API, new Plus.PlusOptions.Builder().build()) // note
																			// the
																			// options
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new LoadGeo().execute();
			}
		});
      
		gps = new GPSTracker(this);
		if (gps.canGetLocation()) {
			latitude = gps.getLatitude();
			longtitude = gps.getLongtitude();
		} else {
			gps.showSettingAllerts();
		}
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

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		// Get user's information
		getProfileInformation();

	}

	private void getProfileInformation() {
		try {

			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				// if (imageLoader == null)
				// imageLoader = AppController.getInstance().getImageLoader();
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				personName = currentPerson.getDisplayName();
				personID = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				if(personID.contains(" ")){
	        		personID= personID.substring(0, personID.indexOf(" ")); 
	        	}
				Log.d("Data", "Name: " + personName + ", plusProfile: "
						+ personGooglePlusProfile + ", email: " + email
						+ ", Image: " + personPhotoUrl);

				SharedPreferences.Editor editor = getSharedPreferences(
						"conetext", Context.MODE_PRIVATE).edit();
				editor.putString("userid", personID);
				editor.putString("persontype", from);
				editor.commit();
				// String
				// uri="http://www.veneziasi.it/images/stories/ico__64_w-22.gif";
				txtName.setText(personName);

				String PhotoUrl = personPhotoUrl.substring(0,
						personPhotoUrl.length() - 2)
						+ PROFILE_PIC_SIZE;

				new LoadProfileImage(imgProfil).execute(PhotoUrl);

			} else {
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public LoadProfileImage(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btnsigin:
//			new LoadGeo().execute();
//			
//			break;
//		}
//
//	}
//	
	private class LoadGeo extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MoreLoginActivityb.this);
            pDialog.setMessage("Loading...");
            pDialog.show();
// 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
        	
        	Map<String, String> params = new HashMap<String, String>();
			params.put("userid", personID);
			params.put("username",personName);
			params.put("usertype",from);
			params.put("address", text2.getText().toString());
			params.put("email",email);
			params.put("userno", text.getText().toString());
			
        	JsonObjectRequest req = new JsonObjectRequest(URL_Insert, new JSONObject(params),
        	       new Response.Listener<JSONObject>() {
        	           @Override
        	           public void onResponse(JSONObject response) {
        	               try {
        	            	   Log.d("map", "msg");
        	                   VolleyLog.v("Response:%n %s", response.toString(4));
        	               } catch (JSONException e) {
        	                   e.printStackTrace();
        	               }
        	           }
        	       }, new Response.ErrorListener() {
        	           @Override
        	           public void onErrorResponse(VolleyError error) {
        	               VolleyLog.e("Error: ", error.getMessage());
        	           }
        	       });

        	// add the request object to the queue to be executed
        	AppController.getInstance().addToRequestQueue(req);
			

    		return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            hidePDialog();
            
            	Map<String, String> params = new HashMap<String, String>();
    			params.put("userid", personID);
    			params.put("usertype", "user");
    			params.put("lat", String.valueOf(latitude));
    			params.put("lng", String.valueOf(longtitude));

    			JsonObjectRequest req = new JsonObjectRequest(URL_GEOUPDATE,
    					new JSONObject(params),
    					new Response.Listener<JSONObject>() {
    						@Override
    						public void onResponse(JSONObject response) {
    							try {
    								Log.d("map", "msg");
    								VolleyLog.v("Response:%n %s",
    										response.toString(4));
    							} catch (JSONException e) {
    								e.printStackTrace();
    							}
    						}
    					}, new Response.ErrorListener() {
    						@Override
    						public void onErrorResponse(VolleyError error) {
    							VolleyLog.e("Error: ", error.getMessage());
    						}
    					});

    			// add the request object to the queue to be executed
    			AppController.getInstance().addToRequestQueue(req);
            	Intent i = new Intent(MoreLoginActivityb.this,
    					MetroHome.class);
    			startActivity(i);
            
        }
 
    }
	
	private void hidePDialog() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

}
