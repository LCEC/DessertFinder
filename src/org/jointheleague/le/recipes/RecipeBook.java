 package org.jointheleague.le.recipes;

import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecipeBook {

	private DataInterface db = new DataAccessObject();
	private static final Logger LOGGER = Logger
			.getLogger(DataAccessObject.class.getCanonicalName());

	public static void main(String[] args) {
			new RecipeBook().run();
	}

	public void run() {
		LOGGER.log(Level.INFO,"Opening the database ...");
		db.open();
		
		LOGGER.log(Level.INFO,"Loading CSV file ...");
		try {
			new CsvLoader(db).loadRecipes();
		} catch (URISyntaxException e) {
			LOGGER.log(Level.SEVERE, "Loading CSV file failed.");
		}
		
		LOGGER.log(Level.INFO,"Searching for all recipes that contain \"macaro\" in their name");
		List<Recipe> result = db.searchByIngredient("sour cream");
		for (Recipe r : result) {
			System.out.println(r);
		}
		LOGGER.log(Level.INFO,"Deleting all data...");
		//db.deleteAllData();
		LOGGER.log(Level.INFO,"Closing the database...");
		db.close();
	}
}