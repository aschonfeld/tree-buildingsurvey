package com.tbs.dao;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TBSJdbcDao extends BaseJdbcDao {
	
	private static final String SQL_LOAD_SURVEYS = "SELECT name, date, tree, Q1, Q2 FROM student_testdata ORDER BY name ASC";
	private static final String SQL_LOAD_STUDENTS = "SELECT name, section FROM students ORDER BY name ASC";
	private static final String SQL_LOAD_STUDENT_LOGIN = "SELECT section, password FROM students WHERE name = ?";
	private static final String SQL_LOAD_SURVEY = "SELECT date, tree, Q1, Q2 FROM student_testdata WHERE name = ?";
	private static final String SQL_INSERT_SURVEY = "INSERT INTO student_testdata (Q1, Q2, Q3, tree, date, name) VALUES (?,?,?,?,NOW(),?)";
	private static final String SQL_UPDATE_SURVEY = "UPDATE student_testdata SET Q1 = ?, Q2 = ?, Q3 = ?, tree = ?, date = NOW() WHERE name = ?";
	
	/**
	 * This method is utilized by AdminApplication to retrieve the
	 * student surveys (name, tree, Q1, Q2) from the table, trees.student_data.
	 *       
	 * @return the desired <code>List</code> of <code>String[]</code> objects,
	 * 		   or <code>null</code> if there is no data.
	 * 
	 * @throws Exception
	 *             if an SQLException occurs.
	 * 
	 */
	public List<String[]> loadSurveys() throws Exception{
		System.out.println(String.format("Fetching Surveys. SQL is [%s].", SQL_LOAD_SURVEYS));
		ResultSetRowHandler<String[]> handler = new ResultSetRowHandler<String[]>() {
			public String[] handle(ResultSet rs) throws SQLException {
				String name, date, tree, Q1, Q2;
				name = rs.getString(1);
				date = rs.getDate(2).toString();
				tree = rs.getString(3);
				Blob blob = rs.getBlob(4);
				byte[] bdata = blob.getBytes(1, (int) blob.length());
				Q1 = new String(bdata);
				blob = rs.getBlob(5);
				bdata = blob.getBytes(1, (int) blob.length());
				Q2 = new String(bdata);
				return new String[]{name,date,tree,Q1,Q2};
			}
		};
		return executeQuery("trees", SQL_LOAD_SURVEYS, handler, new InParam[]{});
	}
	
	/**
	 * This method is utilized by AdminApplication to retrieve the
	 * students (name, section) from the table, grades.students.
	 *       
	 * @return the desired <code>List</code> of <code>String[]</code> objects,
	 * 		   or <code>null</code> if there is no data.
	 * 
	 * @throws Exception
	 *             if an SQLException occurs.
	 * 
	 */
	public List<String[]> loadStudents() throws Exception{
		System.out.println(String.format("Fetching Students. SQL is [%s].", SQL_LOAD_STUDENTS));
		ResultSetRowHandler<String[]> handler = new ResultSetRowHandler<String[]>() {
			public String[] handle(ResultSet rs) throws SQLException {
				String name, section;
				name = rs.getString(1);
				section = rs.getString(2);
				return new String[]{name,section};
			}
		};
		return executeQuery("grades", SQL_LOAD_STUDENTS, handler, new InParam[]{});
	}
	
	/**
	 * This method is utilized by StudentAppletServlet to retrieve the
	 * password of a student from the table, grades.students.
	 *       
	 * @return the desired <code>List</code> of <code>String[]</code> objects,
	 * 		   or <code>null</code> if there is no data.
	 * 
	 * @throws Exception
	 *             if an SQLException occurs.
	 * 
	 */
	public List<String[]> loadStudentLogin(String studentName) throws Exception{
		System.out.println(String.format("Fetching Students. SQL is [%s].", SQL_LOAD_STUDENT_LOGIN));
		ResultSetRowHandler<String[]> handler = new ResultSetRowHandler<String[]>() {
			public String[] handle(ResultSet rs) throws SQLException {
				String section, pass;
				section = rs.getString(1);
				pass = rs.getString(2);
				return new String[]{section,pass};
			}
		};
		
		// setup params
		InParam[] params = new InParam[1];
		params[0] = new InParam(1, studentName);

		return executeQuery("grades", SQL_LOAD_STUDENT_LOGIN, handler, params);
	}
	
	/**
	 * This method is utilized by StudentAppletServlet to retrieve the
	 * password of a student from the table, grades.students.
	 *       
	 * @return the desired <code>List</code> of <code>String[]</code> objects,
	 * 		   or <code>null</code> if there is no data.
	 * 
	 * @throws Exception
	 *             if an SQLException occurs.
	 * 
	 */
	public List<String[]> loadStudentSurvey(String studentName) throws Exception{
		System.out.println(String.format("Fetching Surveys. SQL is [%s].", SQL_LOAD_SURVEY));
		ResultSetRowHandler<String[]> handler = new ResultSetRowHandler<String[]>() {
			public String[] handle(ResultSet rs) throws SQLException {
				String date, tree, Q1, Q2;
				date = rs.getDate(1).toString();
				tree = rs.getString(2);
				Blob blob = rs.getBlob(3);
				byte[] bdata = blob.getBytes(1, (int) blob.length());
				Q1 = new String(bdata);
				blob = rs.getBlob(4);
				bdata = blob.getBytes(1, (int) blob.length());
				Q2 = new String(bdata);
				return new String[]{date,tree,Q1,Q2};
			}
		};
		
		// setup params
		InParam[] params = new InParam[1];
		params[0] = new InParam(1, studentName);

		return executeQuery("trees", SQL_LOAD_SURVEY, handler, params);
	}
	
	/**
	 * This method is utilized by StudentAppletServlet to retrieve the
	 * password of a student from the table, grades.students.
	 *       
	 * @return the desired <code>List</code> of <code>String[]</code> objects,
	 * 		   or <code>null</code> if there is no data.
	 * 
	 * @throws Exception
	 *             if an SQLException occurs.
	 * 
	 */
	public int insertStudentSurvey(String name, String tree,
			String Q1, String Q2, String Q3) throws Exception{
		System.out.println(String.format("Inserting Survey. SQL is [%s].", SQL_INSERT_SURVEY));
		
		// setup params
		InParam[] params = new InParam[5];
		params[0] = new InParam(1, Q1);
		params[1] = new InParam(2, Q2);
		params[2] = new InParam(3, Q3);
		params[3] = new InParam(4, tree);
		params[4] = new InParam(5, name);

		return executeUpdate("trees", SQL_INSERT_SURVEY, params);
	}
	
	/**
	 * This method is utilized by StudentAppletServlet to retrieve the
	 * password of a student from the table, grades.students.
	 *       
	 * @return the desired <code>List</code> of <code>String[]</code> objects,
	 * 		   or <code>null</code> if there is no data.
	 * 
	 * @throws Exception
	 *             if an SQLException occurs.
	 * 
	 */
	public int updateStudentSurvey(String name, String tree,
			String Q1, String Q2, String Q3) throws Exception{
		System.out.println(String.format("Updating Survey. SQL is [%s].", SQL_UPDATE_SURVEY));
		
		// setup params
		InParam[] params = new InParam[5];
		params[0] = new InParam(1, Q1);
		params[1] = new InParam(2, Q2);
		params[2] = new InParam(3, Q3);
		params[3] = new InParam(4, tree);
		params[4] = new InParam(5, name);

		return executeUpdate("trees", SQL_UPDATE_SURVEY, params);
	}
}
