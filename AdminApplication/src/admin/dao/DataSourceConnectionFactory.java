package admin.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles the retrieval of connections from all configured data sources.
 * <p>
 * This class uses <code>common.properties</code> to retrieve the JNDI values for each datasource.
 * Therefore, <code>common.properties</code> must exist in the classpath and the property
 * <code>CommonProperties.DATASOURCE_KEY</code> must be specified.
 * </p>
 * <p>
 * If more than one datasource is needed, additional datasource entries can be added to the properties file.
 * You can then use <code>getConnection(String key)</code> (specifying the key to the property in the file)
 * to retrieve a connection from the additional datasource(s).
 * </p>
 */
public class DataSourceConnectionFactory {
    public static final String CONN_DRIVER = "com.mysql.jdbc.Driver";
    public static final String CONN_URL = "jdbc:mysql://localhost/";
    public static final String CONN_UNAME = "root";
    public static final String CONN_PWORD = "testpw";
    
    // allow only static access
    private DataSourceConnectionFactory() {}
    
    /**
     * Retrieves a connection from the desired data source.
     * <p>
     * The data source lookup logic used is as follows:
     * <ol>
     * <li>
     * The <code>key</code> is used to find the cached data source reference. 
     * </li>
     * <li>
     * If a cached entry does not exist, the <code>key</code> is used as a key to the data source properties file
     * to retrieve the JNDI value to use for the lookup. If an entry exists in the
     * properties file, the retrieved value is used to do the lookup. The data source
     * is then stored in the cache.
     * </li>
     * <li>
     * If a properties entry does not exist, the value of <code>key</code>
     * is used directly as the JNDI value to do the lookup. The data source is then
     * stored in the cache.
     * </li>
     *
     * </ol>
     * Once a data source has been found, the connection is retrieved from it.
     * </p>
     * 
     * @param key the key used to find the data source.
     * @return a connection
     * 
     * @throws SQLException if an error occurs while retrieving the connection.
     * @throws NullPointerException if any of the following occurs
     *                              <ul>
     *                              <li>the <code>key</code> is <code>null</code> or empty.</li>
     *                              <li>the data source is not found in cache, and is not found after the lookup process.</li>
     *                              </ul>
     */
    public static Connection getConnection(String database, String username, String password) throws Exception{
    	Connection conn = null;
    	try{
    		Class.forName(CONN_DRIVER).newInstance ();
            conn = DriverManager.getConnection(CONN_URL + database, username, password);
            System.out.println("Database connection established");
    	}catch (Exception e){
            System.err.println(String.format("Cannot connect to server:%s", CONN_URL));
            throw e;
        }
        return conn;
    }
    
    public static Connection getConnection(String database) throws Exception{
    	
        return getConnection(database, CONN_UNAME, CONN_PWORD);
    }
  
}
