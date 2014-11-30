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
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataAccessObject implements DataInterface {

	public static final Logger LOGGER = Logger.getLogger(DataAccessObject.class
			.getCanonicalName());
	private static final String DB_URL = "jdbc:derby:"
			+ System.getProperty("user.dir") + "/databases/recipesDB";

	// Some statements
	private Statement statement;
	private PreparedStatement psInsert;
	private PreparedStatement psUpdate;
	private PreparedStatement psSearchByName;
	private PreparedStatement psSearchByCalories;
	private PreparedStatement psSearchByIngredient;
	private PreparedStatement psSearchMultiple;

	private Connection connection = null;

	// List of all statements
	private final List<Statement> statements = new ArrayList<Statement>();

	private Properties dbProperties;

	public boolean open() {

		LOGGER.info("Starting the database : " + DB_URL);
		dbProperties = loadProperties();
		try {
			connection = connect();
			connection.setAutoCommit(true);
			statement = connection.createStatement();
			statements.add(statement);
			createTables();
			initializeStatements();
			return true;
		} catch (SQLException e) {
			logSQLException(e);
			return false;
		}
	}

	public int addRecipe(Recipe recipe) {
		List<Recipe> alreadyindb = searchByName(recipe.getName());
		if (!alreadyindb.isEmpty()) {
			LOGGER.log(Level.WARNING,
					"Recipe with the name " + recipe.getName()
							+ " is already in the database.");
			return 0;
		}
		try {
			psInsert.setString(1, recipe.getName());
			psInsert.setInt(2, recipe.getCalories());
			psInsert.setString(3, recipe.getIngredients());
			psInsert.setTime(4, recipe.getPrepTime());
			psInsert.setTime(5, recipe.getCookTime());
			psInsert.setInt(6, recipe.getDifficulty());
			psInsert.setInt(7, recipe.getRating());
			psInsert.setString(8, recipe.getInstructions());
			psInsert.setString(9, recipe.getDietary());
			psInsert.executeUpdate();
			ResultSet results = psInsert.getGeneratedKeys();
			int id = 0;
			if (results.next()) {
				id = results.getInt(1);
				LOGGER.info("Inserted in recipes: " + new Recipe(id, recipe));
			}
			return id;
		} catch (SQLException e) {
			logSQLException(e);
			return 0;
		}
	}

	public void updateRecipe(int id, Recipe recipe) {
		try {
			psUpdate.setString(1, recipe.getName());
			psUpdate.setInt(2, recipe.getCalories());
			psUpdate.setInt(3, id);
			psUpdate.executeUpdate();
			LOGGER.info("Updated recipes: " + recipe);
		} catch (SQLException e) {
			logSQLException(e);
		}
	}

	public List<Recipe> searchByCalories(int maxCalories) {
		try {
			psSearchByCalories.setInt(1, maxCalories);
			ResultSet rs = psSearchByCalories.executeQuery();
			List<Recipe> result = new ArrayList<Recipe>();
			while (rs.next()) {
				// "INSERT INTO recipes (name, calories, ingredients, preptime, cooktime, difficulty, rating, instructions, dietary) "
				result.add(new Recipe(rs.getInt(1), rs.getString(2), rs
						.getInt(3), rs.getString(4), rs.getTime(5), rs
						.getTime(6), rs.getInt(7), rs.getInt(8), rs
						.getString(9), rs.getString(10)));
			}
			rs.close();
			return result;
		} catch (SQLException e) {
			logSQLException(e);
			return null;
		}
	}

	public List<Recipe> searchByName(String name) {
		try {
			psSearchByName.setString(1, name.toLowerCase());
			ResultSet rs = psSearchByName.executeQuery();
			List<Recipe> result = new ArrayList<Recipe>();
			while (rs.next()) {
				result.add(new Recipe(rs.getInt(1), rs.getString(2), rs
						.getInt(3), rs.getString(4), rs.getTime(5), rs
						.getTime(6), rs.getInt(7), rs.getInt(8), rs
						.getString(9), rs.getString(10)));
			}
			rs.close();
			return result;
		} catch (SQLException e) {
			logSQLException(e);
			return null;
		}
	}

	public List<Recipe> searchByIngredient(String ingredient) {
		try {
			psSearchByIngredient.setString(1, "%"+ingredient.toLowerCase()+"%");
			
			ResultSet rs = psSearchByIngredient.executeQuery();
			List<Recipe> result = new ArrayList<Recipe>();
			while (rs.next()) {
				result.add(new Recipe(rs.getInt(1), rs.getString(2), rs
						.getInt(3), rs.getString(4), rs.getTime(5), rs
						.getTime(6), rs.getInt(7), rs.getInt(8), rs
						.getString(9), rs.getString(10)));
			}
			rs.close();
			return result;
		} catch (SQLException e) {
			logSQLException(e);
			return null;
		}
	}
	@Override
	public List<Recipe> searchMultiple(String name ,int maxCalories, String ingredient) {
		try {
			psSearchMultiple.setString(1, "%"+name.toLowerCase()+"%");
			psSearchMultiple.setInt(2, maxCalories);
			psSearchMultiple.setString(3, "%"+ingredient.toLowerCase()+"%");
			ResultSet rs = psSearchMultiple.executeQuery();
			List<Recipe> result = new ArrayList<Recipe>();
			while (rs.next()) {
				result.add(new Recipe(rs.getInt(1), rs.getString(2), rs
						.getInt(3), rs.getString(4), rs.getTime(5), rs
						.getTime(6), rs.getInt(7), rs.getInt(8), rs
						.getString(9), rs.getString(10)));
			}
			rs.close();
			return result;
		} catch (SQLException e) {
			logSQLException(e);
			return null;
		}
	}

	public void deleteAllData() {
		// delete the table
		try {
			statement.execute("DROP TABLE recipes");
			LOGGER.info("Dropped table recipes.");
		} catch (SQLException e) {
			logSQLException(e);
		}
	}

	public void close() {
		shutDownDB();
		try {
			disposeStatements();
			closeConnection();
		} catch (SQLException e) {
			logSQLException(e);
		}
	}

	private Properties loadProperties() {
		Properties props = new Properties(); // connection properties
		props.put("driver", "org.apache.derby.jdbc.EmbeddedDriver");
		props.put("url", "jdbc:derby:");
		props.put("schema", "APP");
		props.put("derby.system.home", System.getProperty("user.home")
				+ "/databases");
		props.put("database", "recipesDB");
		return props;
	}

	private Connection connect() throws SQLException {
		Connection connection = DriverManager.getConnection(DB_URL
				+ ";create=true", dbProperties);

		LOGGER.info("Connected to database " + DB_URL);
		return connection;
	}

	private boolean createTables() {
		try {
			String sqlExpression = "CREATE TABLE recipes ("
					+ "id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)"
					+ ", name VARCHAR(100)" + ", calories INTEGER"
					+ ", ingredients VARCHAR(2000)" + ", preptime TIME"
					+ ", cooktime TIME" + ", difficulty INTEGER"
					+ ", rating INTEGER" + ", instructions VARCHAR(3000)"
					+ ", dietary VARCHAR(2000)" + ")";

			statement.execute(sqlExpression);
			LOGGER.info("Created table recipes");
			return true;
		} catch (SQLException e) {
			LOGGER.warning("Table not created.");
			return false;
		}
	}

	/**
	 * This methods initializes all the statements that we use to talk to the
	 * database.
	 */
	private void initializeStatements() throws SQLException {

		psInsert = connection
				.prepareStatement(
						"INSERT INTO recipes (name, calories, ingredients, preptime, cooktime, difficulty, rating, instructions, dietary) "
								+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
		statements.add(psInsert);

		psUpdate = connection.prepareStatement(
				"UPDATE recipes SET name=?, calories=? WHERE id=?",
				Statement.RETURN_GENERATED_KEYS);
		statements.add(psUpdate);

		psSearchByCalories = connection
				.prepareStatement("SELECT * FROM recipes WHERE calories <= ? ORDER BY name");
		statements.add(psSearchByCalories);

		psSearchByName = connection
				.prepareStatement("SELECT * FROM recipes WHERE LOWER(name) LIKE ? ORDER BY name");
		statements.add(psSearchByName);

		psSearchByIngredient = connection
				.prepareStatement("SELECT * FROM recipes WHERE LOWER(ingredients) LIKE ? ORDER BY name");
		statements.add(psSearchByIngredient);
		psSearchMultiple = connection.prepareStatement("SELECT * FROM recipes WHERE LOWER(name) LIKE ?"
				+ " AND calories <= ? "
				+ "AND LOWER(ingredients) LIKE ? ORDER BY name");
	}

	private void shutDownDB() {

		try {
			// the shutdown=true attribute shuts down Derby
			DriverManager
					.getConnection(DB_URL + ";shutdown=true", dbProperties);

		} catch (SQLException se) {
			if (se.getErrorCode() == 45000 && "08006".equals(se.getSQLState())) {
				// we got the expected exception
				LOGGER.info("Database shut down normally");
				// Note that for single database shutdown, the expected
				// SQL state is "08006", and the error code is 45000.
			} else {
				// if the error code or SQLState is different, we have
				// an unexpected exception (shutdown failed)
				LOGGER.warning("Database did not shut down normally");
				logSQLException(se);
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

	private void closeConnection() throws SQLException {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	/**
	 * Logs details of an SQLException chain. Details included are SQL State,
	 * Error code, Exception message.
	 * 
	 * @param e
	 *            the SQLException from which to print details.
	 */
	private static void logSQLException(SQLException e) {
		// Unwraps the entire exception chain to unveil the real cause of the
		// Exception.
		while (e != null) {
			LOGGER.severe("\n----- SQLException -----\n" + "  SQL State:  "
					+ e.getSQLState() + "\n" + "  Error Code: "
					+ e.getErrorCode() + "\n" + "  Message:    "
					+ e.getMessage());
			// for stack traces, refer to derby.log or uncomment this:
			// e.printStackTrace(System.err);
			e = e.getNextException();
		}
	}

	

}