package com.ving.accountpasswords;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.security.PrivateKey;
import java.security.PublicKey;

import com.ving.accountpasswords.MyApplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class ReadKeyFiles extends AsyncTask<MyApplication, Integer, MyApplication> {
	private Context mContext;
	private ProgressDialog pd = null;
	private String errorMsg = null;
	private File dir = null;
	private File PRIVATE_KEY_FILE;
	private File PUBLIC_KEY_FILE;
	
	ReadKeyFiles(Context context) {
		mContext = context;
		dir = new File(Environment.getExternalStorageDirectory().toString(),"/RSAKeys");
		PRIVATE_KEY_FILE = new File(dir,"RSA.private.key");
		PUBLIC_KEY_FILE = new File(dir,"RSA.public.key");
	}
	
	protected void onPreExecute() {
	    pd = ProgressDialog.show(mContext, "Reading", "Reading Key Files");
	}
	
	protected MyApplication doInBackground(MyApplication... myApps) {
		PublicKey publicKey = null;
		PrivateKey privateKey = null;
		ObjectInputStream inputStream = null;
		try {
			inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
		} catch (IOException e1) {
			e1.printStackTrace();
			errorMsg = e1.toString();
			cancel(true);
		}
		if (! isCancelled()) {
			try {
				publicKey = (PublicKey) inputStream.readObject();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				errorMsg = e1.toString();
				cancel(true);
			} catch (OptionalDataException e1) {
				e1.printStackTrace();
				errorMsg = e1.toString();
				cancel(true);
			} catch (IOException e1) {
				e1.printStackTrace();
				errorMsg = e1.toString();
				cancel(true);
			}
		}
		if (! isCancelled()) {
			try {
				inputStream.close();
				inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
			} catch (IOException e1) {
				e1.printStackTrace();
				errorMsg = e1.toString();
				cancel(true);
			}
		}
		if (! isCancelled()) {
			try {
				privateKey = (PrivateKey) inputStream.readObject();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				errorMsg = e1.toString();
				cancel(true);
			} catch (OptionalDataException e1) {
				e1.printStackTrace();
				errorMsg = e1.toString();
				cancel(true);
			} catch (IOException e1) {
				e1.printStackTrace();
				errorMsg = e1.toString();
				cancel(true);
			}
		}
		if (! isCancelled()) {
			try {
			inputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				errorMsg = e1.toString();
				cancel(true);
			}
		}
		if (! isCancelled()) {
			myApps[0].setKeys(publicKey, privateKey);
		}
		return myApps[0];
	}
	
	protected void onProgressUpdate(Integer... progress) {

    }
	
	protected void onCancelled(MyApplication myApp) {
		pd.cancel();
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	    builder.setTitle("No Data Retrieved");
	    builder.setMessage("Sorry, there was an error trying to read the RSA Keys.\n" + errorMsg);
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
			ReadPasswordData readPWDTask = new ReadPasswordData(mContext);
			readPWDTask.execute(myApp);
		}
	}

}
