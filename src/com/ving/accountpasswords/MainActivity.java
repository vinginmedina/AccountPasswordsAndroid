package com.ving.accountpasswords;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import com.ving.accountpasswords.MyApplication;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {
	
	private MyApplication myApp = null;
	private Context mContext = null;
	ListView listView = null;
	private ArrayAdapter<PasswordData> adapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myApp = (MyApplication) getApplication();
		setContentView(R.layout.activity_main);
		mContext = this;
		myApp.setUrlVing();
		myApp.setPostUrlVing();
		Button saveBtn = (Button)findViewById(R.id.saveBtn);
		myApp.saveStuff(mContext, saveBtn);
		adapter = new PasswordListAdapter(mContext,android.R.layout.simple_list_item_2,myApp.getExpArray());
		listView = (ListView) findViewById(R.id.passwordDataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((OnItemClickListener) mContext);
        listView.setOnItemLongClickListener((OnItemLongClickListener) mContext);
        myApp.setExpandAdapter(adapter);
		ReadKeyFiles readRSATask = new ReadKeyFiles(mContext);
		readRSATask.execute(myApp);
	}
	
	public void onClickCallback (View target) {
		
		switch (target.getId()) {
		case R.id.refreshBtn:
			listView.invalidateViews();
			myApp.refresh();
			adapter = new PasswordListAdapter(mContext,android.R.layout.simple_list_item_2,myApp.getExpArray());
			listView.setAdapter(adapter);
			myApp.setExpandAdapter(adapter);
			ReadPasswordData readPWDTask = new ReadPasswordData(mContext);
			readPWDTask.execute(myApp);
			break;
		case R.id.newBtn:
			View dialogView = getLayoutInflater().inflate(R.layout.add_data_dialog,null);
			Button pos = (Button)dialogView.findViewById(R.id.enter);
			Button neg = (Button)dialogView.findViewById(R.id.cancel);
			final EditText account = (EditText)dialogView.findViewById(R.id.account);
			final EditText userid = (EditText)dialogView.findViewById(R.id.userid);
			final EditText password = (EditText)dialogView.findViewById(R.id.password);
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setView(dialogView);
	        dialog.setTitle("Enter new values");
	        dialog.setCancelable(false);
	        final AlertDialog enterDataDialog = dialog.create();
	        neg.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					enterDataDialog.dismiss();
				}
			});
	        pos.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(password.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					if ((account.getText() != null) && (userid.getText() != null) && (password.getText() != null)) {
						myApp.save(account.getText().toString(),userid.getText().toString(),password.getText().toString());
						myApp.notifyDataSetChanged();
						myApp.dataUpdated();
						enterDataDialog.dismiss();
					}
				}
	        });
			enterDataDialog.show();
		    break;
		case R.id.saveBtn:
			PostPasswordData postPWDTask = new PostPasswordData(mContext);
			postPWDTask.execute(myApp);
			break;
		default:
			break;
		}
	}
	
	public void onItemClick(AdapterView<?> arg0, View target, int position, long id) {
        AlertDialog.Builder passwordDialog = new AlertDialog.Builder(mContext);
        passwordDialog.setTitle(myApp.getExpArray().get(position).getAccount());
        passwordDialog.setMessage("User ID: "+myApp.getExpArray().get(position).getUserId()+"\nPassword: "+myApp.getExpArray().get(position).getPassword());
        passwordDialog.setCancelable(false);
        passwordDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di,int id) {
            	di.dismiss();
            }
        });
        passwordDialog.show();
	}
	
	public boolean onItemLongClick(AdapterView<?> arg0, View target, int position, long id) {
		final int row = position;
		final String account = myApp.getExpArray().get(row).getAccount();
		View dialogView = getLayoutInflater().inflate(R.layout.update_data_dialog,null);
		Button save = (Button)dialogView.findViewById(R.id.enter);
		Button cancel = (Button)dialogView.findViewById(R.id.cancel);
		Button delete = (Button)dialogView.findViewById(R.id.delete);
		final EditText userid = (EditText)dialogView.findViewById(R.id.userid);
		userid.setText(myApp.getExpArray().get(row).getUserId());
		final EditText password = (EditText)dialogView.findViewById(R.id.password);
		password.setText(myApp.getExpArray().get(row).getPassword());
		((TextView)dialogView.findViewById(R.id.account)).setText(account);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setView(dialogView);
        dialog.setTitle("Update the values");
        dialog.setCancelable(false);
        final AlertDialog enterDataDialog = dialog.create();
        cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				enterDataDialog.dismiss();
			}
		});
        delete.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		myApp.getExpArray().remove(row);
        		myApp.notifyDataSetChanged();
        		myApp.dataUpdated();
        		enterDataDialog.dismiss();
        	}
        });
        save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(password.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				if ((userid.getText() != null) && (password.getText() != null)) {
					myApp.getExpArray().get(row).update(account,userid.getText().toString(),password.getText().toString());
					myApp.notifyDataSetChanged();
					myApp.dataUpdated();
					enterDataDialog.dismiss();
				}
			}
        });
		enterDataDialog.show();
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if (myApp.hasDataChanged()) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("Password Data Not Saved, Exit Anyway?");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Go Back and Save",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                	dialog.cancel();
                }
            });
            dialog.setNegativeButton("Exit Anyway", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    finish();
                }
            });
            dialog.show();
		} else {
			finish();
		}
	}

}
