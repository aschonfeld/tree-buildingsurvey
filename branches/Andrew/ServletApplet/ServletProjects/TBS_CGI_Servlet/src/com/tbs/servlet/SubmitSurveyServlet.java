package com.tbs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tbs.Utils;
import com.tbs.dao.TBSJdbcDao;

/**
 * Servlet implementation class SubmitSurveyServlet
 */
public class SubmitSurveyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitSurveyServlet() {
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
		HttpSession session = request.getSession(true);
		if(!Utils.sessionExists(request, response, session))
			return;
		
		String student = (String) session.getAttribute("Name");
		
		String tree = request.getParameter("treeXML");
		String Q1 = request.getParameter("Q1");
		String Q2 = request.getParameter("Q2");
        
		TBSJdbcDao dao = new TBSJdbcDao();
		try{
        	List<String[]> data = dao.loadStudentSurvey(student);
        	int returnVal;
        	if(data.size() == 0)
        		returnVal = dao.insertStudentSurvey(student, tree, Q1, Q2, "");
        	else
        		returnVal = dao.updateStudentSurvey(student, tree, Q1, Q2, "");
        	if(returnVal == 1)
        		System.out.println("Survey data has been updated");
        }catch(Exception e){
        	System.out.println("Error retrieving information from database");
        }
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>TBS Student Survey</title>");
        out.println("</head>");
        
        out.println("<body bgcolor=\"lightblue\">");
  	    out.println("<br><center><font size=+1>" + student + " thank you for your survey submission.</font><br>");
  	    if (Utils.surveyExpired())
  	    	out.println("<br><center><font size=+1>You have recieved $survey_points points.</font><br>");
  	    out.println("<form action=\"" + request.getContextPath() + "/StudentApplet\" method=\"POST\" name=\"form\">");
  	    out.println("<table><tr><td>");
  	    out.println("<input type=\"button\" value=\"Return To Login\" onclick=\"window.location = '" + request.getContextPath() + "/Logout';\">");
  	    out.println("</td><td>");
  	    out.println("<input type=\"submit\" value=\"Return To Survey\">");
  	    out.println("</td></tr></table>");
   	    out.println("</form></center>");
   		out.println("</body></html>");
	}

}
