package com.fbnm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Config {
	public static String getField(String column_name) {
		String result = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			stmt = DB.conn.createStatement();
			String query = "select * from alert_config where id = 1";
			rs = stmt.executeQuery(query);
			
			if (rs.next()) {
				result = rs.getString(column_name);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static void setField(String column_name, String value) {
		Statement stmt = null;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			stmt = DB.conn.createStatement();
			String query = "update alert_config set "+column_name+"='"+value+"' where id = 1";
			stmt.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
