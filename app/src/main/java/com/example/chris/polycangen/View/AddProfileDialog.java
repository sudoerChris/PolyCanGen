package com.example.chris.polycangen.View;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chris.polycangen.Infrastructure.UserDBHandler;
import com.example.chris.polycangen.R;

public class AddProfileDialog extends DialogFragment {
	
	private static final String ARG_ACTIONTYPE = "param1";
	private static final String ARG_PROFILENAME = "param2";
	private static final String ARG_TITLE = "param3";
	TextView errorMsg;
	EditText nameEditText;
	String title = "";
	String profileName;
	ActionType actionType;
	enum ActionType{
		NEW,EDIT
	}
	public static AddProfileDialog newInstance(String title){
		AddProfileDialog newDialog = new AddProfileDialog();
		Bundle args = new Bundle();
		args.putString(ARG_TITLE,title);
		args.putSerializable(ARG_ACTIONTYPE,ActionType.NEW);
		newDialog.setArguments(args);
		return newDialog;
	}
	public static AddProfileDialog newInstance(String title, String profileName){
		AddProfileDialog newDialog = new AddProfileDialog();
		Bundle args = new Bundle();
		args.putString(ARG_TITLE,title);
		args.putSerializable(ARG_ACTIONTYPE,ActionType.EDIT);
		args.putString(ARG_PROFILENAME,profileName);
		newDialog.setArguments(args);
		return newDialog;
	}
	private void showErrorMsg(String msg){
		errorMsg.setText(msg);
		errorMsg.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!=null){
			title = getArguments().getString(ARG_TITLE);
			actionType = (ActionType)getArguments().getSerializable(ARG_ACTIONTYPE);
			if(actionType==ActionType.EDIT){
				profileName = getArguments().getString(ARG_PROFILENAME);
			}
		}
	}
	
	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setTitle(title);
		final View view = inflater.inflate(R.layout.fragment_add_profile_dialog, null);
		builder.setView(view);
		nameEditText = (EditText) view.findViewById(R.id.nameEditText);
		errorMsg = (TextView) view.findViewById(R.id.errorMsg);
		builder.setPositiveButton("OK",null);
		builder.setNegativeButton("Cancel", (dialog, id) -> {
			// User cancelled the dialog
			AddProfileDialog.this.getDialog().cancel();
			getTargetFragment()
					.onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED,
							getActivity().getIntent());
		});
		if(actionType==ActionType.EDIT){
			nameEditText.setText(profileName);
		}
		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE)
				.setOnClickListener(v -> {
					errorMsg.setVisibility(View.GONE);
					if(nameEditText.getText().toString().length()==0){
						showErrorMsg("Profile Name is empty!");
						return;
					}
					if(actionType==ActionType.NEW) {
						if (!UserDBHandler.addProfile(nameEditText.getText().toString())) {
							showErrorMsg("Profile Name already exist!");
							return;
						}
						
					}else{
						UserDBHandler.editProfile(profileName,nameEditText.getText().toString());
					}
					AddProfileDialog.this.getDialog().cancel();
					getTargetFragment()
							.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
									getActivity().getIntent());
				});
		return dialog;
	}
}

