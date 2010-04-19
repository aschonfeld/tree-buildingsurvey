package com.tbs.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tbs.Utils;
import com.tbs.dao.TBSJdbcDao;

/**
 * Servlet implementation class ValidationServlet
 */
public class ValidationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ValidationServlet() {
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
		String student = request.getParameter("Name");
		String inputPass = request.getParameter("Passwd");
		String browser = request.getParameter("Browser");
		String adminParam = request.getParameter("AdminValue");
		if(Utils.isStringEmpty(student) || Utils.isStringEmpty(inputPass)){
			response.sendRedirect(response.encodeRedirectURL(Utils.getBaseURL(request) + "/Login"));
    		return;
    	}
        Boolean admin = false;
        if(adminParam != null && adminParam.length() > 0)
        	admin = Boolean.valueOf(adminParam);
        
		TBSJdbcDao dao = new TBSJdbcDao();
		
		String savedPass = "", section="";
        try{
        	List<String[]> results = dao.loadStudentLogin(student);
        	if(results.size() > 0){
        		section = results.get(0)[0];
        		savedPass = results.get(0)[1];
        	}
        }catch(Exception e){
        	System.out.println("Error retrieving information from database");
        }
        if(!admin){
        	if(Utils.validate(student, inputPass, savedPass)){
        		RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login?invalidLogin=true");
        		rd.forward(request, response);
        	}else{
        		HttpSession session = request.getSession(true);
        		session.setAttribute("Name", student);
        		session.setAttribute("Browser", browser);
        		session.setAttribute("Section", section);

        		RequestDispatcher rd = getServletContext().getRequestDispatcher("/StudentApplet");
        		rd.forward(request, response);
        	}
        }else{
        	if(!"lab09acce55".equals(inputPass)){
        		RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login?invalidLogin=true");
        		rd.forward(request, response);
        	}else{
        		HttpSession session = request.getSession(true);
        		session.setAttribute("Browser", browser);

        		RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminApplet");
        		rd.forward(request, response);
        	}
		}
	}

}
