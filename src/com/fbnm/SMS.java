package com.fbnm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class SMS {
	public static boolean send(String message, String recipient) {
		try {
			URL url = new URL("http://172.28.34.20:8080/FBNMSMS/api/service/send?api_key=0dd0b9d8a8efbf66d12deea0655973d5&username=transaction_alert&recipient="+recipient+"&message="+URLEncoder.encode(message, "UTF-8"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			
			if (conn.getResponseCode() == 200) {
				InputStream is;
				String output = "";
				
				try {
					is = conn.getInputStream();
				} catch (Exception ex) {
					is = conn.getErrorStream();
				}
				
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				String line;
				while ((line = br.readLine()) != null) {
					output += line;
				}
				
				try {
					JSONObject response = new JSONObject(output);
					if (response.get("code").equals("00")) {
						return true;
					} else {
						return false;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return false;
	}
	
	public static String formatNumber(String phone_no) {
		if (phone_no == null) {
			return "";
		} else {
			if (phone_no.substring(0, 1).equals("+")) {
				return phone_no.substring(1);
			} else {
				if (phone_no.substring(0, 1).equals("0")) {
					return "234"+phone_no.substring(1);
				} else {
					return phone_no;
				}
			}
		}
	}
	
	public static boolean checkNumber(String phone_no) {
		if (phone_no == null) {
			return false;
		} else {
			if ((phone_no.trim().substring(0, 1).equals("0") && phone_no.trim().length() == 11) || 
				(phone_no.trim().substring(0, 3).equals("234") && phone_no.trim().length() == 13) ||
				(phone_no.trim().substring(0, 4).equals("+234") && phone_no.trim().length() == 14))
				return true;
			else
				return false;
		}
	}
}
