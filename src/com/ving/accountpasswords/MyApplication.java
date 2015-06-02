package com.ving.accountpasswords;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.crypto.Cipher;

import com.ving.accountpasswords.PasswordListAdapter;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MyApplication extends Application {
	/**
	 * String to hold name of the encryption algorithm.
	 */
	public static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

	/**
	 * String to hold the name of the private key file.
	 */
	public static String PRIVATE_KEY_FILE;

	/**
	 * String to hold name of the public key file.
	 */
	public static String PUBLIC_KEY_FILE;
	
	/**
	 * Public Key
	 */
	public static PublicKey publicKey = null;
	
	/**
	 * Private Key
	 */
	private static PrivateKey privateKey = null;
	
	/**
	 * String to hold name of encrypted password file.
	 */
	public static String fileName;
	
	/**
	 * URL of the encrypted password file, if it isn't available locally
	 */
	public static final String fileURLVing = "fill in URL";
	public static final String fileURLEricsson = "fill in URL";
	public static final String fileURLLuci = "fill in URL";
	public static String urlToUse = "";
	public static final String postURLVing = "fill in URL";
	public static final String postURLEricsson = "fill in URL";
	public static final String postURLLuci = "fill in URL";
	public static String urlToPost = "";
	
	/**
	 * Vector to hold all of the password information
	 */
	public static ArrayList<PasswordData> passwordData = null;
	
	/**
	 * Flag to tell if we are using a local file or the web.
	 */
	public static Boolean useFile;
	
	/**
	 * Flag to tell if data has been changed or not.
	 */
	private Boolean dataChanged = null;
	private ArrayAdapter<PasswordData> expandableAdapter = null;
	private Context mContext = null;
	private Button saveBtn = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		dataChanged = false;
		passwordData = new ArrayList<PasswordData>();
	}
	
	public void refresh() {
		dataChanged = false;
		saveBtn.setBackgroundColor(Color.TRANSPARENT);
		passwordData = new ArrayList<PasswordData>();
	}
	
	public void setUrlVing() {
		urlToUse = fileURLVing;
	}
	
	public void setUrlLuci() {
		urlToUse = fileURLLuci;
	}
	
	public void setUrlEricsson() {
		urlToUse = fileURLEricsson;
	}
	
	public void setPostUrlVing() {
		urlToPost = postURLVing;
	}
	
	public void setPostUrlLuci() {
		urlToPost = postURLLuci;
	}
	
	public void setPostUrlEricsson() {
		urlToPost = postURLEricsson;
	}
	
	public String urlToUse() {
		return urlToUse;
	}
	
	public String urlToPost() {
		return urlToPost;
	}
	
	public void setKeys(PublicKey pblkey, PrivateKey prvkey) {
		publicKey = pblkey;
		privateKey = prvkey;
	}
	
	public void decodeAndSave(String line) {
		String decodeText;
		decodeText = decrypt(line);
		passwordData.add(new PasswordData(decodeText));
	}
	
	public void save(String account, String userid, String password) {
		passwordData.add(new PasswordData(account,userid,password));
	}
	
	public ArrayList<PasswordData> getExpArray() {
		return passwordData;
	}
	
	public void saveStuff(Context newContext, Button btn) {
		mContext = newContext;
		saveBtn = btn;
	}
	
	public void setExpandAdapter(ArrayAdapter<PasswordData> newAdapter) {
		expandableAdapter = newAdapter;
	}
	
	public void dataUpdated() {
		dataChanged = true;
		saveBtn.setBackgroundColor(Color.MAGENTA);
	}
	
	public void dataSaved() {
		dataChanged = false;
		saveBtn.setBackgroundColor(Color.TRANSPARENT);
	}
	
	public Boolean hasDataChanged() {
		return dataChanged;
	}
	
	public void notifyDataSetChanged() {
		Collections.sort(passwordData);
		expandableAdapter.notifyDataSetChanged();
	}
	
	public String generatePostString() throws UnsupportedEncodingException {
		String decodeText;
    	String encodedText;
    	String fullFile = "INPFILE=";
    	for (PasswordData pd : passwordData) {
    		decodeText = pd.getAccount() + "|" + pd.getUserId() + "|" + pd.getPassword();
    		encodedText = encrypt(decodeText);
    		fullFile += URLEncoder.encode(encodedText + "\n", "UTF-8");
    	}
    	return fullFile;
	}
	
	/**
     * Encrypt the plain text using public key.
	 * 
	 * @param text
	 *          : original plain text
	 * @return Encrypted text
	 * @throws java.lang.Exception
	 */
	public static String encrypt(String text) {
		byte[] cipherText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			cipherText = cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Arrays.toString(cipherText);
	}
	
	/**
	 * Decrypt text using private key.
	 * 
	 * @param text
	 *          :encrypted text
	 * @return plain text
	 * @throws java.lang.Exception
	 */
	public String decrypt(String text) {
		byte[] decryptedText = null;
		String rtn = "";
		String[] byteValues = text.substring(1, text.length() - 1).split(",");
		byte[] bytes = new byte[byteValues.length];
		for (int i=0, len=bytes.length; i<len; i++) {
		   bytes[i] = Byte.valueOf(byteValues[i].trim());     
		}
		try {
			// get an RSA cipher object and print the provider
			Cipher cipher = Cipher.getInstance(ALGORITHM);
		
			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			decryptedText = cipher.doFinal(bytes);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (decryptedText != null) {
			try {
				rtn = new String(decryptedText, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		return rtn;
	}
	
}
