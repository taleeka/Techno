package com.android.metrorider;

import com.google.android.gms.plus.Plus;
import com.google.android.gms.wearable.internal.r;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingActivity extends ActionBarActivity {

	Switch sw;
	private Toolbar mToolbar;
	private String state;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);

		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		// show();

		sw = (Switch) findViewById(R.id.switch1);
		sw.setChecked(true);
		new Load().execute();

		sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					SharedPreferences.Editor editor = getSharedPreferences(
							"conetext", Context.MODE_PRIVATE).edit();
					editor.putString("state", "true");
					editor.commit();

				} else {
					SharedPreferences.Editor editor = getSharedPreferences(
							"conetext", Context.MODE_PRIVATE).edit();
					editor.putString("state", "false");
					editor.commit();
				}

			}
		});

	}

	private class Load extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			SharedPreferences prefs = getSharedPreferences("conetext",
					Context.MODE_PRIVATE);
			state = prefs.getString("state", null);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (state == null) {
				sw.setChecked(false);
			} else {
				if (state.equals("true")) {
					sw.setChecked(true);
				}
			}
			
		}
	}

}
