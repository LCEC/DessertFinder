package org.jointheleague.le.recipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvLoader {
	private static final String CELL_REGEX = "(?:([^,\"\n]*+)|\"((?:[^\"]|\"\")*+)\")";
	private static final String ROW_REGEX = String.format(
			"%1$s,%1$s,%1$s,%1$s,%1$s,%1$s,%1$s,%1$s,%1$s", CELL_REGEX);
	private static final Pattern ROW_PATTERN = Pattern.compile(ROW_REGEX);
	
	public static void main(String[] args) {
		System.out.println(CELL_REGEX);
		System.out.println(ROW_REGEX);
	}

	private DataInterface db;

	public CsvLoader(DataInterface db) {
		this.db = db;
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

	private String extractCell(Matcher m, int cellNumber) {
		String cell = m.group(2 * cellNumber - 1);
		if (cell == null) {
			cell = m.group(2 * cellNumber);
			cell = cell.replace("\"\"", "\"");
		}
		return cell;
	}

	public void loadRecipes() throws URISyntaxException {
		URL fileUrl = getClass().getResource("res/recipes.csv");
		File recipeFile = new File(fileUrl.toURI());
		DataAccessObject.LOGGER.log(Level.SEVERE,"File not found.");
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
		Matcher m = ROW_PATTERN.matcher(row);
		if (m.matches()) {
			String name = extractCell(m, 1);
			int calories = Integer.parseInt(extractCell(m, 2));
			String ingredients = extractCell(m, 3);
			Time preptime = Time.valueOf(extractCell(m, 4));
			Time cooktime = Time.valueOf(extractCell(m, 5));
			int difficulty = Integer.parseInt(extractCell(m, 6));
			int rating = Integer.parseInt(extractCell(m, 7));
			String instructions = extractCell(m, 8);
			String dietary = extractCell(m, 9);
			db.addRecipe(new Recipe(0, name, calories, ingredients, preptime, cooktime, difficulty,
					rating, instructions, dietary));
		}

	}

}