package com.fbnm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Failed {
	public static Statement stmt1;
	public static Statement stmt2;
	public static Statement stmt3;
	public static Statement stmt4;
	
	public static void log(int alert_id, String failure_type) {
		stmt1 = null;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			boolean success = false;
			
			stmt1 = DB.conn.createStatement();
			String query = "insert into alert_failed "
					+ "(alert_id, failure_type, trials, last_trial, success) "
					+ "values "
					+ "('"+alert_id+"', '"+failure_type+"', 1, now(), '"+success+"')";
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
	
	public static ResultSet fetchNextSMS() {
		ResultSet rs = null;
		stmt2 = null;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			stmt2 = DB.conn.createStatement();
			String query = "select * from alert_failed where trials < 5 and success = false and failure_type = 'sms'";
			rs = stmt2.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
	
	public static ResultSet fetchNextEmail() {
		ResultSet rs = null;
		stmt3 = null;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			stmt3 = DB.conn.createStatement();
			String query = "select * from alert_failed where trials < 5 and success = false and failure_type = 'email'";
			rs = stmt3.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
	
	public static void update(int id, int trials, boolean success) {
		stmt4 = null;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			stmt4 = DB.conn.createStatement();
			String query = "update alert_failed "
					+ "set trials='"+trials+"', last_trial = now(), success='"+success+"' "
					+ "where id = '"+id+"'";
			stmt4.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stmt4.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
