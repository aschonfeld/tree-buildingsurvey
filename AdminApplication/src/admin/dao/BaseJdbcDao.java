package admin.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Base DAO class that includes many helper methods to simplify/reduce the code
 * needed to make JDBC calls to the data store.
 * 
 * Here are a couple examples on how to implement your class by extending this base
 * class:
 * 
 * <code>
 * <pre>
 * public class MyDao extends BaseDao {
 *     public List&lt;String&gt; getZipCodesForState(String stateCode) {
 *         
 *         // setup params
 *         InParam[] params = new InParam[1];
 *         params[0] = new InParam(1, stateCode);
 *         
 *         // create row handler
 *         ResultSetRowHandler&lt;String&gt; handler = new ResultSetRowHandler&lt;String&gt;() {
 *             public String handle(ResultSet rs) throws SQLException {
 *                 String zipCode = rs.getString(1);
 *                 return zipCode;
 *             }
 *         };
 *         
 *         try {
 *             // retrieve default connection
 *             Connection conn = getConnection();
 *             
 *             // execute query
 *             List&lt;String&gt; results = executeQuery("SELECT ZIPCODE FROM ZIPCODES_TABLE WHERE STATECODE = ?", handler, conn, true, params);
 *             return results;
 *         } catch (SQLException e) {
 *             // handle exception 
 *             // (don't worry about ResultSet, Statement, or Connection as they have been closed by executQuery method)
 *         }
 *     }
 *     
 *     public int deleteZipCode(String zipCode) {
 *         // setup params
 *         InParam[] params = new InParam[1];
 *         params[0] = new InParam(1, zipCode);
 *         
 *         try {
 *             // retrieve default connection
 *             Connection conn = getConnection();
 *             
 *             // execute update
 *             int count = executeUpdate("DELETE FROM ZIPCODES_TABLE WHERE ZIPCODE = ?", conn, true, params);
 *             if (count < 1) {
 *                 // no record was deleted, might need to take action on this
 *             }
 *             return count;
 *         } catch (SQLException e) {
 *             // handle exception
 *             // (don't worry about Statement or Connection as they have been closed by executUpdate method)
 *         }
 *     }
 * }
 * </pre>
 * </code>
 */
public abstract class BaseJdbcDao {
    public static final int CALL_SUCCESS = 0;
    
    /**
     * Returns a connection from the default data source. This is equivalent to
     * calling <code>DataSourceConnectionFactory.getConnection</code>.
     * 
     * @return a connection.
     * @throws SQLException if an error occurs while retrieving the connection.
     */
    protected static Connection getConnection(String database, String username, String password) throws Exception {
        return DataSourceConnectionFactory.getConnection(database, username, password);
    }
    
    /**
     * This is equivalent to calling the more verbose <code>executeQuery</code> method while passing
     * in a default connection instance and setting <code>closeConn</code> to <code>true</code>.
     */
    protected static <T> List<T> executeQuery(String database, String sql, ResultSetRowHandler<T> handler, InParam... params) throws Exception {
        Connection conn = DataSourceConnectionFactory.getConnection(database);
        boolean closeConn = true;
        
        return executeQuery(sql, handler, conn, closeConn, params);
    }
    
    /**
     * Method to simplify/reduce the amount of code needed to make a JDBC query.
     * <p>
     * This method also ensures that the resources are closed properly (even when
     * an exception occurs). The <code>ResultSet</code> and <code>Statement</code> 
     * will always be closed, and the connection will be closed if <code>closeConn</code>
     * is set to true.
     * </p>
     * 
     * @param sql the SQL query to execute. This SQL will be executed by a <code>PreparedStatement</code>,
     *            therefore you can add the '?' marker and specify the 
     *            parameter with the list of <code>InParam</code> objects separately.
     * @param handler the handler that will be invoked for each resulting record.
     * @param conn the connection
     * @param closeConn whether or not this method should close the connection when finished.
     *                  Specify <code>true</code> to have this method automatically close the 
     *                  connection once the query is finished (so that it does not have
     *                  to be done manually within the caller's code). The <code>ResultSet</code> and
     *                  <code>Statement</code> will always be closed regardless of this value.
     * @param params a list of parameters to be set into the generated <code>PreparedStatment</code>.
     * @return a list of &lt;T&gt; items that were returned by the query (and specified handler).
     * 
     * @throws SQLException if an error occurs while executing the query.
     */
    protected static <T> List<T> executeQuery(String sql, ResultSetRowHandler<T> handler, Connection conn, boolean closeConn, InParam... params) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<T> resultList = new ArrayList<T>(10);
        
