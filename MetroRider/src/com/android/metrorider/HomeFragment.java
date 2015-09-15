package com.android.metrorider;



import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.app.AppController;
import com.android.common.GPSTracker;
import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint({ "NewApi", "InlinedApi" })
public class HomeFragment extends Fragment {

	private static final String TAG = "error";
	private GoogleMap googleMap;
	private SupportMapFragment mapFrag;
	GPSTracker gps;
	double latitude;
	double longtitude;
	private String URL_FEED = "https://maps.googleapis.com/maps/api/directions/json?origin=Colombo&destination=Kandy&key=AIzaSyDCNmT2IS2op-Aue_AGtezu0Qp0ysGIXH0";
	JSONArray routearr = null;
	JSONArray legsarr = null;
	String code;
	double lng;
	double lat;
	String spos;
	PolylineOptions options;
	List<LatLng> list;
	private LinearLayout rowContainer;
	 private static final int SCALE_DELAY = 30;
	@SuppressLint({ "NewApi", "InlinedApi" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("responsemsg","initialize gps");
		
		try {
			// Loading map
			initilizeMap();
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(true);
			//
			// // Enable / Disable my location button
//			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			//
			// // Enable / Disable Compass icon
			googleMap.getUiSettings().setCompassEnabled(true);
			//
			// // Enable / Disable Rotate gesture
			googleMap.getUiSettings().setRotateGesturesEnabled(true);
			//
			// // Enable / Disable zooming functionality
			googleMap.getUiSettings().setZoomGesturesEnabled(true);
			// PolylineOptions option=new
			// PolylineOptions().add(cld).add(nld).width(5).color(Color.BLUE).geodesic(true);
			// googleMap.addPolyline(option);
			// googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cld, 13));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		gps = new GPSTracker(getActivity().getApplicationContext());
		if (gps.canGetLocation()) {
			latitude = gps.getLatitude();
			longtitude = gps.getLongtitude();
		} else {
			gps.showSettingAllerts();
		}
		
			// making fresh volley request and getting json
			JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
					URL_FEED, null, new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							VolleyLog.d(TAG,
									"Response: " + response.toString());
							Log.d("responsemsg", response.toString());
							if (response != null) {
							
								parseJsonFeed(response);
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							VolleyLog.d(TAG, "Error: " + error.getMessage());
						}
					});

			// Adding request to volley request queue
			AppController.getInstance().addToRequestQueue(jsonReq);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map_layout, container, false);
		
		
		// Inflate the layout for this fragment
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private void initilizeMap() {
		try {
			
			// Loading map
//			if (googleMap == null) {
//				googleMap = ((SupportMapFragment) getFragmentManager()
//						.findFragmentById(R.id.map)).getMap();
//			}
//			googleMap = ((MapFragment)getActivity(). getFragmentManager().findFragmentById(
//					R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				googleMap = ((MapFragment)getActivity(). getFragmentManager().findFragmentById(
						R.id.map)).getMap();

				// check if map is created successfully or not
				if (googleMap == null) {
					Toast.makeText(getActivity().getApplicationContext(),
							"Sorry! unable to create maps", Toast.LENGTH_SHORT)
							.show();
				}
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		initilizeMap();
	}

	private void parseJsonFeed(JSONObject response) {
		try {
			JSONArray feedArray = response.getJSONArray("routes");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);
				
				routearr = feedObj.getJSONArray("legs");
				Log.d("mmm", "hhh" + routearr);
				for (int j = 0; j < routearr.length(); j++) {
					JSONObject feedObj2 = (JSONObject) routearr.get(j);
					legsarr =feedObj2.getJSONArray("steps");
					Log.d("jj", "jj" + legsarr);
					for (int k = 0; k < legsarr.length(); k++) {
						JSONObject c2 = legsarr
								.getJSONObject(k);
						JSONObject c3 = c2
								.getJSONObject("polyline");
						JSONObject c4 = c2
								.getJSONObject("start_location");
						
						
						lat = c4.getDouble("lat");
						lng = c4.getDouble("lng");
						
					
						spos = c3.getString("points");
					
						list = decodePoly(spos);
						Log.d("point", "points" + list);
						options = new PolylineOptions()
						.width(10)
						.color(Color.GREEN)
						.geodesic(true);
						for (int z = 0; z < list.size(); z++) {
							
							LatLng point = list.get(z);
							options.add(point);
							
						}
						
						googleMap.addPolyline(options);
						
						
					}
					Log.d("pp", "pp"+options);
					
				}
				
			}
		

			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}
}