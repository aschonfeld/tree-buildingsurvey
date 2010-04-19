package com.tbs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tbs.Utils;
import com.tbs.dao.TBSJdbcDao;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        List<String[]> students = new LinkedList<String[]>();
        TBSJdbcDao dao = new TBSJdbcDao();
        try{
        	students = dao.loadStudents();
        	HttpSession session = request.getSession(true);
        	session.setAttribute("Students", students);
        }catch(Exception e){
        	System.out.println("Error retrieving information from database");
        }
        
        Calendar cal = Calendar.getInstance();
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.set(Calendar.MONTH, Calendar.DECEMBER);
        expirationDate.set(Calendar.DAY_OF_MONTH, 12);
        
        String invalidLoginParam = request.getParameter("invalidLogin");
        Boolean invalidLogin = false;
        if(invalidLoginParam != null && invalidLoginParam.length() > 0)
        	invalidLogin = Boolean.valueOf(invalidLoginParam);
        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Login to the TBS Survey</title>");
        out.println("<SCRIPT LANGUAGE=\"JavaScript\" SRC=\"" + request.getContextPath() + "/scripts/common_functions.js\"></SCRIPT>");
    	
        out.println("</head>");
        out.println("<body bgcolor=\"#808000\">");
        out.println("<form action=\"http://localhost:8080" + request.getContextPath() + "/Validate\" method=\"POST\" onsubmit=\"return getAdminValue();\" name=\"form\">");
        out.println("<font size=+3>Login to the diversity of life survey site</font><br>");
        out.println("<br>Administrator?  <input type=\"checkbox\" name=\"AdminCB\" onclick=\"return updateView();\"><br><br>");
        out.println("<div id=\"NameSelection\">Choose your name from this list:<br>");
    	
        if(Utils.surveyExpired())
        	out.println("<table style=\"border-collapse: collapse;padding: 0;margin: 0;\"><tr><td>");
        
        out.println("<select name=\"Name\" size=10 style=\"width:200px;\">");
        for(String[] student : students)
        	out.println("<option value=\"" + student[0] + "\">" + student[0] + "</option>");
        out.println("</select>");
        
        if(cal.after(expirationDate)){
        	out.println("</td><td height=100%>");
        	out.println("<table bgcolor=yellow height=100% style=\"width:200px;\"><tr><td style=\"font-weight:bold;\"><center>");
        	out.println("The submission deadline has passed!</center></td></tr><tr><td><center>");
        	out.println("You can submit a survey but you will not recieve $survey_points points.<br>");
        	out.println("Thanks!</center></td></tr></table>");
        	out.println("</td></tr></table>");
        }
        
        out.println("</div>");
        out.println("<div id=\"StudentPassText\">");
        //out.println("Enter your 8-digit UMS ID # (leave off the UMS):");
        out.println("Enter your password (this is a test version, just enter in 'pass'):");
        out.println("</div>");
        out.println("<div id=\"AdminPassText\" style=\"display:none\">");
        out.println("Enter administrator password:");
        out.println("</div>");
        out.println("<input type=\"password\" name=\"Passwd\" size=20><br>");
        
        if(invalidLogin)
        	out.println("<div id=\"InvalidLogin\" style=\"display:block\">Invalid Password!<br></div>");
        
        out.println("<br>");
        out.println("<input type=\"submit\" value=\"Login\" onclick=\"return checkLogin();\">");
        out.println("<input type=\"hidden\" name=\"AdminValue\" value=\"false\">");
        out.println("<input type=\"hidden\" name=\"GraphingValue\" value=\"false\">");
        out.println("<input type=\"hidden\" name=\"Browser\" value=\"\">");
        out.println("</form>");
        out.println("<hr>");
        
        out.println("</body>");
        out.println("</html>");

        out.close(); 
	}
}
