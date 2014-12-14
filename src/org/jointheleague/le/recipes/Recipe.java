package org.jointheleague.le.recipes;

import java.sql.Time;

public class Recipe {

	private final String name;
	private final int calories;
	private final int id;
	private final String ingredients;
	private final Time preptime;
	private final Time cooktime;
	private final int difficulty;
	private final int rating;
	private final String instructions;
	private final String dietary;

	public Recipe(int id, String name, int calories, String ingredients, Time preptime, Time cooktime, int difficulty, int rating, String instructions, String dietary) {

		this.name = name;
		this.calories = calories;
		this.id = id;
		this.ingredients = ingredients;
		this.preptime = preptime;
		this.cooktime = cooktime;
		this.difficulty = difficulty;
		this.rating = rating;
		this.instructions = instructions;
		this.dietary = dietary;

	}

	public Recipe(int id, Recipe recipe) {
		this.id = id;
		this.name = recipe.getName();
		this.calories = recipe.getCalories();
		this.ingredients = recipe.getIngredients();
		this.preptime = recipe.getPrepTime();
		this.cooktime = recipe.getCookTime();
		this.difficulty = recipe.getDifficulty();
		this.rating = recipe.getRating();
		this.instructions = recipe.getInstructions();
		this.dietary = recipe.getDietary();

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
	sb.append("Id:");
	sb.append(id);
	sb.append("Name: ");
	sb.append(name);
	sb.append("\n\n");
	sb.append("Calories: ");
	sb.append(calories);
	sb.append("\n\n");
	sb.append("Ingredients:");
	sb.append(ingredients);
	sb.append("\n\n");
	sb.append("Prep time: ");
	sb.append(preptime);
	sb.append("\n\n");
	sb.append("Cook time: ");
	sb.append(cooktime);
	sb.append("\n\n");
	sb.append("Difficulty: ");
	sb.append(difficulty);
	sb.append("\n\n");
	sb.append("Rating: ");
	sb.append(rating);
	sb.append("\n\n");
	sb.append("Instructions: ");
	sb.append(instructions);
	sb.append("\n\n");
	sb.append("Dietary");
	sb.append(dietary);
		return sb.toString();

	}

	public int getId() {
		return id;
	}

	public int getCalories() {
		return calories;
	}

	public String getName() {
		return name;
	}

	public String getIngredients() {
		return ingredients;
	}

	public Time getPrepTime() {
		return preptime;
	}

	public Time getCookTime() {
		return cooktime;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public int getRating() {
		return rating;
	}

	public String getInstructions() {
		return instructions;
	}

	public String getDietary() {
		return dietary;
	}
	

}