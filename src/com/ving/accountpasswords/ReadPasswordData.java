package com.ving.accountpasswords;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.ving.accountpasswords.MyApplication;

public class ReadPasswordData extends AsyncTask<MyApplication, Integer, MyApplication> {
	private Context mContext;
	private ProgressDialog pd = null;
	private String errorMsg = null;
	InputStream inp = null;
	URL url = null;
	URLConnection cnx = null;
	InputStreamReader ipsr = null;
	BufferedReader br = null;
	
	ReadPasswordData(Context context) {
		mContext = context;
	}
	
	protected void onPreExecute() {
	    pd = ProgressDialog.show(mContext, "Reading", "Getting Password Data");
	}
	
	protected MyApplication doInBackground(MyApplication... myApps) {
		try {
			url = new URL(myApps[0].urlToUse());
			cnx = url.openConnection();
			cnx.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			cnx.setDoInput(true);
            cnx.setDoOutput(true);
			ipsr = new InputStreamReader(cnx.getInputStream());
			br = new BufferedReader(ipsr);
		} catch (IOException e) {
			e.printStackTrace();
			errorMsg = e.toString();
			cancel(true);
		}
		if (! isCancelled()) {
			String line;
			try {
				while ((line=br.readLine())!=null){
					myApps[0].decodeAndSave(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
				errorMsg = e.toString();
				cancel(true);
			}
			try {
				br.close();
				ipsr.close();
			} catch (IOException e) {
				e.printStackTrace();
				errorMsg = e.toString();
				cancel(true);
			}
		}
		return myApps[0];
	}
	
	protected void onProgressUpdate(Integer... progress) {

    }
	
	protected void onCancelled(MyApplication myApp) {
		pd.cancel();
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	    builder.setTitle("No Data Retrieved");
	    builder.setMessage("Sorry, there was an error trying to get the Password Data.\n" + errorMsg);
	    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int arg1) {
		            dialog.dismiss();
		        }});
	    builder.setCancelable(false);
	    AlertDialog myAlertDialog = builder.create();
	    myAlertDialog.show();
	}
	
	protected void onPostExecute(MyApplication myApp) {
		pd.cancel();
		if (! isCancelled()) {
			myApp.notifyDataSetChanged();
		}
	}

}
