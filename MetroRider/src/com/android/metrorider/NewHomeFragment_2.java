package com.android.metrorider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class NewHomeFragment extends Fragment {

	private ImageButton imgbtbusesroute;
	private ImageButton nearbybuses;
	private ImageButton setting;
	Fragment fragment = null;

	public NewHomeFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.metro_homefragment,
				container, false);

		imgbtbusesroute = (ImageButton) rootView.findViewById(R.id.item_image);
		imgbtbusesroute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// fragment = new HomeFragment();
				// if (fragment != null) {
				// FragmentManager fragmentManager
				// =getActivity().getSupportFragmentManager();
				// FragmentTransaction fragmentTransaction =
				// fragmentManager.beginTransaction();
				// fragmentTransaction.replace(R.id.container_body, fragment);
				// fragmentTransaction.commit();
				//
				// }

				Intent i = new Intent(getActivity().getApplicationContext(),
						HomeActivity.class);
				startActivity(i);

			}
		});

		nearbybuses = (ImageButton) rootView.findViewById(R.id.item_image2);
		nearbybuses.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity().getApplicationContext(),
						NearByActivity.class);
				startActivity(i);

			}
		});
		setting = (ImageButton) rootView.findViewById(R.id.item_image4);
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity().getApplicationContext(),
						SettingActivity.class);
				startActivity(i);

			}
		});
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

}
