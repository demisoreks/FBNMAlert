package com.fbnm;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

public class Transaction {
	public static Statement stmt1;
	public static Statement stmt2;
	public static Statement stmt3;
	public static Statement stmt4;
	
	public static void log(String serial, String customer_title, String customer_name, String account_no, double amount, 
			String transaction_type, String narration, String reference, String currency, String branch, double available_balance, 
			String mobile_no, String email, boolean send_sms, boolean send_email, Date transaction_date, Time transaction_time) {
		stmt1 = null;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			stmt1 = DB.conn.createStatement();
			String query = "insert into alert_transactions "
					+ "(serial, customer_title, customer_name, account_no, amount, transaction_type, narration, reference, "
					+ "currency, branch, available_balance, mobile_no, email, send_sms, send_email, date_time_logged, "
					+ "transaction_date, transaction_time) "
					+ "values "
					+ "('"+serial+"', '"+customer_title+"', '"+customer_name+"', '"+account_no+"', '"+amount+"', "
					+ "'"+transaction_type+"', '"+narration+"', '"+reference+"', '"+currency+"', '"+branch+"', "
					+ "'"+available_balance+"', '"+mobile_no+"', '"+email+"', '"+send_sms+"', '"+send_email+"', "
					+ "now(), '"+transaction_date+"', '"+transaction_time+"')";
			stmt1.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stmt1.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static ResultSet fetchNext() {
		ResultSet rs = null;
		stmt2 = null;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			stmt2 = DB.conn.createStatement();
			String query = "select * from alert_transactions where id > '"+Config.getField("last_treated")+"'";
			rs = stmt2.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
	
	public static void markAsTreated(int id) {
		stmt3 = null;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			stmt3 = DB.conn.createStatement();
			String query = "update alert_transactions "
					+ "set date_time_treated = now() "
					+ "where id = '"+id+"'";
			stmt3.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stmt3.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static ResultSet fetchSingle(int id) {
		ResultSet rs = null;
		stmt4 = null;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			stmt4 = DB.conn.createStatement();
			String query = "select * from alert_transactions where id = '"+id+"'";
			rs = stmt4.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
}
