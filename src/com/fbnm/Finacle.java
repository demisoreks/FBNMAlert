package com.fbnm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimeZone;

public class Finacle {
	public static Connection conn;
	public static Statement stmt;
	
	public static void connect() {
		try {
			TimeZone timezone = TimeZone.getTimeZone("Africa/Lagos");
			TimeZone.setDefault(timezone);
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String db_conn_string = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST="+Config.getField("cba_server")+")(PORT="+Config.getField("cba_port")+")))(CONNECT_DATA=(SERVICE_NAME="+Config.getField("cba_service_name")+")))";
			conn = DriverManager.getConnection(db_conn_string, Config.getField("cba_user"), Config.getField("cba_pass"));
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ResultSet getNextBatch() {
		ResultSet rs = null;
		stmt = null;
		
		try {
			if (conn == null || conn.isClosed()) {
				connect();
			}
			
			stmt = conn.createStatement();
			String query = "SELECT TO_CHAR(D.PSTD_DATE, 'YYYYMMDDHH24MISS') MARKER, " + 
					"TO_CHAR(D.TRAN_DATE, 'YYYYMMDD')||D.TRAN_ID||LPAD(D.PART_TRAN_SRL_NUM, 5, '0') TRAN_SERIAL, " + 
					"DECODE(D.PART_TRAN_TYPE,'D','Debit','C','Credit') TRAN_TYPE, " + 
					"D.TRAN_AMT, D.PSTD_DATE, D.TRAN_PARTICULAR, C.CUST_TITLE_CODE, C.CUST_NAME, " + 
					"G.FORACID, C.CUST_COMU_PHONE_NUM_1, C.EMAIL_ID, D.TRAN_ID||'-'||D.PART_TRAN_SRL_NUM REFERENCE, " + 
					"D.CRNCY_CODE, S.SOL_DESC, G.CLR_BAL_AMT " + 
					"FROM DTD D, CMG C, GAM G, SOL S " + 
					"WHERE C.CUST_ID = G.CUST_ID " + 
					"AND G.ACID = D.ACID " + 
					"AND G.SOL_ID = S.SOL_ID " + 
					"AND D.PSTD_FLG = 'Y' " +
					"AND ((SELECT ACCT_STATUS FROM SMT WHERE ACID = D.ACID) IN ('A','I') " + 
					"OR (SELECT ACCT_STATUS FROM CAM WHERE ACID = D.ACID) IN ('A','I')) " +
					"AND TO_CHAR(D.PSTD_DATE, 'YYYYMMDDHH24MISS') >= '"+Config.getField("last_marker")+"' " +
					"ORDER BY MARKER";
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
}
