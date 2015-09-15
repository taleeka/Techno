package com.android.metrorider;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MoreLoginActivitya extends Activity {

	Button btndriversigin;
	Button btnusersignin;
	TextView textskip;
    String type;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metro_portallogin);

		btndriversigin = (Button) findViewById(R.id.btnDriverSignin);
		btnusersignin = (Button) findViewById(R.id.btnUserSignin);
        textskip =(TextView)findViewById(R.id.textskip);
		
		btndriversigin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MoreLoginActivitya.this,
						MoreLoginActivityc.class);
				i.putExtra("from", "driver");
				startActivity(i);
			}
		});

		btnusersignin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MoreLoginActivitya.this,
						MoreLoginActivityb.class);
				i.putExtra("from", "user");
				startActivity(i);
			}
		});
		
		textskip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences prefs = getSharedPreferences("conetext",
						Context.MODE_PRIVATE);
				type = prefs.getString("persontype", null);
				if(type.equals("user")){
				  Intent i = new Intent(MoreLoginActivitya.this,
							MetroHome.class);
					startActivity(i);
				}else{
					 Intent i = new Intent(MoreLoginActivitya.this,
								MetroHome2.class);
						startActivity(i);
				}
			}
		});
	}

}
