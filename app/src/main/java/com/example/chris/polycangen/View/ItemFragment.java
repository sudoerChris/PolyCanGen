package com.example.chris.polycangen.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chris.polycangen.Infrastructure.UserDBHandler;
import com.example.chris.polycangen.Model.ItemNode;
import com.example.chris.polycangen.Model.ProfileNode;
import com.example.chris.polycangen.R;

import java.util.Locale;

public class ItemFragment extends Fragment {
	private static final String ARG_PROFILENAME = "param1";
	public static final String tag = "ItemFragment";
	private String mProfile;
	private ItemNode[] itemList;
	private ListView itemListView;
	public static ItemFragment newInstance(String profileName) {
		ItemFragment fragment = new ItemFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PROFILENAME, profileName);
		fragment.setArguments(args);
		return fragment;
	}
	public void refreshList(){
		itemList = UserDBHandler.getItems(mProfile);
		ItemAdapter itemAdapter = new ItemAdapter(ItemFragment.this, itemList);
		itemListView.setAdapter(itemAdapter);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mProfile = getArguments().getString(ARG_PROFILENAME);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.list_with_add, container, false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		itemListView = (ListView)view.findViewById(R.id.listView);
		refreshList();
		itemListView.setOnCreateContextMenuListener((menu, v, menuInfo) -> getActivity().getMenuInflater().inflate(R.menu.row_menu, menu));
		FloatingActionButton addFAB = (FloatingActionButton)view.findViewById(R.id.addFAB);
		addFAB.setOnClickListener(view12 -> {
			AddItemDialog dialog = AddItemDialog.newInstance("Add Profile",mProfile);
			dialog.show( getFragmentManager(), "NoticeDialogFragment");
			dialog.setTargetFragment(ItemFragment.this,1);
		});
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode== Activity.RESULT_OK){
			refreshList();
		}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.action_delete:
				UserDBHandler.deleteItem(itemList[menuInfo.position]);
				refreshList();
				break;
			case  R.id.action_edit:
				AddItemDialog dialog = AddItemDialog.newInstance("Edit Item",itemList[menuInfo.position]);
				dialog.show( getFragmentManager(), "NoticeDialogFragment");
				dialog.setTargetFragment(ItemFragment.this,1);
				break;
		}
		
		return super.onContextItemSelected(item);
	}
	private class ItemAdapter extends ArrayAdapter<ItemNode> {
		ItemAdapter(ItemFragment a, ItemNode[] data) {
			super(a.getActivity(), R.layout.item_list_item, data);
		}
		@Override
		@NonNull
		public View getView(int position, View convertView,@NonNull ViewGroup parent) {
			
			ViewHolder holder;
			LayoutInflater inflater = getActivity().getLayoutInflater();
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_list_item, parent, false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			setText(holder, position);
			return convertView;
		}
		void setText(ViewHolder holder, int position) {
			ItemNode node = this.getItem(position);
			if(node==null){
				return;
			}
			holder.getName().setText(node.ItemName);
			holder.getWeight().setText(String.format(Locale.US,"%d",node.Weight));
			holder.getEnableTime().setText(node.EnableTime<=0?"Always":String.format(Locale.US,"%04d",node.EnableTime));
			holder.getDisableTime().setText(node.DisableTime<0?"Always":String.format(Locale.US,"%04d",node.DisableTime));
		}
	}
	
	private class ViewHolder {
		private View row;
		private TextView name = null, weight = null, enableTime = null,disableTime = null;
		ViewHolder(View row) {
			this.row = row;
		}
		
		TextView getName() {
			if (this.name == null) {
				this.name = (TextView) row.findViewById(R.id.name);
			}
			return this.name;
		}
		TextView getWeight() {
			if (this.weight == null) {
				this.weight = (TextView) row.findViewById(R.id.weight);
			}
			return this.weight;
		}
		TextView getEnableTime(){
			if(this.enableTime == null){
				this.enableTime = (TextView) row.findViewById(R.id.enableTime);
			}
			return this.enableTime;
		}
		TextView getDisableTime(){
			if(this.disableTime == null){
				this.disableTime = (TextView) row.findViewById(R.id.disableTime);
			}
			return disableTime;
		}
	}
}
