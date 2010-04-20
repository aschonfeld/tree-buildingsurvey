package com.tbs;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class Utils {
	
	public static String surveyPoints = "15";
	public static String googleCodeURL = "http://code.google.com/p/tree-buildingsurvey/issues/list";

	public static boolean isStringEmpty(String s){
		return (s == null || s.length() == 0);
	}
	
	public static Boolean validate(String student, String inputPass, String savedPass){
        
        String time = System.currentTimeMillis() + "";
        if(JCrypt.crypt(inputPass, time).equals(savedPass) 
        		&& !isStringEmpty(inputPass) && !isStringEmpty(savedPass))
        	return true;
        return false;
	}
	
	public static boolean surveyExpired(){
		Calendar cal = Calendar.getInstance();
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.set(Calendar.MONTH, Calendar.DECEMBER);
        expirationDate.set(Calendar.DAY_OF_MONTH, 12);
        return cal.after(expirationDate);
	}
	
	public static String getBaseURL(HttpServletRequest request){
		return request.getScheme() + "://" + request.getServerName() 
			+ ":" + request.getServerPort() + request.getContextPath();
	}
	
	public static boolean sessionExists(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws ServletException,
			IOException {
		if(session.getAttributeNames().hasMoreElements())
			return true;
		response.sendRedirect(response.encodeRedirectURL(getBaseURL(request) + "/Login"));
		return false;
	}
}
