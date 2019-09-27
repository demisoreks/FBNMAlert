package com.fbnm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
	public static Connection conn;
	private static String server = "localhost:5432";
	private static String db = "alert";
	private static String user = "postgres";
	private static String pass = "fbnm@2019";
	
	public static void connect() {
		try {
			Class.forName("org.postgresql.Driver");
			
			String db_conn_string = "jdbc:postgresql://"+server+"/"+db;
			conn = DriverManager.getConnection(db_conn_string, user, pass);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
