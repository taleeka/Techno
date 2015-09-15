package com.android.metrorider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.app.AppConst;
import com.android.app.AppController;
import com.android.common.GPSTracker;
import com.android.common.PopUpWindow;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class NearByActivity extends Activity implements OnMarkerClickListener {

	private static final String TAG = "error";
	private GoogleMap googleMap;
	private static String URL = AppConst.GEOMETRY;
	private static String URL_UPDATE = AppConst.GEOMETRY_UPDATE;
	private static String GOOGE_DIRECTIONS = AppConst.GOOGLE_DIRECTIONS;
	private static final String TAG_User = "userid";
	private static final String TAG_Route = "route";
	private ProgressDialog pDialog;
	GPSTracker gps;
	double latitude;
	double longtitude;
	String id;
	String type;
	List<Marker> markerList = new ArrayList<Marker>();
	Marker marker;
	boolean suspended = false;
	double distance;
	private static final int MIN_TIME_Notification = 1000 * 30;
	private Polyline poly = null;
	JSONArray routearr = null;
	JSONArray legsarr = null;
	String spos;
	List<LatLng> list;
	PolylineOptions options;
	String state;
	HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby_activity);

		try {
			// Loading map
			initilizeMap();
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			googleMap.getUiSettings().setCompassEnabled(true);
			googleMap.getUiSettings().setRotateGesturesEnabled(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		gps = new GPSTracker(this);
		if (gps.canGetLocation()) {
			latitude = gps.getLatitude();
			longtitude = gps.getLongtitude();
		} else {
			gps.showSettingAllerts();
		}
		SharedPreferences prefs = getSharedPreferences(
				"conetext",
				Context.MODE_PRIVATE);
		state = prefs
				.getString(
						"state",
						null);
		new LoadGeo().execute();

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						sleep(MIN_TIME_Notification);
						if (suspended) {
							showNotification();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();

		googleMap.setOnMarkerClickListener(this);
	}

	private void initilizeMap() {
		try {

			if (googleMap == null) {
				googleMap = ((MapFragment) getFragmentManager()
						.findFragmentById(R.id.map)).getMap();

				// check if map is created successfully or not
				if (googleMap == null) {
					Toast.makeText(getApplicationContext(),
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

	private void hidePDialog() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

	private class LoadGeo extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NearByActivity.this);
			pDialog.setMessage("Loading...");
			pDialog.show();
			//
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			SharedPreferences prefs = getSharedPreferences("conetext",
					Context.MODE_PRIVATE);
			id = prefs.getString("userid", null);
			type = prefs.getString("persontype", null);
			Map<String, String> params = new HashMap<String, String>();
			params.put("userid", id);
			params.put("usertype", type);
			params.put("routeno", "");
			params.put("lat", String.valueOf(latitude));
			params.put("lng", String.valueOf(longtitude));

			JsonObjectRequest req = new JsonObjectRequest(URL_UPDATE,
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

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						while (true) {
							sleep(1000);
							JsonArrayRequest req = new JsonArrayRequest(URL,
									new Response.Listener<JSONArray>() {
										@Override
										public void onResponse(
												JSONArray response) {
											Log.d(TAG, response.toString());
											hidePDialog();
											try {

												googleMap.clear();
												//
												for (int i = 0; i < response
														.length(); i++) {

													JSONObject geo = (JSONObject) response
															.get(i);

													String usertype = geo
															.getString("UserType");
													MarkerOptions markerblue = new MarkerOptions();
													markerblue
															.icon(BitmapDescriptorFactory
																	.fromResource(R.drawable.usermarker44));
													markerblue
															.position(new LatLng(
																	latitude,
																	longtitude));
													googleMap
															.addMarker(markerblue);

													if (usertype
															.equals("driver")) {
														double lat = geo
																.getDouble("Lat");
														double lng = geo
																.getDouble("Lng");

														String userid = geo
																.getString("UserId");
														String route = geo
																.getString("RouteNo");

														if (route.equals("100")) {
															MarkerOptions markerop = new MarkerOptions();
															markerop.icon(BitmapDescriptorFactory
																	.fromResource(R.drawable.busblue2));
															markerop.position(new LatLng(
																	lat, lng));

															marker = googleMap
																	.addMarker(markerop);
														}
														if(route.equals("101")){
															MarkerOptions markerop = new MarkerOptions();
															markerop.icon(BitmapDescriptorFactory
																	.fromResource(R.drawable.busgreen2));
															markerop.position(new LatLng(
																	lat, lng));

															marker = googleMap
																	.addMarker(markerop);
														}
														if(route.equals("02")){
															MarkerOptions markerop = new MarkerOptions();
															markerop.icon(BitmapDescriptorFactory
																	.fromResource(R.drawable.busorange2));
															markerop.position(new LatLng(
																	lat, lng));

															marker = googleMap
																	.addMarker(markerop);
														}
														if(route.equals("154")){
															MarkerOptions markerop = new MarkerOptions();
															markerop.icon(BitmapDescriptorFactory
																	.fromResource(R.drawable.buusred2));
															markerop.position(new LatLng(
																	lat, lng));

															marker = googleMap
																	.addMarker(markerop);
														}
														HashMap<String, String> data = new HashMap<String, String>();
														data.put(TAG_User,
																userid);
														data.put(TAG_Route,
																route);
														extraMarkerInfo.put(
																marker.getId(),
																data);

														distance = CalculationByDistance(
																lat, lng,
																latitude,
																longtitude);

														if (distance < 4) {
															// Load state for
															// notifications
															

															if (state
																	.equals("true")) {
																Log.d("true",
																		"suspeded");
																suspended = true;
															} else {
																suspended = false;
															}

														} else if (distance < 0.5) {
															suspended = false;
														}
													}
												}
											} catch (JSONException e) {
												e.printStackTrace();
												Toast.makeText(
														getApplicationContext(),
														"Error: "
																+ e.getMessage(),
														Toast.LENGTH_LONG)
														.show();
											}
										}
									}, new Response.ErrorListener() {

										@Override
										public void onErrorResponse(
												VolleyError error) {
											VolleyLog.d(
													TAG,
													"Error: "
															+ error.getMessage());
											Toast.makeText(
													getApplicationContext(),
													error.getMessage(),
													Toast.LENGTH_SHORT).show();

										}
									});
							AppController.getInstance().addToRequestQueue(req);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(latitude, longtitude), 16.0f));

		}
	}

	public double CalculationByDistance(double latitude1, double longitude1,
			double latitude2, double longitude2) {
		int Radius = 6371;// radius of earth in Km
		double lat1 = latitude1;
		double lat2 = latitude2;
		double lon1 = longitude1;
		double lon2 = longitude2;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		double valueResult = Radius * c;
		double km = valueResult / 1;
		DecimalFormat newFormat = new DecimalFormat("####");
		int kmInDec = Integer.valueOf(newFormat.format(km));
		double meter = valueResult % 1000;
		int meterInDec = Integer.valueOf(newFormat.format(meter));
		Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
				+ " Meter   " + meterInDec);

		return Radius * c;
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void showNotification() {

		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Intent intent = new Intent(this, NotificationReceiverActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this,
				(int) System.currentTimeMillis(), intent, 0);

		// Build notification
		// Actions are just fake
		Notification noti = new Notification.Builder(this)
				.setContentTitle("Bus is arriving")
				.setContentText("MetroRider")
				.setSmallIcon(R.drawable.iconloader).setContentIntent(pIntent)
				.setSound(soundUri).build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, noti);

	}

	@SuppressWarnings("unchecked")
	public boolean onMarkerClick(Marker marker) {
		final LatLng position = marker.getPosition();
		final LatLng current = new LatLng(latitude, longtitude);
		// if (current != position) {
		// String google_url = GOOGE_DIRECTIONS + "origin=" + current.latitude
		// + "," + current.longitude + "&destination="
		// + position.latitude + "," + position.longitude
		// + "&sensor=false&units=metric&mode=walking";
		// JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
		// google_url, null, new Response.Listener<JSONObject>() {
		//
		// @Override
		// public void onResponse(JSONObject response) {
		// VolleyLog.d(TAG, "Response: " + response.toString());
		// Log.d("responsemsg", response.toString());
		// if (response != null) {
		// parseJsonFeed(response);
		// }
		// }
		// }, new Response.ErrorListener() {
		//
		// @Override
		// public void onErrorResponse(VolleyError error) {
		// VolleyLog.d(TAG, "Error: " + error.getMessage());
		// }
		// });
		//
		// // Adding request to volley request queue
		// AppController.getInstance().addToRequestQueue(jsonReq);
		//
		// }
		HashMap<String, String> marker_data = extraMarkerInfo.get(marker
				.getId());
		String driver = marker_data.get(TAG_User);
		String route = marker_data.get(TAG_Route);
		PopUpWindow popwindow = new PopUpWindow(this, driver, route, position,
				current);
		popwindow.initiatePopupWindow();

		return true;

	}

	private void parseJsonFeed(JSONObject response) {
		try {
			JSONArray feedArray = response.getJSONArray("routes");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);

				JSONObject c3 = feedObj.getJSONObject("overview_polyline");
				spos = c3.getString("points");
				Log.d("spos", "spos" + spos);
				list = decodePoly(spos);
				Log.d("point", "points" + list);
				options = new PolylineOptions().width(10).color(Color.RED)
						.geodesic(true);
				for (int z = 0; z < list.size(); z++) {

					LatLng point = list.get(z);
					options.add(point);

				}

				googleMap.addPolyline(options);

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