        try {
            // prepare statement
            stmt = conn.prepareStatement(sql);
            setInputParams(stmt, params);
            
            // execute query
            rs = stmt.executeQuery();
            
            // handle results
            while (rs.next()) {
                T result = handler.handle(rs);
                if (result != null) {
                    resultList.add(result);
                }
            }
            
            return resultList;
        } finally {
            closeResources(rs, stmt, conn, closeConn);
        }
    }
    
    /**
     * This is equivalent to calling the more verbose <code>executeUpdate</code> method while passing
     * in a default connection instance and setting <code>closeConn</code> to <code>true</code>.
     */
    protected static int executeUpdate(String database, String sql, InParam... params) throws Exception {
        Connection conn = DataSourceConnectionFactory.getConnection(database);
        boolean closeConn = true;
        return executeUpdate(sql, conn, closeConn, params);
    }
    
    /**
     * Method to simplify/reduce the amount of code needed to make a JDBC update statement.
     * <p>
     * This method also ensures that the resources are closed properly (even when
     * an exception occurs). The <code>Statement</code> 
     * will always be closed, and the connection will be closed if <code>closeConn</code>
     * is set to true.
     * </p>
     * 
     * @param sql the SQL statement to execute. This SQL will be executed by a <code>PreparedStatement</code>,
     *            therefore you can add the '?' marker and specify the 
     *            parameter with the list of <code>InParam</code> objects separately.
     * @param conn the connection
     * @param closeConn whether or not this method should close the connection when finished.
     *                  Specify <code>true</code> to have this method automatically close the 
     *                  connection once the query is finished (so that it does not have
     *                  to be done manually within the caller's code).
     *                  The <code>Statement</code> will always be closed regardless of this value.
     * @param params a list of parameters to be set into the generated <code>PreparedStatment</code>.
     * @return a count of how many records were modified or deleted by this execution.
     * @throws SQLException if an error occurs while executing the statement.
     */
    protected static int executeUpdate(String sql, Connection conn, boolean closeConn, InParam... params) throws SQLException {
        PreparedStatement stmt = null;
        
        try {
            // prepare statement
            stmt = conn.prepareStatement(sql);
            setInputParams(stmt, params);
            
            // execute insert/update and return record count
            return stmt.executeUpdate();
        } finally {
            closeResources(stmt, conn, closeConn);
        }
    }
    
    /**
     * Method to simplify/reduce the amount of code needed to make a JDBC call statement (calling a stored procedure for example).
     * <p>
     * This method should only be used when there are no <code>ResultSet</code> objects to be retrieved by this call.
     * Therefore, none of the passed in <code>Param</code> objects should be of type OUT and be expecting a resultSet to be returned
     * (i.e. Param.isResultSet should return <code>false</code>).
     * </p>
     * <p>
     * For each <code>Param</code> objects of type OUT, the method <code>Parm.setObject</code> will be called, inserting the resulting
     * output object from the <code>CallableStatement</code>.
     * </p>
     * <p>
     * This method also ensures that the resources are closed properly (even when
     * an exception occurs). The <code>Statement</code> 
     * will always be closed, and the connection will be closed if <code>closeConn</code>
     * is set to true.
     * </p>
     * 
     * @param sql the SQL statement to execute. This SQL will be executed by a <code>CallableStatement</code>,
     *            therefore you can add the '?' marker and specify the 
     *            parameter with the list of <code>Param</code> objects separately.
     * @param conn the connection
     * @param closeConn whether or not this method should close the connection when finished.
     *                  Specify <code>true</code> to have this method automatically close the 
     *                  connection once the query is finished (so that it does not have
     *                  to be done manually within the caller's code).
     *                  The <code>Statement</code> will always be closed regardless of this value.
     * @param params a list of parameters to be set into the generated <code>CallableStatement</code>.
     *               IN, OUT, and INOUT parameters are all accepted. However, this list should not contain
     *               any OUT parameters that expect a <code>ResultSet</code>.
     * @throws SQLException if an error occurs while executing the statement.
     */
    protected static void executeCall(String sql, Connection conn, boolean closeConn, Param... params) throws Exception {
        executeCall(sql, null, conn, closeConn, params);
    }
    
    /**
     * Method to simplify/reduce the amount of code needed to make a JDBC call statement (calling a stored procedure for example).
     * <p>
     * This method should be used when one or more of the output objects returned by this call is a <code>ResultSet</code>.
     * In order for a <code>Param</code> to handle a <code>ResultSet</code>, the value of
     * <code>Param.isResultSet</code> should be <code>true</code>).
     * The output object returned by this call will then be treated as a <code>ResultSet</code>, and 
     * the specified handler will be invoked for each row. The final list of all items returned by the handler
     * will be set into the <code>Param</code> by calling <code>Param.setObject</code>.
     * </p>
     * <p>
     * For each <code>Param</code> objects of type OUT that is not expecting a <code>ResultSet</code>, 
     * the method <code>Parm.setObject</code> will be called, inserting the resulting
     * output object from the <code>CallableStatement</code>.
     * </p>
     * 
     * <p>
     * This method also ensures that the resources are closed properly (even when
     * an exception occurs). The <code>ResultSet</code> (if any) and <code>Statement</code> 
     * will always be closed, and the connection will be closed if <code>closeConn</code>
     * is set to true.
     * </p>
     * 
     * <p><b>
     * There is an obvious limitation to this current implementation. Currently, 
     * the same handler must be used for all <code>ResultSet</code> objects returned.
     * </b></p>
     * 
     * @param <T> the type that is returned by the handler when invoked on each row in an expected output <code>ResultSet</code>.
     * @param sql the SQL statement to execute. This SQL will be executed by a <code>CallableStatement</code>,
     *            therefore you can add the '?' marker and specify the 
     *            parameter with the list of <code>Param</code> objects separately.
     * @param handler the handler to be used for all returned <code>ResultSet</code> objects.
     * @param conn the connection
     * @param closeConn whether or not this method should close the connection when finished.
     *                  Specify <code>true</code> to have this method automatically close the 
     *                  connection once the query is finished (so that it does not have
     *                  to be done manually within the caller's code). The <code>ResultSet</code> (if any) and
     *                  <code>Statement</code> will always be closed regardless of this value.
     * @param params a list of parameters to be set into the generated <code>CallableStatement</code>.
     *               IN, OUT, and INOUT parameters are all accepted.
     * @throws SQLException if an error occurs while executing the statement.
     */
    protected static <T> void executeCall(String sql, ResultSetRowHandler<T> handler, Connection conn, boolean closeConn, Param... params) throws Exception {
        CallableStatement stmt = null;
        
        try {
            // prepare callable statement
            stmt = conn.prepareCall(sql);
            setParams(stmt, params);
            
            // execute call
            stmt.execute();
            
            // handle output params
            handleCallResults(stmt, handler, params);
        } finally {
            closeResources(stmt, conn, closeConn);
        }
    }
    
    /**
     * Sets the given input parameters onto the <code>PreparedStatement</code>.
     * <p>
     * This method simply calls <code>PreparedStatement.setObject(int parameterIndex, Object x, int targetSqlType, int scale)</code>
     * using the information set in the <code>InParam</code> object.
     * </p>
     * 
     * @param stmt the statement
     * @param params the params to set onto the statement
     * @throws SQLException if an error occurs while setting the parameters onto the statement.
     */
    protected static void setInputParams(PreparedStatement stmt, InParam... params) throws SQLException {
        for (InParam param : params) {
            stmt.setObject(param.getIndex(), param.getObject(), param.getSqlType(), param.getScale());
        }
    }
    
    /**
     * Sets the given input parameters onto the <code>PreparedStatement</code>.
     * <p>
     * For an IN parameter, this method calls 
     * <code>CallableStatement.setObject(int parameterIndex, Object x, int targetSqlType, int scale)</code>
     * using the information set in the <code>Param</code> object.
     * </p>
     * 
     * <p>
     * For an OUT parameter, this method calls 
     * <code>CallableStatement.registerOutParameter(int paramIndex, int sqlType, String typeName)</code>
     * using the information set in the <code>Param</code> object.
     * </p>
     * 
     * <p>
     * For an INOUT parameter, this method calls both of the methods listed above.
     * </p>
     * 
     * @param stmt the statement
     * @param params the params to set onto the statement
     * @throws SQLException if an error occurs while setting the parameters onto the statement.
     */
    protected static void setParams(CallableStatement stmt, Param...params) throws SQLException {
        for (Param param : params) {
            Param.INOUT_TYPE inOutType = param.getInOutType();
            if (!(Param.INOUT_TYPE.IN == inOutType || Param.INOUT_TYPE.OUT == inOutType || Param.INOUT_TYPE.INOUT == inOutType)) {
                throw new AssertionError("Unexpected enumerated value [" + inOutType.toString() + "] for Param.INOUT_TYPE");
            }
            
            if (Param.INOUT_TYPE.IN == inOutType || Param.INOUT_TYPE.INOUT == inOutType) {
                stmt.setObject(param.getIndex(), param.getObject(), param.getSqlType(), param.getScale());
            } 

            if (Param.INOUT_TYPE.OUT == inOutType || Param.INOUT_TYPE.INOUT == inOutType) {
                String typeName = param.getTypeName();
                if (typeName != null && typeName.trim().length() > 0) {
                    stmt.registerOutParameter(param.getIndex(), param.getSqlType(), param.getTypeName());
                } else {
                    stmt.registerOutParameter(param.getIndex(), param.getSqlType(), param.getScale());
                }
            }
        }
    }
    
    /**
     * Handles any output <code>ResultSet</code> objects returned by the <code>CallableStatement</code>.
     * <p>
     * For each OUT or INOUT <code>Param</code> that has been set to represent a <code>ResultSet</code>,
     * the corresponding output object will be treated as a <code>ResultSet</code>. The same handler
     * will be used for all returned <code>ResultSet</code> objects. The final list of all items returned by the handler
     * will be set into the <code>Param</code> by calling <code>Param.setObject</code>.
     * </p>
     * 
     * <p>
     * For each OUT or INOUT <code>Param</code> that has NOT been set to represent a <code>ResultSet</code>,
     * the returned object from the <code>CallableStatement</code> is directly inserted into the
     * <code>Param</code> by calling <code>Param.setObject</code>.
     * </p>
     * <p><b>
     * There is an obvious limitation to this current implementation. Currently, 
     * the same handler must be used for all <code>ResultSet</code> objects returned.
     * </b></p>
     * 
     * @param <T> the type that is returned by the handler when invoked on each row in an expected output <code>ResultSet</code>.
     * @param stmt the SQL statement to handle the output for.
     * @param handler the handler to be used for all returned <code>ResultSet</code> objects.
     * @param params the list of parameters used to help determine how to handle the 
     *               returned objects from the <code>CallableStatement</code>
     * @throws SQLException if an error occurs while handling the output objects.
     */
    protected static <T> void handleCallResults(CallableStatement stmt, ResultSetRowHandler<T> handler, Param... params) throws Exception {
        for (Param param : params) {
            Param.INOUT_TYPE inOutType = param.getInOutType();
            
            if (!(Param.INOUT_TYPE.OUT == inOutType || Param.INOUT_TYPE.INOUT == inOutType)) {
                continue;
            }
            
            Object outputObj = stmt.getObject(param.getIndex());
            
            if (param.isResultSet()) {
                List<T> resultList = new ArrayList<T>(10);
                ResultSet rs = null;
                try {
                    rs = (ResultSet) outputObj;
                    while (rs.next()) {
                        if (handler == null) {
                            throw new NullPointerException("A ResultSet ouptut type was found, but the given ResultSetRowHandler is null.");
                        }
                        
                        T result = handler.handle(rs);
                        resultList.add(result);
                    }
                    
                    param.setObject(resultList);
                } finally {
                    closeResultSetOnly(rs);
                }
            } else {
                param.setObject(outputObj);
            }
        }
    }
    
    /**
     * Properly closes the given connection. This method handles all checks for null,
     * and ignores any <code>SQLException</code> that may occur when trying to close
     * the connection.
     */
    protected static void closeConnection(Connection conn) {
        if (conn != null) try { conn.close(); } catch (SQLException e) {} // do nothing
        
        conn = null;
    }
    
    /**
     * Properly closes the given result set. This method handles all checks for null,
     * and ignores any <code>SQLException</code> that may occur when trying to close
     * the result set.
     */
    protected static void closeResultSetOnly(ResultSet rs) {
        if (rs != null) try { rs.close(); } catch (SQLException e) {} // do nothing
        
        rs = null;
    }
    
    /**
     * Properly closes the given resources. This method handles all checks for null,
     * and ignores any <code>SQLException</code> that may occur when trying to close
     * the resources. The connection is only closed if <code>closeConn</code> is
     * <code>true</code>.
     * 
     * @param rs the result set to close.
     * @param stmt the statement to close.
     * @param conn the connection to close (depending on the value of <code>closeConn</code>).
     * @param closeConn whether or not this method should close the connection.
     *                  Specify <code>true</code> to have this method close the 
     *                  connection as well.
     */
    protected static void closeResources(ResultSet rs, Statement stmt, Connection conn, boolean closeConn) {
        if (rs != null) try { rs.close(); } catch (SQLException e) {} // do nothing
        if (stmt != null) try { stmt.close(); } catch (SQLException e) {} // do nothing
        if (closeConn) {
            if (conn != null) try { conn.close(); } catch (SQLException e) {} // do nothing
        }
        
        rs = null;
        stmt = null;
        if (closeConn) conn = null;
    }
    
    /**
     * Properly closes the given resources. This method handles all checks for null,
     * and ignores any <code>SQLException</code> that may occur when trying to close
     * the resources. The connection is only closed if <code>closeConn</code> is
     * <code>true</code>.
     * 
     * @param stmt the statement to close.
     * @param conn connection to close (depending on the value of <code>closeConn</code>).
     * @param closeConn whether or not this method should close the connection.
     *                  Specify <code>true</code> to have this method close the 
     *                  connection as well.
     */
    protected static void closeResources(Statement stmt, Connection conn, boolean closeConn) {
        if (stmt != null) try { stmt.close(); } catch (SQLException e) {} // do nothing
        if (closeConn) {
            if (conn != null) try { conn.close(); } catch (SQLException e) {} // do nothing
        }
        
        stmt = null;
        if (closeConn) conn = null;
    }
    
    /**
     * Represents a parameter to be set on a <code>PreparedStatement</code>.
     * <p>
     * This class can represent IN, OUT, and even INOUT parameters.
     * </p>
     * <p>
     * For OUT parameters, the return object from a statement is to be inserted into
     * this class by calling <code>setObject</code>.
     * </p>
     * <p>
     * OUT parameters that need to represent a returned <code>ResultSet</code> are
     * handled slightly differently. You must first make sure to set the
     * <code>isResultSet</code> property to <code>true</code>. When <code>BaseDao</code>
     * handles the output objects returned by the statement, it will treat this output object
     * as a <code>ResultSet</code> (using the specified handler to generate a list of items,
     * each representing a single row in the result set), and finally insert the generated list
     * into this class by calling <code>setObject</code>. This allows <code>BaseDao</code>
     * to immediately close the result set when finished.
     * </p>
     */
    public static class Param {
        public enum INOUT_TYPE {IN, OUT, INOUT};
        
        private int m_index = 0;
        private Object m_object = null;
        private INOUT_TYPE m_inOutType = INOUT_TYPE.IN;
        private int m_sqlType = Types.VARCHAR;
        private String m_typeName = null;
        private int m_scale = 0;
        private boolean m_isResultSet = false;
        
        /**
         * Creates an instance, specifiying all properties.
         * <p>
         * To simplify the creation of a parameter, try using
         * <code>InParam</code>, or <code>OutParam</code>. These subclasses
         * have simplified constructors which help to populate the known properties
         * automatically based on the desired parameter type.
         * </p>
         * 
         * 
         * 
         * @param index the index within the statement to set this paratemeter (starting with 1).
         * @param obj the input value for any IN or INOUT types.
         * @param inOut specifies what type of parameter this is (IN, OUT, or INOUT).
         * @param sqlType the SQL type of this paramter. (e.g. values from java.sql.Type).
         * @param typeName the SQL type-name of this parameter (used only for OUT params).
         *                 This value can be <code>null</code>. 
         * @param scale the scale (useful only for numeric parameters)
         * @param isResultSet specifies whether or not this parameter represents 
         *                    a <code>ResultSet</code> output object. 
         * 
         * <p>
         * See the javadoc for <code>java.sql.PreparedStatement.setObject</code>
         * and <code>java.sql.CallableStatement.registerOutputParameter</code> for more information
         * on these properties.
         * </p>
         */
        public Param(int index, Object obj, INOUT_TYPE inOut, int sqlType, String typeName, int scale, boolean isResultSet) {
            if (index < 1) {
                throw new IllegalArgumentException("Index must be equal to or greater than 1.");
            }
            
            if ((INOUT_TYPE.IN == inOut || INOUT_TYPE.INOUT == inOut) && isResultSet) {
                throw new IllegalArgumentException("ResultSet paramater cannot be of type IN or INOUT.");
            }
            
            m_index = index;
            m_object = obj;
            m_inOutType = inOut;
            m_sqlType = sqlType;
            m_typeName = typeName;
            m_scale = scale;
            m_isResultSet = isResultSet;
        }
        
        public int getIndex() { return m_index; }
        public Object getObject() { return m_object; }
        private void setObject(Object object) { m_object = object; }
        public INOUT_TYPE getInOutType() { return m_inOutType; }
        public int getSqlType() { return m_sqlType; }
        public String getTypeName() { return m_typeName; }
        public int getScale() { return m_scale; }
        public boolean isResultSet() { return m_isResultSet; }
    }
    
    /**
     * Contains simplified constructors to help quickly create various kinds
     * of IN parameters.
     */
    public static class InParam extends Param {
        
        /**
         * Constructor for an IN string param
         * 
         * @param index the index.
         * @param inputStr the value.
         */
        public InParam(int index, String inputStr) {
            super(index, inputStr, INOUT_TYPE.IN, Types.VARCHAR, null, 0, false);
        }
        
        /**
         * Constructor for an IN Integer param
         * 
         * @param index the index.
         * @param inputInt the value.
         */
        public InParam(int index, Integer inputInt) {
            super(index, inputInt, INOUT_TYPE.IN, Types.INTEGER, null, 0, false);
        }
        
        /**
         * Constructor for an IN Date (or Timestamp) param
         * 
         * @param index the index.
         * @param inputDate the value.
         * @param includeTime specifies whether or not to include the time portion of the date.
         */
        public InParam(int index, java.util.Date inputDate, boolean includeTime) {
            // this is ugly, but is the only way to get it to work without major/unnecessary refactoring
            super(index, 
                    inputDate == null ? null : (includeTime ? new java.sql.Timestamp(inputDate.getTime()) : new java.sql.Date(inputDate.getTime())), 
                    INOUT_TYPE.IN,
                    includeTime ? Types.TIMESTAMP : Types.DATE,
                    null,
                    0,
                    false);
        }
        
        /**
         * Constructor for an IN BigDecimal param
         * 
         * @param index the index.
         * @param inputBigDecimal the value.
         */
        public InParam(int index, BigDecimal inputBigDecimal) {
            super(index, inputBigDecimal, INOUT_TYPE.IN, Types.NUMERIC, null, 0, false);
        }
        
        /**
         * Constructor for an IN param of a specified SQL type.
         * 
         * @param index the index.
         * @param inputObj the value.
         * @param sqlType the SQL type.
         */
        public InParam(int index, Object inputObj, int sqlType) {
            super(index, inputObj, INOUT_TYPE.IN, sqlType, null, 0, false);
        }
        
        /**
         * Constructor for an IN param of a specified SQL type and scale.
         * 
         * @param index the index.
         * @param inputObj the value.
         * @param sqlType the SQL type.
         * @param scale the scale.
         */
        public InParam(int index, Object inputObj, int sqlType, int scale) {
            super(index, inputObj, INOUT_TYPE.IN, sqlType, null, scale, false);
        }
    }
    
    /**
     * Contains simplified constructors to help quickly create various kinds
     * of OUT parameters.
     */
    public static class OutParam extends Param {
        
        /**
         * Constructor for a simple OUT param
         * 
         * @param index the index.
         * @param sqlType the SQL type.
         */
        public OutParam(int index, int sqlType) {
            super(index, null, INOUT_TYPE.OUT, sqlType, null, 0, false);
        }
        
        /**
         * Constructor for an OUT param, with the ability to specify
         * whether or not it represents a returned <code>ResultSet</code>.
         * 
         * @param index the index.
         * @param sqlType the SQL type.
         * @param isResultSet specifies whether or not this parameter represents 
         *                    a <code>ResultSet</code> output object.
         */
        public OutParam(int index, int sqlType, boolean isResultSet) {
            super(index, null, INOUT_TYPE.OUT, sqlType, null, 0, isResultSet);
        }
        
        /**
         * Constructor for an OUT param, with the ability to specify
         * whether or not it represents a returned <code>ResultSet</code>,
         * and also the ability to set the type-name.
         * 
         * @param index the index.
         * @param sqlType the SQL type.
         * @param typeName the type-name.
         * @param isResultSet specifies whether or not this parameter represents 
         *                    a <code>ResultSet</code> output object.
         */
        public OutParam(int index, int sqlType, String typeName, boolean isResultSet) {
            super(index, null, INOUT_TYPE.OUT, sqlType, typeName, 0, isResultSet);
        }
    }
}
