package org.jointheleague.le.recipes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * This sample program is a minimal Java application showing JDBC access to a
 * Derby database.
 * </p>
 * 
 */
public class SimpleApp
{
	private static final String DB_URL = "jdbc:derby:"
	        + System.getProperty("user.home") + "/databases/testDB";

	// Some statements
	private Statement statement;
	private PreparedStatement psInsert;
	private PreparedStatement psUpdate;

	// List of all statements
	private final List<Statement> statements = new ArrayList<Statement>();

	private Properties dbProperties;

	public static void main(String[] args) {
		new SimpleApp().go();
		System.out.println("SimpleApp finished");
	}

	private void go() {

		System.out.println("SimpleApp starting in embedded mode");

		Connection connection = null;

		try {
			dbProperties = loadProperties();

			connection = connect();

			// We want to control transactions manually. Autocommit is on by
			// default in JDBC.
			connection.setAutoCommit(false);

			/* Creating a statement object that we can use for running various
			 * SQL statements commands against the database. */
			statement = connection.createStatement();
			statements.add(statement);
			// We create the tables...
			createTables();

			//
			initializeStatements(connection);

			// and add a few rows...

//			addLocation(1956, "Webster St.");
//			addLocation(1910, "Union St.");

			// Let's update some rows as well...

//			updateLocation(1956, 180, "Grand Ave.");
//			updateLocation(180, 300, "Lakeshore Ave.");

			verifyLocations();

//			 deleteTables();

			/* We commit the transaction. Any changes will be persisted to the
			 * database now. */
			connection.commit();
			System.out.println("Committed the transaction");

			shutDownDB();
		} catch (SQLException sqle) {
			printSQLException(sqle);
		} finally {
			// release all open resources to avoid unnecessary memory usage
			try {
				disposeStatements();
				closeConnection(connection);
			} catch (SQLException sqle) {
				printSQLException(sqle);
			}

		}
	}

	/**
	 * Loads the properties of the database
	 * 
	 * By default, the schema APP will be used when no username is provided.
	 * Otherwise, the schema name is the same as the user name (in this case
	 * "user1" or USER1.)
	 * 
	 * Note that user authentication is off by default, meaning that any user
	 * can connect to your database using any password. To enable
	 * authentication, see the Derby Developer's Guide.
	 */
	private Properties loadProperties() {
		Properties props = new Properties(); // connection properties
		// providing a user name and password is optional
		props.put("user", "user1");
		props.put("password", "user1");
		props.put("driver", "org.apache.derby.jdbc.EmbeddedDriver");
		props.put("url", "jdbc:derby:");
		props.put("schema", "APP");
		props.put("derby.system.home", System.getProperty("user.home")
		        + "/databases");
		props.put("database", "testDB");
		return props;
	}

	/**
	 * This connection specifies create=true in the connection URL to cause the
	 * database to be created when connecting for the first time. To remove the
	 * database, remove the directory derbyDB (the same as the database name)
	 * and its contents.
	 * 
	 * The directory derbyDB will be created under the directory that the system
	 * property derby.system.home points to, or the current directory (user.dir)
	 * if derby.system.home is not set.
	 */
	private Connection connect()
	        throws SQLException {
		Connection connection = DriverManager.getConnection(DB_URL
		        + ";create=true", dbProperties);

		System.out.println("Connected to and created database " + DB_URL);
		return connection;
	}

