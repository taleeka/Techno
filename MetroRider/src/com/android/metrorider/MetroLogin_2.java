package com.android.metrorider;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;





public class MetroLogin extends Activity implements OnClickListener,ConnectionCallbacks,
OnConnectionFailedListener{

	private Button btnSignIn;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private static final int RC_SIGN_IN = 0;
	private ConnectionResult mConnectionResult;
	private LinearLayout llProfileLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metro_loginui);
		btnSignIn = (Button) findViewById(R.id.btngSignin);
		btnSignIn.setOnClickListener(this);
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API, new Plus.PlusOptions.Builder().build()) // note
																	// the
																	// options
		.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}
	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btngSignin:
			if (isNetworkAvailable()) {
				final ProgressDialog ringProgressDialog = new ProgressDialog(
						v.getContext(), ProgressDialog.THEME_HOLO_LIGHT);
				ringProgressDialog.setTitle("Please wait ...");
				ringProgressDialog.setMessage("Log In...");
				ringProgressDialog.show();
				ringProgressDialog.setCancelable(true);

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(4000);
							signInWithGplus();
						} catch (Exception e) {
						}
						ringProgressDialog.dismiss();
					}

				}).start();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						new ContextThemeWrapper(this,
								android.R.style.Theme_Holo_Light));
				builder.setMessage(
						"        Internet connection is not available   ")
						.setPositiveButton("ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										startActivityForResult(
												new Intent(
														android.provider.Settings.ACTION_SETTINGS),
												0);
									}
								})
						.setNegativeButton("cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// User cancelled the dialog
									}
								});
				// Create the AlertDialog object and return it
				builder.create().show();
			}
			break;
		}
	}


	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}
	
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {

				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
				

			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

//		super.onActivityResult(requestCode, resultCode, data);
//		uiHelper.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

//	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;

		// Get user's information
		// getProfileInformation();

		// Update the UI after signin
		if (isNetworkAvailable()) {

			updateUI(true);
		} else {
			Log.d("connect", "connectto internet");
		}
		
	}

	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) {
			Intent i = new Intent(MetroLogin.this, MoreLoginActivitya.class);
			startActivity(i);

		} else {
			llProfileLayout.setVisibility(View.GONE);
		}
	}

//	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		updateUI(false);
		
	}
	
	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}
	
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	

}
