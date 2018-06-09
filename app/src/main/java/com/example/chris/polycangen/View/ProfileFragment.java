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
import com.example.chris.polycangen.Model.ProfileNode;
import com.example.chris.polycangen.R;
import java.util.Locale;

public class ProfileFragment extends Fragment {
	public static final String tag = "ProfileFragment";
	ProfileNode[] profileList;
	ListView profileListView;
	public void refreshList(){
		profileList = UserDBHandler.getProfileNode();
		ProfileAdapter profileAdapter = new ProfileAdapter(this, profileList);
		profileListView.setAdapter(profileAdapter);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list_with_add, container, false);
	}
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		profileListView = (ListView) view.findViewById(R.id.listView);
		refreshList();
		profileListView.setOnItemClickListener((adapterView, view1, i, l) -> {
			//transform to detail fragment
			getActivity()
					.getFragmentManager()
					.beginTransaction()
					.addToBackStack(null)
					.replace(R.id.main_frame,ItemFragment.newInstance(profileList[i].name),ItemFragment.tag)
					.commit();
		});
		profileListView.setOnCreateContextMenuListener((menu, v, menuInfo) -> getActivity().getMenuInflater().inflate(R.menu.row_menu, menu));
		FloatingActionButton addFAB = (FloatingActionButton)view.findViewById(R.id.addFAB);
		addFAB.setOnClickListener(view12 -> {
			AddProfileDialog dialog = AddProfileDialog.newInstance("Add Profile");
			dialog.show( getFragmentManager(), "NoticeDialogFragment");
			dialog.setTargetFragment(ProfileFragment.this,1);
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
				UserDBHandler.deleteProfile(profileList[menuInfo.position].name);
				refreshList();
				break;
			case  R.id.action_edit:
				AddProfileDialog dialog = AddProfileDialog.newInstance("Edit Profile",profileList[menuInfo.position].name);
				dialog.show( getFragmentManager(), "NoticeDialogFragment");
				dialog.setTargetFragment(ProfileFragment.this,1);
				break;
		}
		
		return super.onContextItemSelected(item);
	}
	private class ProfileAdapter extends ArrayAdapter<ProfileNode> {
		ProfileAdapter(ProfileFragment a, ProfileNode[] data) {
			super(a.getActivity(), R.layout.profile_list_item, data);
		}
		@Override
		@NonNull
		public View getView(int position, View convertView,@NonNull ViewGroup parent) {
			
			ViewHolder holder;
			LayoutInflater inflater = getActivity().getLayoutInflater();
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.profile_list_item, parent, false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			setText(holder, position);
			return convertView;
		}
		void setText(ViewHolder holder, int position) {
			ProfileNode node = this.getItem(position);
			if(node==null){
				return;
			}
			holder.getName().setText(node.name);
			holder.getCount().setText(String.format(Locale.US,"%d",node.count));
		}
	}
	
	private class ViewHolder {
		private View row;
		private TextView name = null, count = null;
		ViewHolder(View row) {
			this.row = row;
		}
		
		TextView getName() {
			if (this.name == null) {
				this.name = (TextView) row.findViewById(R.id.name);
			}
			return this.name;
		}
		TextView getCount() {
			if (this.count == null) {
				this.count = (TextView) row.findViewById(R.id.count);
			}
			return this.count;
		}
	}
}
