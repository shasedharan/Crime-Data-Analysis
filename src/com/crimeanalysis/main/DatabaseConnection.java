package com.crimeanalysis.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection {

	public static String loginValidation(String username, String password)
	{
		String validationRes = "FAILURE";
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:oracle.cise.ufl.edu:1521:orcl","","");
			Statement stmt = conn.createStatement();
			String sqlQuery1 = "SELECT COUNT(*) COUNT FROM CA_USER WHERE USERNAME = '" + username + "' AND PASSWORD = '" + password + "'";
			ResultSet rs = stmt.executeQuery(sqlQuery1);
			while(rs.next())
			{
				System.out.println(rs.getInt("COUNT"));
				if(rs.getInt("COUNT") > 0)
				{
					System.out.println(rs.getInt("COUNT"));
					validationRes = "SUCCESS";
				}
			}
			conn.close();
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		return validationRes;
	}

	public static String signupValidation(String username, String password)
	{
		System.out.println("came");
		String validationRes = "FAILURE";
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:oracle.cise.ufl.edu:1521:orcl","","");
			Statement stmt = conn.createStatement();
			String sqlQuery1 = "SELECT COUNT(*) COUNT FROM CA_USER WHERE USERNAME = '" + username + "'";
			ResultSet rs1 = stmt.executeQuery(sqlQuery1);
			while(rs1.next())
			{
				System.out.println(rs1.getInt("COUNT"));
				if(rs1.getInt("COUNT") > 0)
				{
					return "FAILURE";
				}
			}
			String sqlQuery2 = "INSERT INTO CA_USER VALUES('" + username + "','" + password + "')";
			stmt.executeQuery(sqlQuery2);
			validationRes = "SUCCESS";
			conn.close();
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		return validationRes;
	}

}
