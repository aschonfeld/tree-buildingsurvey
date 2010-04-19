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
 * Servlet implementation class StudentAppletServlet
 */
public class StudentAppletServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StudentAppletServlet() {
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
		String browser = (String) session.getAttribute("Browser");
		String section = (String) session.getAttribute("Section");
		
		TBSJdbcDao dao = new TBSJdbcDao();
		
		
		String date="", tree="", Q1="", Q2="";
		try{
        	List<String[]> data = dao.loadStudentSurvey(student);
        	if(data.size() > 0){
        		date = data.get(0)[0];
        		tree = data.get(0)[1];
        		Q1 = data.get(0)[2];
        		Q2 = data.get(0)[3];
        	}        	
        }catch(Exception e){
        	System.out.println("Error retrieving information from database");
        }
        
        boolean surveyComplete = !Utils.isStringEmpty(tree) 
        	&& !Utils.isStringEmpty(Q1) && !Utils.isStringEmpty(Q2);
        
        StringBuffer appletParam = new StringBuffer(student).append("+=");
        appletParam.append(date).append("+=");
        appletParam.append(tree).append("+=");
        appletParam.append(Q1).append("+=");
        appletParam.append(Q2).append("+=");
        appletParam.append("+=");
        appletParam.append(section).append("+=");
        
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>TBS Student Survey</title>");
        out.println("<SCRIPT LANGUAGE=\"JavaScript\" SRC=\"" + request.getContextPath() + "/scripts/common_functions.js\"></SCRIPT>");
        out.println("</head>");
        
        out.println("<body bgcolor=\"lightblue\" style=\"border: 0;padding: 0;margin:0;\">"); 
    	out.println("<form action=\"" + request.getContextPath() + "/SubmitSurvey\" method=\"POST\" name=\"form\" style=\"border: 0;padding: 0;margin:0;\">");
      	out.println("<table width=\"100%\" height=\"100%\" style=\"border-collapse: collapse;padding: 0;margin: 0;\"> ");
      	out.println("<tr>");
      	out.println("<td width=\"85%\">");
    	out.println("<applet code=\"tbs.TBSApplet.class\" archive=\"" + request.getContextPath() + "/TBSRun.jar\" width=\"100%\" height=\"100%\" name=\"TreeApplet\"> ");
    	out.println("<param name=\"Student\" value=\""+ appletParam.toString() + "\"> ");
      	out.println("<param name=\"Admin\" value=\"false\"> ");
      	out.println("<param name=\"Browser\" value=\"" + browser + "\"> ");
      	out.println("You have to enable Java on your machine! ");
      	out.println("</applet>");
      	out.println("</td>");
      	out.println("<td width=\"15%\" height=\"100%\" align=\"center\">");
      	out.println("<table style=\"border-collapse: collapse;padding: 0;margin: 0;height:100%;\">");
      	out.println("<tr><td valign=\"top\"><center>");
        out.println("<input type=\"button\" value=\"Logout\" onclick=\"window.location = '" + request.getContextPath() + "/Logout';\">");
        out.println("</center></td></tr>");
        out.println("<tr><td><center>");
        out.println("<font size=+1>Diversity of Life<br> Survey<br> for<br> " + student + "</font><br>");
        
        if(Utils.surveyExpired()){
        	out.println("<table bgcolor=yellow><tr><td style=\"font-weight:bold;\"><center>");
    		out.println("The submission deadline<br> has passed!</center></td></tr><tr><td><center>");
    		out.println("You can submit a survey<br>but you will not<br> recieve " + Utils.surveyPoints + " points.<br>");
    		out.println("Thanks!</center></td></tr></table>");
        }else{
        	if(!surveyComplete){
        		out.println("<table bgcolor=red><tr><td style=\"font-weight:bold;\"><center>");
   		     out.println("Your survey is not complete!</center></td></tr><tr><td><center>");
   		     out.println("You will not receive<br> any credit unless<br> you answer all the questions.<br>");
   		     out.println("Thanks!</center></td></tr></table>");
        	}else{
        		out.println("<table bgcolor=green><tr><td style=\"font-weight:bold;\"><center>");
   		     out.println("Your survey is complete!</center></td></tr><tr><td><center>");
   		     out.println("You have received<br> " + Utils.surveyPoints + " points<br> for the<br> &quot;Diversity Of Life Survey&quot;<br>");
   		     out.println("Thanks!</center></td></tr></table>");	
        	}
        }
        
        out.println("<input type=\"hidden\" name=\"treeXML\" value=\"$treeXML\">");
        out.println("<input type=\"hidden\" name=\"Q1\" value=\"$Q1\">");
        out.println("<input type=\"hidden\" name=\"Q2\" value=\"$Q2\">");
        out.println("<input type=\"hidden\" name=\"Q3\" value=\"$Q3\">");
        out.println("<input type=\"submit\" value=\"Submit Survey\" onclick=\"return isComplete();\">");
        out.println("</center></td></tr> ");
        out.println("<tfoot><tr><td valign=\"bottom\">");
        out.println("<center>For any issues<br> with this site<br> click here<br> ");
        out.println("<input type=\"button\" value=\"Site Issues\" onclick=\"window.open('" + Utils.googleCodeURL + "','','fullscreen=yes,toolbar=yes,menubar=yes,status=yes,scrollbars=yes,directories=yes,resizable=yes');\"><br> ");
        out.println("</center>");
        out.println("</td></tr></tfoot>");
        out.println("</table>");
        out.println("</td></tr></table>");
      	out.println("</form>");
      	
        out.println("</body>");
        out.println("</html>");
        
        out.close();
	}
	
	

}
