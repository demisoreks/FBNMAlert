package com.fbnm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Process {
	private static Locale locale = new Locale("en", "NG");
	private static DecimalFormat formatter = new DecimalFormat("#,###.00");
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM d, yyyy", locale);
	private static SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a", locale);
	
	public static void fetcher() {
		while (true) {
			ResultSet rs_f = Finacle.getNextBatch();

			try {
				while (rs_f.next()) {
					boolean send_sms = true;
					boolean send_email = true;
					
					if (rs_f.getString("CUST_COMU_PHONE_NUM_1") != null && !SMS.checkNumber(SMS.formatNumber(rs_f.getString("CUST_COMU_PHONE_NUM_1")))) {
						send_sms = false;
					}
					if (rs_f.getString("CUST_COMU_PHONE_NUM_1") == null || rs_f.getString("CUST_COMU_PHONE_NUM_1").equals("null")) {
						send_sms = false;
					}
					if (Exemption.check(rs_f.getString("FORACID"), "sa")) {
						send_sms = false;
					}
					if (Exemption.check(rs_f.getString("FORACID"), "ea")) {
						send_email = false;
					}
					
					Transaction.log(rs_f.getString("TRAN_SERIAL"), rs_f.getString("CUST_TITLE_CODE"), rs_f.getString("CUST_NAME"), 
							rs_f.getString("FORACID"), rs_f.getDouble("TRAN_AMT"), rs_f.getString("TRAN_TYPE"), rs_f.getString("TRAN_PARTICULAR"), 
							rs_f.getString("REFERENCE"), rs_f.getString("CRNCY_CODE"), rs_f.getString("SOL_DESC"), rs_f.getDouble("CLR_BAL_AMT"), 
							SMS.formatNumber(rs_f.getString("CUST_COMU_PHONE_NUM_1")), rs_f.getString("EMAIL_ID"), send_sms, send_email, 
							rs_f.getDate("PSTD_DATE"), rs_f.getTime("PSTD_DATE"));
					
					System.out.println("Logged transaction "+rs_f.getString("TRAN_SERIAL"));
					Config.setField("last_marker", rs_f.getString("MARKER"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					rs_f.close();
					Finacle.stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void picker() {
		while (true) {
			ResultSet rs = Transaction.fetchNext();
			
			try {
				if (rs.next()) {
					if (rs.getBoolean("send_sms") || rs.getBoolean("send_email")) {
						String transaction_type = rs.getString("transaction_type");
						String currency;
						if (rs.getString("currency") == null || rs.getString("currency").equals("null")) {
							currency = "NGN";
						} else {
							currency = rs.getString("currency");
						}
						String transaction_amount = formatter.format(rs.getDouble("amount"));
						String narration;
						if (rs.getString("narration") == null) {
							narration = "";
						} else {
							if (rs.getString("narration").length() > 40) {
								narration = rs.getString("narration").substring(0, 40);
							} else {
								narration = rs.getString("narration");
							}
						}
						String account_no = rs.getString("account_no");
						account_no = account_no.replace(account_no.substring(2, 8), "******");
						String transaction_date = dateFormatter.format(rs.getDate("transaction_date"));
						String transaction_time = timeFormatter.format(rs.getTime("transaction_time"));
						String available_balance = formatter.format(rs.getDouble("available_balance"));
						String branch = rs.getString("branch");
						String name;
						if (rs.getString("customer_title").equals("OTH.")) {
							name = rs.getString("customer_name");
						} else if (rs.getString("customer_title").equals("COY")) {
							name = "MESSRS "+rs.getString("customer_name");
						} else {
							name = rs.getString("customer_title")+" "+rs.getString("customer_name");
						}
						
						if (rs.getBoolean("send_sms")) {
							String message = transaction_type+" Alert: "+currency+transaction_amount+" "
									+ "("+narration+") "+account_no+" on "+transaction_date+"@"+transaction_time+". "
									+ "Bal: "+currency+available_balance;
							
							//if (SMS.send(message, "2348023571103")) {
							if (SMS.send(message, rs.getString("mobile_no"))) {
								System.out.println("SUCCESS - "+message);
								
								resend("sms");
							} else {
								System.out.println("FAILED - "+message);
								Failed.log(rs.getInt("id"), "sms");
							}
						}
						
						if (rs.getBoolean("send_email") && Mail.isValidEmailAddress(rs.getString("email"))) {
							//if (Mail.sendEmailAlert(transaction_type, name, account_no, currency+" "+transaction_amount, narration, transaction_date+" at "+transaction_time, branch, currency+" "+available_balance, "demiladesoremekun@gmail.com")) {
							if (Mail.sendEmailAlert(transaction_type, name, account_no, currency+" "+transaction_amount, narration, transaction_date+" at "+transaction_time, branch, currency+" "+available_balance, rs.getString("email"))) {
								System.out.println("SUCCESS - Email sent");
								
								resend("email");
							} else {
								System.out.println("FAILED - Unknown error");
								Failed.log(rs.getInt("id"), "email");
							}
						}
					}
					
					Transaction.markAsTreated(rs.getInt("id"));
					Config.setField("last_treated", Integer.toString(rs.getInt("id")));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					Transaction.stmt2.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void resend(String type) {
		ResultSet rs1 = null;
		
		if (type.equals("sms")) {
			rs1 = Failed.fetchNextSMS();
		} else if (type.equals("email")) {
			rs1 = Failed.fetchNextEmail();
		}
		
		try {
			if (rs1.next()) {
				ResultSet rs = Transaction.fetchSingle(rs1.getInt("alert_id"));
				
				String transaction_type = rs.getString("transaction_type");
				String currency;
				if (rs.getString("currency") == null || rs.getString("currency").equals("null")) {
					currency = "NGN";
				} else {
					currency = rs.getString("currency");
				}
				String transaction_amount = formatter.format(rs.getDouble("amount"));
				String narration;
				if (rs.getString("narration") == null) {
					narration = "";
				} else {
					if (rs.getString("narration").length() > 40) {
						narration = rs.getString("narration").substring(0, 40);
					} else {
						narration = rs.getString("narration");
					}
				}
				String account_no = rs.getString("account_no");
				account_no = account_no.replace(account_no.substring(2, 8), "******");
				String transaction_date = dateFormatter.format(rs.getDate("transaction_date"));
				String transaction_time = timeFormatter.format(rs.getTime("transaction_time"));
				String available_balance = formatter.format(rs.getDouble("available_balance"));
				String branch = rs.getString("branch");
				String name;
				if (rs.getString("customer_title").equals("OTH.")) {
					name = rs.getString("customer_name");
				} else if (rs.getString("customer_title").equals("COY")) {
					name = "MESSRS "+rs.getString("customer_name");
				} else {
					name = rs.getString("customer_title")+" "+rs.getString("customer_name");
				}
				
				if (rs1.getString("failure_type").equals("sms")) {
					String message = transaction_type+" Alert: "+currency+transaction_amount+" "
							+ "("+narration+") "+account_no+" on "+transaction_date+"@"+transaction_time+". "
							+ "Bal: "+currency+available_balance;
					
					if (SMS.send(message, rs.getString("mobile_no"))) {
						System.out.println("SUCCESS FINALLY - "+message);
						Failed.update(rs1.getInt("id"), rs1.getInt("trials"), true);
					} else {
						System.out.println("FAILED AGAIN - "+message);
						Failed.update(rs1.getInt("id"), rs1.getInt("trials")+1, false);
					}
				}
				
				if (rs1.getString("failure_type").equals("email")) {
					if (Mail.sendEmailAlert(transaction_type, name, account_no, currency+" "+transaction_amount, narration, transaction_date+" at "+transaction_time, branch, currency+" "+available_balance, rs.getString("email"))) {
						System.out.println("SUCCESS FINALLY - Email sent");
						Failed.update(rs1.getInt("id"), rs1.getInt("trials"), true);
					} else {
						System.out.println("FAILED AGAIN - Unknown error");
						Failed.update(rs1.getInt("id"), rs1.getInt("trials")+1, false);
					}
				}
				
				rs.close();
				Transaction.stmt4.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs1.close();
				if (Failed.stmt2 != null) Failed.stmt2.close();
				if (Failed.stmt3 != null) Failed.stmt3.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				fetcher();
			}
		});
		
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				picker();
			}
		});
		
		DB.connect();
		Finacle.connect();
		t1.start();
		t2.start();
	}
}
