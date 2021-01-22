package com.crimeanalysis.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class SQLQueryTrends {

	public static JSONObject domesticViolenceTrends(int selectedYear)
	{
		System.out.println("came");
		JSONObject finalObject = new JSONObject();
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:oracle.cise.ufl.edu:1521:orcl","","");
			Statement stmt = conn.createStatement();
			String sqlQuery =  	"WITH crimestatistics AS (SELECT c.year year,CASE substr(to_char(c.timestamp), 4, 3) " +
								"WHEN 'JAN' THEN 'January' WHEN 'FEB' THEN 'February' WHEN 'MAR' THEN 'March' WHEN 'APR' THEN 'April' " +
								"WHEN 'MAY' THEN 'May' WHEN 'JUN' THEN 'June' WHEN 'JUL' THEN 'July' WHEN 'AUG' THEN 'August' " +
								"WHEN 'SEP' THEN 'September' WHEN 'OCT' THEN 'October' WHEN 'NOV' THEN 'November' WHEN 'DEC' THEN 'December' " +
								"END AS month, 'Domestic' type, COUNT(1) total FROM cct_cases c WHERE c.domestic = 'true' AND c.year = " + selectedYear + " " +
								"GROUP BY c.year, substr(to_char(c.timestamp), 4, 3) UNION ALL " +
								"SELECT c.year year, CASE substr(to_char(c.timestamp), 4, 3) " +
								"WHEN 'JAN' THEN 'January' WHEN 'FEB' THEN 'February' WHEN 'MAR' THEN 'March' WHEN 'APR' THEN 'April' " +
								"WHEN 'MAY' THEN 'May' WHEN 'JUN' THEN 'June' WHEN 'JUL' THEN 'July' WHEN 'AUG' THEN 'August' " +
								"WHEN 'SEP' THEN 'September' WHEN 'OCT' THEN 'October' WHEN 'NOV' THEN 'November' WHEN 'DEC' THEN 'December' " +
								"END AS month, 'Non-Domestic' type, COUNT(1) total FROM cct_cases c WHERE c.domestic = 'false' AND c.year = " + selectedYear + " " +
								"GROUP BY c.year, substr(to_char(c.timestamp), 4, 3)) " +
								"SELECT * FROM crimestatistics PIVOT (SUM ( total ) FOR type IN ( 'Domestic' AS domestic, 'Non-Domestic' AS nondomestic )) " +
								"ORDER BY decode(month, 'January', 1, 'February', 2,'March', 3, 'April', 4, 'May',5, 'June', 6, 'July', 7,'August', 8, 'September', 9, 'October',10, 'November', 11, 'December', 12)";
			ResultSet rs1 = stmt.executeQuery(sqlQuery);
			String[] labels = new String[12];
			int[] domestic = new int[12];
			int[] nondomestic = new int[12];
			int i=0;
			while(rs1.next())
			{
				labels[i] = rs1.getString("MONTH");
				domestic[i] = rs1.getInt("DOMESTIC");
				nondomestic[i] = rs1.getInt("NONDOMESTIC");
				i++;
			}
			finalObject.put("labels", labels);
			finalObject.put("Domestic", domestic);
			finalObject.put("Non-Domestic", nondomestic);
			System.out.println(i);
			conn.close();
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		return finalObject;
	}

	public static JSONObject riskyHours(int selectedYear)
	{
		JSONObject finalObject = new JSONObject();
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:oracle.cise.ufl.edu:1521:orcl","","");
			Statement stmt = conn.createStatement();
			String sqlQuery = 	"SELECT CASE tz WHEN 1  THEN '00 a.m. to 06 a.m.' WHEN 2  THEN '06 a.m. to 12 p.m.' WHEN 3  THEN '12 p.m. to 06 p.m.' ELSE '06 p.m. to 00 a.m.' " +
								"END AS times, std_dev, mean FROM (SELECT DISTINCT tz, round(STDDEV(total) OVER(ORDER BY tz)) std_dev, round(AVG(total) OVER(ORDER BY tz))mean " +
								"FROM (SELECT CASE WHEN EXTRACT(HOUR FROM c.timestamp) >= 0 AND EXTRACT(HOUR FROM c.timestamp) < 6 THEN 1 " +
								"WHEN EXTRACT(HOUR FROM c.timestamp) >= 6 AND EXTRACT(HOUR FROM c.timestamp) < 12 THEN 2 " +
								"WHEN EXTRACT(HOUR FROM c.timestamp) >= 12 AND EXTRACT(HOUR FROM c.timestamp) < 18 THEN 3 ELSE 4 END AS tz, " +
								"COUNT(*) total FROM cct_cases c WHERE c.domestic = 'false' AND c.district IS NOT NULL AND year =" + selectedYear + " GROUP BY EXTRACT(HOUR FROM c.timestamp)))";
			ResultSet rs1 = stmt.executeQuery(sqlQuery);
			List<String> times = new ArrayList();
			List<Integer> stddev = new ArrayList();
			List<Integer> mean = new ArrayList();
			while(rs1.next())
			{
				times.add(rs1.getString("TIMES"));
				stddev.add(rs1.getInt("STD_DEV"));
				mean.add(rs1.getInt("MEAN"));
			}
			finalObject.put("times", times);
			finalObject.put("stddev", stddev);
			finalObject.put("mean", mean);
			conn.close();
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		return finalObject;
	}

	public static JSONObject arrestDetails()
	{
		JSONObject finalObject = new JSONObject();
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:oracle.cise.ufl.edu:1521:orcl","","");
			Statement stmt = conn.createStatement();
			String sqlQuery = 	"SELECT arrest.year, total.number_of_cases, arrest.number_of_arrests FROM " +
								"(SELECT year, COUNT(case_no) AS number_of_arrests FROM cct_cases WHERE arrest = 'true' GROUP BY year) arrest, " +
								"(SELECT year, COUNT(case_no) AS number_of_cases FROM cct_cases GROUP BY year) total WHERE arrest.year = total.year ORDER BY 1";
			System.out.println(sqlQuery);
			ResultSet rs1 = stmt.executeQuery(sqlQuery);
			List<Integer> year = new ArrayList();
			List<Integer> noofcases = new ArrayList();
			List<Integer> noofarrests = new ArrayList();
			while(rs1.next())
			{
				year.add(rs1.getInt("YEAR"));
				noofcases.add(rs1.getInt("NUMBER_OF_CASES"));
				noofarrests.add(rs1.getInt("NUMBER_OF_ARRESTS"));
			}
			finalObject.put("year", year);
			finalObject.put("noofcases", noofcases);
			finalObject.put("noofarrests", noofarrests);
			conn.close();
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		return finalObject;
	}

	public static JSONObject districtsRank(boolean alt)
	{
		JSONObject finalObject = new JSONObject();
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:oracle.cise.ufl.edu:1521:orcl","","");
			Statement stmt = conn.createStatement();
			String sqlQuery1 = 	"WITH totrangecases AS (SELECT COUNT(case_no) total_case FROM cct_cases WHERE district IS NOT NULL AND trunc(timestamp) >= '01-JAN-2012' " +
								"AND trunc(timestamp) <= '31-DEC-2013' GROUP BY district HAVING COUNT(case_no) >= ALL (" +
								"SELECT COUNT(case_no) FROM cct_cases WHERE district IS NOT NULL AND trunc(timestamp) >= '01-JAN-2012' AND trunc(timestamp) <= '31-DEC-2013' " +
								"GROUP BY district)) SELECT district, total_cases, relative_grade, RANK() OVER(ORDER BY relative_grade DESC) rank FROM " +
								"(SELECT district, COUNT(case_no) total_cases, ceil(COUNT(case_no) /(SELECT total_case FROM totrangecases) * 100) AS relative_grade " +
								"FROM cct_cases WHERE district IS NOT NULL AND trunc(timestamp) >= '01-JAN-2012' AND trunc(timestamp) <= '31-DEC-2013' GROUP BY district " +
								"ORDER BY district)";
			String sqlQuery2 =  "WITH casecount AS (SELECT district, COUNT(case_no) AS \"case_count\" FROM cct_cases WHERE district IS NOT NULL " +
								"AND trunc(timestamp) >= TO_DATE('2014/07/09', 'yyyy-mm-dd') AND trunc(timestamp) <= TO_DATE('2018/07/09', 'yyyy-mm-dd') " +
								"GROUP BY district) SELECT district,\"case_count\" as TOTAL_CASES, RANK() OVER(ORDER BY \"case_count\") AS \"RANK\" FROM casecount";
			System.out.println(sqlQuery2);
			if(alt)
			{
				ResultSet rs1 = stmt.executeQuery(sqlQuery2);
				List<Integer> district = new ArrayList();
				List<Integer> rank = new ArrayList();
				List<Integer> noofcases = new ArrayList();
				while(rs1.next())
				{
					district.add(rs1.getInt("DISTRICT"));
					rank.add(rs1.getInt("RANK"));
					noofcases.add(rs1.getInt("TOTAL_CASES"));
				}
				finalObject.put("district", district);
				finalObject.put("rank", rank);
				finalObject.put("noofcases", noofcases);
			}
			else
			{
				ResultSet rs2 = stmt.executeQuery(sqlQuery1);
				List<Integer> district = new ArrayList();
				List<Integer> rank = new ArrayList();
				List<Integer> noofcases = new ArrayList();
				while(rs2.next())
				{
					district.add(rs2.getInt("DISTRICT"));
					rank.add(rs2.getInt("RANK"));
					noofcases.add(rs2.getInt("TOTAL_CASES"));
				}
				finalObject.put("district", district);
				finalObject.put("rank", rank);
				finalObject.put("noofcases", noofcases);
			}

			conn.close();
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		return finalObject;
	}

	public static JSONObject crimeDistribution()
	{
		JSONObject finalObject = new JSONObject();
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:oracle.cise.ufl.edu:1521:orcl","","");
			Statement stmt = conn.createStatement();
			String sqlQuery = 	"SELECT I.Description,count(c.case_ID) Cases FROM CCT_CASES C INNER JOIN CCT_FBI_Code I on C.FBI_Code = I.Code " +
								"GROUP BY Description ORDER BY COUNT(c.case_ID) DESC";
			System.out.println(sqlQuery);
			ResultSet rs1 = stmt.executeQuery(sqlQuery);
			List<String> description = new ArrayList();
			List<Integer> cases = new ArrayList();
			while(rs1.next())
			{
				description.add(rs1.getString("DESCRIPTION"));
				cases.add(rs1.getInt("CASES"));
			}
			finalObject.put("description", description);
			finalObject.put("cases", cases);
			conn.close();
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		return finalObject;
	}

	public static JSONObject quarterlyAnalysis(int selectedYear)
	{
		JSONObject finalObject = new JSONObject();
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:oracle.cise.ufl.edu:1521:orcl","","");
			Statement stmt = conn.createStatement();
			String sqlQuery = 	"WITH crimebytimequarter AS (SELECT year, CASE month WHEN 'JAN' THEN 'Q1' WHEN 'FEB' THEN 'Q1' WHEN 'MAR' THEN 'Q1' " +
								"WHEN 'APR' THEN 'Q2' WHEN 'MAY' THEN 'Q2' WHEN 'JUN' THEN 'Q2' WHEN 'JUL' THEN 'Q3' WHEN 'AUG' THEN 'Q3' WHEN 'SEP' THEN 'Q3' " +
								"WHEN 'OCT' THEN 'Q4' WHEN 'NOV' THEN 'Q4' WHEN 'DEC' THEN 'Q4' END AS quarter, " +
								"CASE WHEN period = 'AM' AND hour >= 0 AND hour < 6 THEN 1 WHEN period = 'AM' AND hour >= 6 THEN 2 WHEN period = 'PM' AND hour >= 0 " +
								"AND hour < 6 THEN 3 WHEN period = 'PM' AND hour >= 6 THEN 4 ELSE 0 END AS time_sector, district " +
								"FROM(SELECT year, substr(to_char(c.timestamp), 4, 3) month, mod(to_number(substr(to_char(c.timestamp), 11, 2)), 12) hour, " +
								"TRIM(substr(to_char(c.timestamp), 26, 3)) period, c.district FROM cct_cases c WHERE c.domestic = 'false' AND year = 2017)) " +
								"SELECT month_range||' '||time_range as Str, total FROM (SELECT year, CASE quarter WHEN 'Q1' THEN 'Q1 : January to April' " +
								"WHEN 'Q2' THEN 'Q2 : May to July' WHEN 'Q3' THEN 'Q3 : August to September' ELSE 'Q4: October to December' END AS month_range, " +
								"CASE time_sector WHEN 1 THEN 'T1: 00 a.m. to 06 a.m.' WHEN 2 THEN 'T2: 06 a.m. to 12 p.m.' WHEN 3 THEN 'T3: 12 p.m. to 06 p.m.' " +
								"ELSE 'T4: 06 p.m. to 00 a.m.' END AS time_range, COUNT(1) total FROM crimebytimequarter GROUP BY year, quarter, time_sector " +
								"ORDER BY 1 ASC, 2 ASC, 3 ASC, 4 DESC)";
			ResultSet rs1 = stmt.executeQuery(sqlQuery);
			List<String> str = new ArrayList();
			List<Integer> total = new ArrayList();
			while(rs1.next())
			{
				str.add(rs1.getString("STR"));
				total.add(rs1.getInt("TOTAL"));
			}
			finalObject.put("str", str);
			finalObject.put("total", total);
			conn.close();
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		return finalObject;
	}
}
