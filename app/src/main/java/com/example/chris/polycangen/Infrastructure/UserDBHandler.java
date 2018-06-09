package com.example.chris.polycangen.Infrastructure;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.chris.polycangen.Model.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import static android.database.sqlite.SQLiteDatabase.openDatabase;

public class UserDBHandler {
	private static String DB_PATH = null;
	private final static String DB_NAME = "userdata.db";
	public static void CheckDB(Context context) {
		DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
		String filePath = DB_PATH + DB_NAME;
		if (!new File(filePath).exists()) {
			newDB(context);
		}
		try {
			SQLiteDatabase db =  SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READWRITE);
			db.close();
		} catch (SQLiteCantOpenDatabaseException SCDEx) {
			newDB(context);
		}
	}
	private static void newDB(Context context){
		try {
			if(! new File(DB_PATH).exists()){
				new File(DB_PATH).mkdirs();
			}
			InputStream mInput = context.getAssets().open(DB_NAME);
			String outFileName = DB_PATH + DB_NAME;
			File dbFile = new File(DB_PATH + DB_NAME);
			dbFile.createNewFile();
			OutputStream mOutput = new FileOutputStream(outFileName);
			byte[] mBuffer = new byte[1024];
			int mLength;
			while ((mLength = mInput.read(mBuffer)) > 0) {
				mOutput.write(mBuffer, 0, mLength);
			}
			mOutput.flush();
			mOutput.close();
			mInput.close();
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	private static SQLiteDatabase getDB(){
		if(DB_PATH==null){
			throw new NullPointerException("DB_PATH is null!");
		}
		return openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
	}
	public static ProfileNode[] getProfileNode(){
		ArrayList<ProfileNode> result = new ArrayList<>();
		SQLiteDatabase mDatabase = getDB();
		Cursor cursor = mDatabase.rawQuery("select Profile.profileName,count(ItemName) from Profile left join ProfileItem on Profile.ProfileName=ProfileItem.ProfileName group by Profile.ProfileName",null);
		cursor.moveToFirst();
		if(cursor.isAfterLast()){
			return new ProfileNode[0];
		}
		do{
			ProfileNode newNode = new ProfileNode();
			newNode.name = cursor.getString(0);
			newNode.count = cursor.getInt(1);
			result.add(newNode);
		}while(cursor.moveToNext());
		cursor.close();
		mDatabase.close();
		return result.toArray(new ProfileNode[0]);
	}
	public static String[] getProfileStr(){
		ArrayList<String> result = new ArrayList<>();
		SQLiteDatabase mDatabase = getDB();
		Cursor cursor = mDatabase.rawQuery("select distinct ProfileName from Profile",null);
		cursor.moveToFirst();
		if(cursor.isAfterLast()){
			return new String[0];
		}
		do{
			result.add(cursor.getString(0));
		}while(cursor.moveToNext());
		cursor.close();
		mDatabase.close();
		return result.toArray(new String[0]);
	}
	public static boolean addProfile(String profileName){
		SQLiteDatabase mDatabase = getDB();
		try {
			mDatabase.execSQL("insert into Profile (profileName) values (?)", new String[]{profileName});
			
		}catch (SQLiteConstraintException SQLCEx){
			Log.e("DB","exist!");
			mDatabase.close();
			return false;
		}
		mDatabase.close();
		return true;
	}
	public static void deleteProfile(String profileName){
		SQLiteDatabase mDatabase = getDB();
		mDatabase.execSQL("delete from Profile where profileName = ?", new String[]{profileName});
		mDatabase.execSQL("delete from ProfileItem where profileName = ?", new String[]{profileName});
		mDatabase.close();
	}
	public static void editProfile(String oldProfile, String newProfile){
		SQLiteDatabase mDatabase = getDB();
		mDatabase.execSQL("update Profile set ProfileName=? where ProfileName=?", new String[]{newProfile,oldProfile});
		mDatabase.execSQL("update ProfileItem set ProfileName=? where ProfileName=?", new String[]{newProfile,oldProfile});
		mDatabase.close();
	}
	public static ItemNode[] getItems(String profileName){
		ArrayList<ItemNode> result = new ArrayList<>();
		SQLiteDatabase mDatabase = getDB();
		Cursor cursor = mDatabase.rawQuery("SELECT ItemName,Weight,EnableTime,DisableTime,ID FROM ProfileItem WHERE ProfileName = ?",new String[] {profileName});
		cursor.moveToFirst();
		if(cursor.isAfterLast()){
			return new ItemNode[0];
		}
		do{
			ItemNode newNode = new ItemNode();
			newNode.ProfileName = profileName;
			newNode.ItemName = cursor.getString(0);
			newNode.Weight = cursor.getInt(1);
			newNode.EnableTime = cursor.isNull(2)?-1: cursor.getInt(2);
			newNode.DisableTime =cursor.isNull(3)?-1: cursor.getInt(3);
			newNode.ID = cursor.getInt(4);
			result.add(newNode);
		}while(cursor.moveToNext());
		cursor.close();
		mDatabase.close();
		return result.toArray(new ItemNode[0]);
	}
	public static void addItem(ItemNode node){
		SQLiteDatabase mDatabase = getDB();
		ContentValues cv = new ContentValues();
		cv.put("ItemName",node.ItemName);
		cv.put("ProfileName",node.ProfileName);
		cv.put("Weight",node.Weight);
		if(node.EnableTime>0) {
			cv.put("EnableTime",node.EnableTime);
		}
		if(node.DisableTime>0) {
			cv.put("DisableTime",node.DisableTime);
		}
		mDatabase.insert("ProfileItem",null,cv);
		mDatabase.close();
	}
	public static void deleteItem(ItemNode node){
		SQLiteDatabase mDatabase = getDB();
		mDatabase.execSQL("delete from ProfileItem where ID = ?", new String[]{String.format(Locale.US,"%d",node.ID)});
		mDatabase.close();
	}
	public static void editItem(ItemNode node){
		SQLiteDatabase mDatabase = getDB();
		ContentValues cv = new ContentValues();
		cv.put("ItemName",node.ItemName);
		cv.put("ProfileName",node.ProfileName);
		cv.put("Weight",node.Weight);
		if(node.EnableTime>0) {
			cv.put("EnableTime",node.EnableTime);
		}else{
			cv.putNull("EnableTime");
		}
		if(node.DisableTime>0) {
			cv.put("DisableTime",node.DisableTime);
		}else{
			cv.putNull("DisableTime");
		}
		mDatabase.update("ProfileItem",cv,"ID=?",new String[]{String.format(Locale.US,"%d",node.ID)});
		mDatabase.close();
	}
}
