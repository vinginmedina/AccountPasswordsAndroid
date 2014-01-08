package com.ving.accountpasswords;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

public class PostPasswordData extends AsyncTask<MyApplication, Integer, MyApplication> {

	private Context mContext;
	private ProgressDialog pd = null;
	private String errorMsg = null;
	private URL url = null;
	private HttpURLConnection connection = null;
	private DataOutputStream wr = null;
	private InputStreamReader is = null;
	private BufferedReader rd = null;

	PostPasswordData(Context context) {
		mContext = context;
	}

	protected void onPreExecute() {
		pd = ProgressDialog.show(mContext, "Posting", "Updating Password Data");
	}

	protected MyApplication doInBackground(MyApplication... myApps) {
		String fullFile = "";
		try {
			// Create connection
			url = new URL(myApps[0].urlToPost());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Language", "en-US");
		} catch (Exception e) {
			errorMsg = e.toString();
			cancel(true);
		}
		if (!isCancelled()) {
			try {
				fullFile = myApps[0].generatePostString();
			} catch (UnsupportedEncodingException e) {
				errorMsg = e.toString();
				cancel(true);
			}
		}
		if (!isCancelled()) {
			try {
				connection.setRequestProperty("Content-Length",
						"" + Integer.toString(fullFile.getBytes().length));
				wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(fullFile);
				wr.flush();
				wr.close();
				is = new InputStreamReader(connection.getInputStream());
				rd = new BufferedReader(is);
				String line;
				String response = "";
				while ((line = rd.readLine()) != null) {
					response += line;
				}
				rd.close();
				if (!response.equals("File was created.")) {
					errorMsg = response;
					cancel(true);
				}
			} catch (Exception e) {
				errorMsg = e.toString();
				cancel(true);
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
		return myApps[0];
	}

	protected void onCancelled(MyApplication myApp) {
		pd.cancel();
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Data Not Saved");
		builder.setMessage("Sorry, there was an error trying to update the Password Data.\n"
				+ errorMsg);
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		builder.setCancelable(false);
		AlertDialog myAlertDialog = builder.create();
		myAlertDialog.show();
	}

	protected void onPostExecute(MyApplication myApp) {
		pd.cancel();
		if (!isCancelled()) {
			myApp.dataSaved();
		}
	}

}
