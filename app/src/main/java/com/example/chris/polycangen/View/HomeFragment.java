package com.example.chris.polycangen.View;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.chris.polycangen.Common;
import com.example.chris.polycangen.Controller.GenResult;
import com.example.chris.polycangen.Infrastructure.UserDBHandler;
import com.example.chris.polycangen.Model.ItemNode;
import com.example.chris.polycangen.R;

import java.util.ArrayList;
import java.util.Date;

import static com.example.chris.polycangen.Common.StandardTimeSdf;

public class HomeFragment extends Fragment implements GenResult.GenResultIF {
	public final static String tag="HomeFragment";
	LinearLayout clickArea;
	TextView resultTextView, hintTextView,timeTextView;
	Spinner profileSelector;
	GenResult generator = null;
	String[] profileList = null;
	ItemNode[] itemList = null;
	
	private void setResultText(String contentText,String hintText){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int textSize = ((Double)((displayMetrics.widthPixels*0.9)/contentText.length())).intValue();
		if(textSize>400) {
			resultTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 400);
		}
		else if (textSize < 60) {
			resultTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 60);
		}else{
			resultTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, textSize);
		}
		resultTextView.setText(contentText);
		hintTextView.setText(hintText);
	}
	@Override
	public void GRDone(ItemNode node, String hint) {
		setResultText(node.ItemName,hint);
		timeTextView.setText("Generated at: "+StandardTimeSdf.format(new Date()));
	}
	
	@Override
	public void GRUpdate(ItemNode node, String hint) {
		setResultText(node.ItemName,hint);
	}
	public void refreshData(){
		profileList = UserDBHandler.getProfileStr();
		if(profileList.length>0) {
			refreshItem(profileList[0]);
		}else{
			getActivity()
					.getFragmentManager()
					.beginTransaction()
					.addToBackStack(null)
					.replace(R.id.main_frame,new ProfileFragment(),ProfileFragment.tag)
					.commit();
		}
	}
	public void refreshItem(String profileName){
		int time = Integer.parseInt( Common.TimeSdf.format(new Date()));
		ArrayList<ItemNode> _itemArrayList = new ArrayList<>();
		for(ItemNode node:UserDBHandler.getItems(profileName)){
			if((node.EnableTime==-1||node.EnableTime<time)&&(node.DisableTime==-1||node.DisableTime>time)){
				_itemArrayList.add(node);
			}
		}
		itemList = _itemArrayList.toArray(new ItemNode[0]);
		if(itemList.length==0) {
			setResultText("冇野好食啊", "");
		}else{
			setResultText("唔知食乜好?","撳我");
		}
	}
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public void onStart() {
		super.onStart();
		refreshData();
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view,savedInstanceState);
		clickArea = (LinearLayout) view.findViewById(R.id.clickArea);
		resultTextView = (TextView) view.findViewById(R.id.resultTextView);
		hintTextView = (TextView)view.findViewById(R.id.hintTextView);
		profileSelector = (Spinner) view.findViewById(R.id.profileSelector);
		timeTextView = (TextView)view.findViewById(R.id.timeTextView);
		refreshData();
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),  android.R.layout.simple_spinner_dropdown_item,profileList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		profileSelector.setAdapter(adapter);
		clickArea.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshItem(profileList[profileSelector.getSelectedItemPosition()]);
				if(itemList==null||itemList.length==0){
					return;
				}
				if(generator!=null){
					generator.cancel(false);
				}
				generator = new GenResult(HomeFragment.this.itemList,HomeFragment.this);
				generator.execute();
				timeTextView.setText("");
			}
		});
		profileSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				refreshItem((String)adapterView.getItemAtPosition(i));
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}
}
