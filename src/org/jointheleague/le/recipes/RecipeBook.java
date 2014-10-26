package org.jointheleague.le.recipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Time;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeBook {

	private DataInterface db = new DataAccessObjectAlt();

	public static void main(String[] args) {
		try {
			new CsvLoader().loadRecipes();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("Opening the database ...");
		db.open();
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