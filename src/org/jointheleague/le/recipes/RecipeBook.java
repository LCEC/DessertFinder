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
			new RecipeBook().loadRecipes();
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

	private void loadRecipes() throws URISyntaxException {
		URL fileUrl = getClass().getResource("res/recipes.csv");
		File recipeFile = new File(fileUrl.toURI());
		System.out.println(String.format("File %s found",
				recipeFile.getAbsolutePath()));
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(recipeFile));
			String line = null;
			int quoteCount = 0;
			String row = "";
			boolean firstRowRead = false;
			while ((line = reader.readLine()) != null) {
				quoteCount += countChars(line, '"');
				if (!row.isEmpty()) {
					row += "\n";
				}
				row += line;

				if (quoteCount % 2 == 0) {
					if (firstRowRead) {
						parseRow(row);
					}	
					else {
						firstRowRead = true;
					}
					row = "";
					quoteCount = 0;
				}

			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void parseRow(String row) {
		System.out.println("========START========");
		System.out.println(row);
		System.out.println("========END========");
		String cellRegex = "(?:([^,\"\n]*)|\"((?:[^\"]|\"\")*)\")";
		String rowRegex = String.format(
				"%1$s,%1$s,%1$s,%1$s,%1$s,%1$s,%1$s,%1$s,%1$s", cellRegex);
		Pattern rowPattern = Pattern.compile(rowRegex);
		Matcher m = rowPattern.matcher(row);
		String name;
		int calories;
		String ingredients;
		Time preptime;
		Time cooktime;
		int difficulty;
		int rating;
		String instruction;
		String dietary;
		if (m.matches()) {
			name = extractCell(m, 1);
			calories = Integer.parseInt(extractCell(m, 2));
			ingredients = extractCell(m, 3);
			preptime = Time.valueOf(extractCell(m, 4));
			cooktime = Time.valueOf(extractCell(m, 5));
			difficulty = Integer.parseInt(extractCell(m, 6));
			rating = Integer.parseInt(extractCell(m, 7));
			instruction = extractCell(m, 8);
			dietary = extractCell(m, 9);
			System.out.println(ingredients);
		}

	}

	private String extractCell(Matcher m, int cellNumber) {
		String cell = m.group(2 * cellNumber - 1);
		if (cell == null) {
			cell = m.group(2 * cellNumber);
			cell = cell.replace("\"\"", "\"");
		}
		return cell;
	}

	private int countChars(String s, char c) {
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c) {
				count++;
			}
		}
		return count;
	}
}