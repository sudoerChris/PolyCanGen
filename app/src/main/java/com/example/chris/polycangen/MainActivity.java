package com.example.chris.polycangen;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.chris.polycangen.Infrastructure.UserDBHandler;
import com.example.chris.polycangen.View.HomeFragment;
import com.example.chris.polycangen.View.ProfileFragment;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		UserDBHandler.CheckDB(getApplicationContext());
		Fragment fragment = new HomeFragment();
		getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment, HomeFragment.tag).commit();
	}
	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			if(getFragmentManager().getBackStackEntryCount()>0){
				getFragmentManager().popBackStackImmediate();
			}else {
				super.onBackPressed();
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		while(getFragmentManager().getBackStackEntryCount()>0) {
			getFragmentManager().popBackStackImmediate();
		}
		Fragment newFragment = null;
		String tag = null;
		int id = item.getItemId();
		if (id == R.id.nav_home) {
		} else if (id == R.id.nav_profile) {
			newFragment = new ProfileFragment();
			tag = ProfileFragment.tag;
		} else if (id == R.id.nav_setting) {
		}else{
		}
		DrawerLayout drawer =  (DrawerLayout)findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		if(newFragment!=null) {
			getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_frame,newFragment,tag).commit();
		}
		
		return true;
	}
}
