package com.tbs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tbs.Utils;
import com.tbs.dao.TBSJdbcDao;

/**
 * Servlet implementation class AdminAppletServlet
 */
public class AdminAppletServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminAppletServlet() {
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
		
		String browser = (String) session.getAttribute("Browser");
		
		List<String[]> students = (List<String[]>) session.getAttribute("Students");
		Map<String, String[]> studentMap = new TreeMap<String, String[]>();
		String[] studentData;
		for(String[] student : students){
			if(!studentMap.containsKey(student[0])){
				studentData = new String[]{"","","","","","",""};
				studentData[0] = student[0];
				studentData[6] = student[1];
				studentMap.put(student[0], studentData);
			}
		}
		TBSJdbcDao dao = new TBSJdbcDao();
		List<String[]> studentSurveys = new LinkedList<String[]>();
		try{
			studentSurveys = dao.loadSurveys();
        	for(String[] survey : studentSurveys){
        		studentData = studentMap.get(survey[0]);
        		if(studentData != null){
        			studentData[1] = survey[1];
        			studentData[2] = survey[2];
        			studentData[3] = survey[3];
        			studentData[4] = survey[4];
        			studentData[5] = "";
        			studentMap.put(survey[0], studentData);	
        		}
        	}
        }catch(Exception e){
        	System.out.println("Error retrieving information from database");
        }
		
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>TBS Student Survey</title>");
        out.println("<SCRIPT LANGUAGE=\"JavaScript\" SRC=\"" + request.getContextPath() + "/scripts/common_functions.js\"></SCRIPT>");
        out.println("</head>");
        
        out.println("<body bgcolor = \"lightblue\" style=\"border: 0;padding: 0;margin:0;\">"); 
    	out.println("<form name=\"form\" style=\"border: 0;padding: 0;margin:0;\">");
      	out.println("<table width=\"100%\" height=\"100%\" style=\"border-collapse: collapse;padding: 0;margin: 0;\">");
      	out.println("<tr><td width=\"85%\"> ");
    	out.println("<applet code=\"tbs.TBSApplet.class\" archive=\"" + request.getContextPath() + "/TBSRun.jar\" width=\"100%\" height=\"100%\" name=\"TreeApplet\"> ");
    	
    	int index=0;
    	for(Map.Entry<String, String[]> e : studentMap.entrySet()){
    		index++;
    		StringBuffer param = new StringBuffer("<param name=\"Student");
    		param.append(index).append("\" value=\"");
    		for(String val : e.getValue())
    			param.append(val).append("+=");
    		param.append("\">");
    		out.println(param.toString());
    	}
    	
    	out.println("<param name=\"Admin\" value=\"true\">");
    	out.println("<param name=\"StudentCount\" value=\"" + index + "\">");
    	out.println("<param name=\"Browser\" value=\"" + browser + "\">");
      	out.println("You have to enable Java on your machine!");
      	out.println("</applet> ");
      	out.println("</td>");
      	out.println("<td width=\"15%\" height=\"100%\" align=\"center\">");
      	out.println("<table style=\"border-collapse: collapse;padding: 0;margin: 0;height:100%;\">");
      	out.println("<tr><td valign=\"top\"><center>");
      	out.println("<input type=\"button\" value=\"Logout\" onclick=\"window.location = '" + request.getContextPath() + "/Logout';\">");
      	out.println("</center></td></tr>");
      	out.println("<tr><td><center>");
      	out.println("<font size=+1>Diversity of Life<br> Survey<br> Administrator Version</font><br>");
      	out.println("</center></td></tr>");
      	out.println("<tfoot><tr><td valign=\"bottom\">");
      	out.println("<center>For any issues<br> with this site<br> click here<br>");
      	out.println("<input type=\"button\" value=\"Site Issues\" onclick=\"window.open('" + Utils.googleCodeURL + "','','fullscreen=yes,toolbar=yes,menubar=yes,status=yes,scrollbars=yes,directories=yes,resizable=yes');\"><br>");
      	out.println("</center>");
      	out.println("</td></tr></tfoot>");
      	out.println("</table>");
      	out.println("</td>");
      	out.println("</tr></table>");
      	out.println("</form>");
      	
      	out.println("</body>");
        out.println("</html>");
        
        out.close();
	}

}
