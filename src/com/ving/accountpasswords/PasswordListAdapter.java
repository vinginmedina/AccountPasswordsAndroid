package com.ving.accountpasswords;

import java.util.ArrayList;

import com.ving.accountpasswords.MyApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PasswordListAdapter extends ArrayAdapter<PasswordData> {
	
	private Context mContext;
    private ArrayList<PasswordData> mPasswordData;

    public PasswordListAdapter(Context context, int textViewResourceId, ArrayList<PasswordData> passwordData) {
    	super(context, textViewResourceId, passwordData);
    	mContext = context;
        mPasswordData = passwordData;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = LayoutInflater.from(mContext);
			v = vi.inflate(android.R.layout.simple_list_item_2, null);
		}
		if (mPasswordData.get(position) != null) {
			TextView text1 = (TextView) v.findViewById(android.R.id.text1);
//			TextView text2 = (TextView) v.findViewById(android.R.id.text2);
			text1.setText(mPasswordData.get(position).getAccount());
		}
		return v;
	}

}
