 package org.jointheleague.le.recipes;

import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecipeBook {

	private DataInterface db = new DataAccessObjectAlt();
	private static final Logger LOGGER = Logger
			.getLogger(DataAccessObject.class.getCanonicalName());

	public static void main(String[] args) {
			new RecipeBook().run();
	}

	public void run() {
		System.out.println("Opening the database ...");
		db.open();
		
		System.out.println("Loading CSV file ...");
		try {
			new CsvLoader(db).loadRecipes();
		} catch (URISyntaxException e) {
			LOGGER.log(Level.SEVERE, "Loading CSV file failed.");
		}
		
		
		
		System.out.println("Adding some recipes ...");

		System.out.println("Printing out all recipes in the db...");
		for (Recipe r : db.searchByCalories(9000)) {
			System.out.println(r);
		}
		System.out.println("Updating a recipe...");

		System.out
				.println("Printing all recipes that have 220 calories or less.");
		for (Recipe r : db.searchByCalories(220)) {
			System.out.println(r);
		}
		System.out.println("Adding a recipe.");

		System.out
				.println("Searching for all recipes that contain \"macaro\" in their name");
		List<Recipe> result = db.searchByName("%macaro%");
		for (Recipe r : result) {
			System.out.println(r);
		}
		System.out.println("Deleting all data...");
		db.deleteAllData();
		System.out.println("Closing the database...");
		db.close();
	}
}