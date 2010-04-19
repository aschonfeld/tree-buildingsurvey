package com.tbs.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ErrorServlet
 */
public class ErrorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ErrorServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
	    PrintWriter out = response.getWriter();

	    String code = null, message = null, type = null, uri = null;
	    Object codeObj, messageObj, typeObj;
	    Throwable throwable;

	    // Retrieve the three possible error attributes, some may be null
	    codeObj = request.getAttribute("javax.servlet.error.status_code");
	    messageObj = request.getAttribute("javax.servlet.error.message");
	    typeObj = request.getAttribute("javax.servlet.error.exception_type");
	    throwable = (Throwable)
	      request.getAttribute("javax.servlet.error.exception");
	    uri = (String) 
	      request.getAttribute("javax.servlet.error.request_uri");

	    if (uri == null) {
	      uri = request.getRequestURI(); // in case there's no URI given
	    }

	    // Convert the attributes to string values
	    if (codeObj != null) code = codeObj.toString();
	    if (messageObj != null) message = messageObj.toString();
	    if (typeObj != null) type = typeObj.toString();

	    // The error reason is either the status code or exception type
	    String reason = (code != null ? code : type);

	    out.println("<HTML>");
	    out.println("<HEAD><TITLE>" + reason + ": " + message +
	                "</TITLE></HEAD>");
	    out.println("<BODY>");
	    out.println("<H1>" + reason + "</H1>");
	    out.println("<H2>" + message + "</H2>");
	    out.println("<PRE>");
	    if (throwable != null)
	      throwable.printStackTrace(out);
	    out.println("</PRE>");
	    out.println("<HR>");
	    out.println("<I>Error accessing " + uri + "</I><br><br>");
	    out.println("<center>");
	    out.println("Click Here to go back to Login<br>");
	    out.println("<a href=\"Login\">TBS CGI Servlet</a>");
	    out.println("</center>");
	    out.println("</BODY></HTML>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