	private boolean createTables() {
		String sqlExpression = "create table location("
		        + "id integer not null primary key generated always as identity (start with 1, increment by 1)"
		        + ", num int"
		        + ", addr varchar(40))";
		try {
			statement.execute(sqlExpression);
			System.out.println("Created table location");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * This methods initializes all the statements that we use to talk to the
	 * database.
	 */
	private void initializeStatements(Connection connection)
	        throws SQLException {

		/* Creating a prepared statement for inserting locations into the
		 * location table. */
		psInsert = connection.prepareStatement(
		        "insert into location (num, addr) values (?, ?)");
		statements.add(psInsert);

		/* Creating a prepared statement for updating locations in the location
		 * table. */
		psUpdate = connection.prepareStatement(
		        "update location set num=?, addr=? where num=?");
		statements.add(psUpdate);
	}

	private void addLocation(int num, String streetName) throws SQLException {
		psInsert.setInt(1, num);
		psInsert.setString(2, streetName);
		psInsert.executeUpdate();
		System.out.println(String.format("Inserted: %d %s", num,
		        streetName));
	}

	private void updateLocation(int oldNum, int newNum, String streetName)
	        throws SQLException {
		psUpdate.setInt(1, newNum);
		psUpdate.setString(2, "Grand Ave.");
		psUpdate.setInt(3, oldNum);
		psUpdate.executeUpdate();
		System.out.println(String.format("Updated location at %d to %d, %s",
		        oldNum, newNum, streetName));
	}

	private ResultSet getAllLocations() throws SQLException {
		/* We select the rows and verify the results. */
		return statement.executeQuery(
		        "SELECT num, addr FROM location ORDER BY num");
	}

	private void verifyLocations() {

		ResultSet rs = null;
		try {
			rs = getAllLocations();

			int number; // street number retrieved from the database
			boolean failure = false;
			if (!rs.next()) {
				failure = true;
				reportFailure("No rows in ResultSet");
			}

			if ((number = rs.getInt(1)) != 300) {
				failure = true;
				reportFailure("Wrong row returned, expected num=300, got "
				        + number);
			}

			if (!rs.next()) {
				failure = true;
				reportFailure("Too few rows");
			}

			if ((number = rs.getInt(1)) != 1910) {
				failure = true;
				reportFailure("Wrong row returned, expected num=1910, got "
				        + number);
			}

			if (rs.next()) {
				failure = true;
				reportFailure("Too many rows");
			}

			if (!failure) {
				System.out.println("Verified the rows");
			}
		} catch (SQLException e) {
			printSQLException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					printSQLException(e);
				}
			}
		}
	}

	/**
	 * Reports a data verification failure to System.err with the given message.
	 * 
	 * @param message
	 *            A message describing what failed.
	 */
	private void reportFailure(String message) {
		System.err.println("\nData verification failed:");
		System.err.println('\t' + message);
	}

	private void deleteTables() throws SQLException {
		// delete the table
		statement.execute("drop table location");
		System.out.println("Dropped table location");
	}

	private void shutDownDB() {

		try {
			// the shutdown=true attribute shuts down Derby
			DriverManager.getConnection(DB_URL
			        + ";shutdown=true", dbProperties);

		} catch (SQLException se) {
			if (se.getErrorCode() == 45000 && "08006".equals(se
			        .getSQLState())) {
				// we got the expected exception
				System.out.println("Derby shut down normally");
				// Note that for single database shutdown, the expected
				// SQL state is "08006", and the error code is 45000.
			} else {
				// if the error code or SQLState is different, we have
				// an unexpected exception (shutdown failed)
				System.err.println("Derby did not shut down normally");
				printSQLException(se);
			}
		}
	}

	private void disposeStatements() throws SQLException {
		for (Iterator<Statement> it = statements.iterator(); it.hasNext();) {
			Statement st = it.next();
			if (st != null) {
				st.close();
			}
			it.remove();
		}
	}

	private void closeConnection(Connection connection) throws SQLException {
		// Connection
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	/**
	 * Prints details of an SQLException chain to <code>System.err</code>.
	 * Details included are SQL State, Error code, Exception message.
	 * 
	 * @param e
	 *            the SQLException from which to print details.
	 */
	private static void printSQLException(SQLException e)
	{
		// Unwraps the entire exception chain to unveil the real cause of the
		// Exception.
		while (e != null)
		{
			System.err.println("\n----- SQLException -----");
			System.err.println("  SQL State:  " + e.getSQLState());
			System.err.println("  Error Code: " + e.getErrorCode());
			System.err.println("  Message:    " + e.getMessage());
			// for stack traces, refer to derby.log or uncomment this:
			// e.printStackTrace(System.err);
			e = e.getNextException();
		}
	}

}