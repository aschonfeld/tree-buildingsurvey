package admin.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Invoked to handle a single row in a <code>ResultSet</code>. When a result set
 * is retrieved, it is typically looped through, handling one row at a time. For
 * each row, this class is invoked to return an object that represents that
 * single record.
 * 
 * @param <T>
 *            the type that represents a single row.
 */
public interface ResultSetRowHandler<T> {

	public T handle(ResultSet rs) throws SQLException;
}
