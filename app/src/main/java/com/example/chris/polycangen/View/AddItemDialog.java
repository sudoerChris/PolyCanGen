package com.example.chris.polycangen.View;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chris.polycangen.Infrastructure.UserDBHandler;
import com.example.chris.polycangen.Model.ItemNode;
import com.example.chris.polycangen.R;

import java.util.Locale;

public class AddItemDialog extends DialogFragment {
	private static final String ARG_ACTIONTYPE = "param1";
	private static final String ARG_PROFILENAME = "param2";
	private static final String ARG_TITLE = "param3";
	private ActionType actionType;
	private ItemNode node;
	private TextView errorMsg;
	private EditText name,weight,enableTime,disableTime;
	private String title = "",profileName;
	private enum ActionType{
		NEW,EDIT
	}
	public static AddItemDialog newInstance(String title,String profileName){
		AddItemDialog fragment = new AddItemDialog();
		Bundle args = new Bundle();
		args.putSerializable(ARG_ACTIONTYPE, ActionType.NEW);
		args.putString(ARG_TITLE,title);
		args.putString(ARG_PROFILENAME,profileName);
		fragment.setArguments(args);
		return fragment;
	}
	public static AddItemDialog newInstance(String title, ItemNode node){
		AddItemDialog fragment = new AddItemDialog();
		Bundle args = new Bundle();
		args.putSerializable(ARG_ACTIONTYPE, ActionType.EDIT);
		args.putString(ARG_TITLE,title);
		fragment.setArguments(args);
		fragment.node = node;
		return fragment;
	}
	private void showErrorMsg(String msg){
		errorMsg.setText(msg);
		errorMsg.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null){
			title = getArguments().getString(ARG_TITLE);
			actionType = (ActionType)getArguments().getSerializable(ARG_ACTIONTYPE);
			if(actionType==ActionType.NEW){
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
		final View view = inflater.inflate(R.layout.fragment_add_item_dialog, null);
		builder.setView(view);
		name = (EditText) view.findViewById(R.id.name);
		weight = (EditText) view.findViewById(R.id.weight);
		enableTime = (EditText) view.findViewById(R.id.enableTime);
		disableTime = (EditText)view.findViewById(R.id.disableTime);
		errorMsg = (TextView) view.findViewById(R.id.errorMsg);
		builder.setPositiveButton("OK",null);
		builder.setNegativeButton("Cancel", (dialog, id) -> {
			// User cancelled the dialog
			AddItemDialog.this.getDialog().cancel();
			getTargetFragment()
					.onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED,
							getActivity().getIntent());
		});
		if(actionType== ActionType.EDIT){
			name.setText(node.ItemName);
			weight.setText( String.format(Locale.US,"%d",node.Weight));
			if(node.EnableTime>=0) {
				enableTime.setText(String.format(Locale.US, "%04d", node.EnableTime));
			}
			if(node.DisableTime>=0) {
				disableTime.setText(String.format(Locale.US, "%04d", node.DisableTime));
			}
		}
		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE)
				.setOnClickListener(v -> {
					errorMsg.setVisibility(View.GONE);
					if(name.getText().toString().length()==0){
						showErrorMsg("Item Name is empty!");
						return;
					}
					if(weight.getText().toString().length()==0){
						showErrorMsg("Weight is empty!");
						return;
					}
					if(enableTime.getText().toString().length()>0&&Integer.parseInt(enableTime.getText().toString())>2359){
						showErrorMsg("Time range is 0000-2359");
						return;
					}
					if(disableTime.getText().toString().length()>0&&Integer.parseInt(disableTime.getText().toString())>2359){
						showErrorMsg("Time range is 0000-2359");
						return;
					}
					if(actionType== ActionType.NEW) {
						ItemNode newNode = new ItemNode();
						newNode.ItemName = name.getText().toString();
						newNode.Weight = Integer.parseInt( weight.getText().toString());
						newNode.ProfileName = this.profileName;
						if(enableTime.getText().toString().length()>0){
							newNode.EnableTime=Integer.parseInt(enableTime.getText().toString());
						}else{
							newNode.EnableTime = -1;
						}
						if(disableTime.getText().toString().length()>0){
							newNode.DisableTime=Integer.parseInt(disableTime.getText().toString());
						}else{
							newNode.DisableTime = -1;
						}
						
						UserDBHandler.addItem(newNode);
					}else{
						node.ItemName = name.getText().toString();
						node.Weight = Integer.parseInt( weight.getText().toString());
						if(enableTime.getText().toString().length()>0){
							node.EnableTime=Integer.parseInt(enableTime.getText().toString());
						}else{
							node.EnableTime = -1;
						}
						if(disableTime.getText().toString().length()>0){
							node.DisableTime=Integer.parseInt(disableTime.getText().toString());
						}else{
							node.DisableTime = -1;
						}
						UserDBHandler.editItem(node);
					}
					AddItemDialog.this.getDialog().cancel();
					getTargetFragment()
							.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
									getActivity().getIntent());
				});
		return dialog;
	}
}
