package com.crimeanalysis.main;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class CrimeAnalysisServlet
 */
@WebServlet("/CrimeAnalysisServlet")
public class CrimeAnalysisServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CrimeAnalysisServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String invokeAction = request.getParameter("invokeAction");
		System.out.println(invokeAction);
		switch(invokeAction)
		{
			case "loginValidation":		String username = request.getParameter("username");
										String password = request.getParameter("password");
										String validationRes1 = DatabaseConnection.loginValidation(username,password);
										response.setContentType("text/html");
										PrintWriter out1 = response.getWriter();
										out1.append(validationRes1);
										out1.close();
										break;
			
			case "domesticViolenceTrends":	int selectedYear1 = Integer.parseInt(request.getParameter("selectedYear"));
											JSONObject dataset1 = SQLQueryTrends.domesticViolenceTrends(selectedYear1); 
											response.setContentType("application/json");
											PrintWriter out2 = response.getWriter();
											out2.print(dataset1);
											out2.close();
											
			case "riskyHours":	int selectedYear2 = Integer.parseInt(request.getParameter("selectedYear"));
								JSONObject dataset2 = SQLQueryTrends.riskyHours(selectedYear2); 
								response.setContentType("application/json");
								PrintWriter out3 = response.getWriter();
								out3.print(dataset2);
								out3.close();
			case "arrestDetails":	JSONObject dataset3 = SQLQueryTrends.arrestDetails(); 
									response.setContentType("application/json");
									PrintWriter out4 = response.getWriter();
									out4.print(dataset3);
									out4.close();
			case "districtsRank":	boolean altSelected = Boolean.parseBoolean(request.getParameter("altSelected"));
									JSONObject dataset4 = SQLQueryTrends.districtsRank(altSelected); 
									response.setContentType("application/json");
									PrintWriter out5 = response.getWriter();
									out5.print(dataset4);
									out5.close();
			case "crimeDistribution":	JSONObject dataset5 = SQLQueryTrends.crimeDistribution(); 
										response.setContentType("application/json");
										PrintWriter out6 = response.getWriter();
										out6.print(dataset5);
										out6.close();
			case "quarterlyAnalysis":		int selectedYear3 = Integer.parseInt(request.getParameter("selectedYear"));
											JSONObject dataset6 = SQLQueryTrends.quarterlyAnalysis(selectedYear3); 
											response.setContentType("application/json");
											PrintWriter out7 = response.getWriter();
											out7.print(dataset6);
											out7.close();
			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String invokeAction = request.getParameter("invokeAction");
		switch(invokeAction)
		{
			case "signupValidation":	String username = request.getParameter("username");
										String password = request.getParameter("password");
										String validationRes = DatabaseConnection.signupValidation(username,password);
										response.setContentType("text/html");
										PrintWriter out = response.getWriter();
										out.append(validationRes);
										out.close();
		}
	}

}
