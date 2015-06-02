package com.ving.accountpasswords;

import java.util.regex.PatternSyntaxException;

public class PasswordData implements Comparable<PasswordData> {
	private String account;
	private String userId;
	private String password;
	
	public PasswordData (String newAccount, String newUserId, String newPassword) {
		account = newAccount;
		userId = newUserId;
		password = newPassword;
	}
	
	public PasswordData (String input) {
		try {
			String[] fields = input.split("\\|");
			account = fields[0];
			if (fields.length < 2) {
				userId = "";
			} else {
				userId = fields[1];
			}
			if (fields.length < 3) {
				password = "";
			} else {
				password = fields[2];
			}
		} catch (PatternSyntaxException ex) {
			account = "";
			userId = "";
			password = "";
		}
	}
	
	public int compareTo (PasswordData pwd) {
		int rtn = this.account.toLowerCase().compareTo(pwd.account.toLowerCase());
		return rtn;
	}
	
	public String getAccount() {
		return account;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void update(String newAccount, String newUserId, String newPassword) {
		account = newAccount;
		userId = newUserId;
		password = newPassword;
	}

}
