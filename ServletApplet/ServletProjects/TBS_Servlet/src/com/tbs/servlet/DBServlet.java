package com.tbs.servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tbs.Utils;
import com.tbs.dao.TBSJdbcDao;

/**
 * Servlet implementation class DBServlet
 */
public class DBServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
       
	private TBSJdbcDao dao = null;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DBServlet() {
        super();
        dao = new TBSJdbcDao();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("in doGet(...)");
        String userOption = null;
        userOption = request.getParameterValues("UserOption")[0];
        ObjectOutputStream outputToApplet;
        
        System.out.println("userOption == " + userOption);
        
        List<String[]> returnData = new LinkedList<String[]>();
        if (userOption == null) {
            // simply display the students
        	System.out.println("Displaying the students");
        	try{
        		returnData = dao.loadStudents();
            }catch(Exception e){
            	System.out.println("Error retrieving information from database");
            }
        } else if (userOption.equals("Login")) {
        	// simply display the students
        	System.out.println("Displaying the students");
        	try{
        		returnData = dao.loadStudents();
            }catch(Exception e){
            	System.out.println("Error retrieving information from database");
            }
        } else if (userOption.equals("Students")) {
        	// simply display the students
        	System.out.println("Displaying the students");
        	try{
        		returnData = dao.loadStudents();
            }catch(Exception e){
            	System.out.println("Error retrieving information from database");
            }
        } else if (userOption.equals("Surveys")) {
    		try{
    			List<String[]> students = dao.loadStudents();
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
        		List<String[]> studentSurveys = new LinkedList<String[]>();
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
            	for(Map.Entry<String, String[]> e : studentMap.entrySet())
            		returnData.add(e.getValue());
            }catch(Exception e){
            	System.out.println("Error retrieving information from database");
            }
        }
        
        try
        {
            outputToApplet = new ObjectOutputStream(response.getOutputStream());
            
            System.out.println("Sending data to applet...");
            outputToApplet.writeObject(returnData);
            outputToApplet.flush();
            
            outputToApplet.close();
            System.out.println("Data transmission complete.");
        }catch (IOException e){
			e.printStackTrace(); 
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userOption = null;
		userOption = request.getParameterValues("UserOption")[0];
		ObjectInputStream inputFromApplet = null;
		String[] student = null;        
		PrintWriter out = null;

		try
		{  
			// get an input stream from the applet
			inputFromApplet = new ObjectInputStream(request.getInputStream());
			System.out.println("Connected");

			// read the serialized student data from applet        
			System.out.println("Reading data...");
			student = (String[]) inputFromApplet.readObject();
			System.out.println("Finished reading.");

			inputFromApplet.close();

			if (userOption.equals("Login")) {
				String[] studentData = new String[]{"","","","","","",""};
				String savedPass = "", section="";
		        try{
		        	List<String[]> results = dao.loadStudentLogin(student[0]);
		        	if(results.size() > 0){
		        		section = results.get(0)[0];
		        		savedPass = results.get(0)[1];
		        		if(!Utils.validate(student[0], student[1], savedPass)){
				        	List<String[]> data = dao.loadStudentSurvey(student[0]);
				            	if(data.size() > 0){
				            		studentData[0] = student[0];
				            		studentData[1] = data.get(0)[0];
				            		studentData[2] = data.get(0)[1];
			            			studentData[3] = data.get(0)[2];
			            			studentData[4] = data.get(0)[3];
			            			studentData[5] = "";
			            			studentData[6] = section;
				            	}else{
				            		studentData[0] = student[0];
				            		studentData[6] = section;
				            	}
				        	}
		        	}else
		        		studentData = new String[]{"","","","","","",""};
		        }catch(Exception e){
		        	System.out.println("Error retrieving information from database");
		        }
		        try
		        {
		        	ObjectOutputStream outputToApplet = new ObjectOutputStream(response.getOutputStream());
		        	System.out.println("Sending data to applet...");
		        	outputToApplet.writeObject(studentData);
		        	outputToApplet.flush();
		        	outputToApplet.close();
		        	System.out.println("Data transmission complete.");
		        }catch (IOException e){
		        	e.printStackTrace(); 
		        }
			} else if (userOption.equals("Save")) {

				try{
					List<String[]> data = dao.loadStudentSurvey(student[0]);
					int returnVal;
					if(data.size() == 0)
						returnVal = dao.insertStudentSurvey(student[0], student[1], student[2], student[3], "");
					else
						returnVal = dao.updateStudentSurvey(student[0], student[1], student[2], student[3], "");
					if(returnVal == 1)
						System.out.println("Survey data has been updated");
				}catch(Exception e){
					System.out.println("Error retrieving information from database");
				}

				System.out.println("Complete.");

				// send back a confirmation message to the applet
				out = new PrintWriter(response.getOutputStream());
				response.setContentType("text/plain");
				out.println("confirmed");
				out.flush();
				out.close(); 
			}


		}catch (Exception e){
			e.printStackTrace();    
		}

	}

}
