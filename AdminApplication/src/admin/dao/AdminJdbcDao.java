package admin.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AdminJdbcDao extends BaseJdbcDao {

	private static final String SQL_LOAD_SURVEYS = "SELECT name, date, tree, Q1, Q2 FROM student_testdata  ORDER BY name ASC";
	private static final String SQL_LOAD_STUDENTS = "SELECT name, section FROM students ORDER BY name ASC";

	/**
	 * This method is utilized by AdminApplication to retrieve the student
	 * surveys (name, tree, Q1, Q2) from the table, trees.student_data.
	 * 
	 * @return the desired <code>List</code> of <code>String[]</code> objects,
	 *         or <code>null</code> if there is no data.
	 * 
	 * @throws Exception
	 *             if an SQLException occurs.
	 * 
	 */
	public List<String[]> loadSurveys(String username, String password)
			throws Exception {
		System.out.println(String.format("Fetching Surveys. SQL is [%s].",
				SQL_LOAD_SURVEYS));
		ResultSetRowHandler<String[]> handler = new ResultSetRowHandler<String[]>() {
			public String[] handle(ResultSet rs) throws SQLException {
				String name, date, tree, Q1, Q2;
				name = rs.getString(1);
				date = rs.getDate(2).toString();
				tree = rs.getString(3);
				Q1 = rs.getBlob(4).toString();
				Q2 = rs.getBlob(5).toString();
				return new String[] { name, date, tree, Q1, Q2 };
			}
		};
		Connection conn = getConnection("trees", username, password);
		return executeQuery(SQL_LOAD_SURVEYS, handler, conn, true,
				new InParam[] {});
	}

	/**
	 * This method is utilized by AdminApplication to retrieve the students
	 * (name, section) from the table, grades.students.
	 * 
	 * @return the desired <code>List</code> of <code>String[]</code> objects,
	 *         or <code>null</code> if there is no data.
	 * 
	 * @throws Exception
	 *             if an SQLException occurs.
	 * 
	 */
	public List<String[]> loadStudents(String username, String password)
			throws Exception {
		System.out.println(String.format("Fetching Students. SQL is [%s].",
				SQL_LOAD_STUDENTS));
		ResultSetRowHandler<String[]> handler = new ResultSetRowHandler<String[]>() {
			public String[] handle(ResultSet rs) throws SQLException {
				String name, section;
				name = rs.getString(1);
				section = rs.getString(2);
				return new String[] { name, section };
			}
		};
		Connection conn = getConnection("grades", username, password);
		return executeQuery(SQL_LOAD_STUDENTS, handler, conn, true,
				new InParam[] {});
	}
}
