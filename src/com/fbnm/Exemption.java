package com.fbnm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Exemption {
	public static boolean check(String account_no, String alert_type) {
		Statement stmt = null;
		ResultSet rs = null;
		
		boolean result = false;
		
		try {
			if (DB.conn == null || DB.conn.isClosed()) {
				DB.connect();
			}
			
			stmt = DB.conn.createStatement();
			String query = "select * from alert_exemptions where account_no = '"+account_no+"' and alert_type = '"+alert_type+"'";
			rs = stmt.executeQuery(query);
			
			if (rs.next())
				result = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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
}
