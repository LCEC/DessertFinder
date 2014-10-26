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


	public String toString() {
		// return "[ id = "+ getId() + ", name = " +getName() + ", calories = "+
		// getCalories() + " ]" ;
		return String.format("[ id = %d, name = %s, calories = %d ]", getId(),
				getName(), getCalories());

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
		// TODO Auto-generated method stub
		return null;
	}

	public Time getPrepTime() {
		// TODO Auto-generated method stub
		return preptime;
	}

	private Time getCookTime() {
		// TODO Auto-generated method stub
		return cooktime;
	}

	public int getDifficulty() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getRating() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDietary() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Time StringtoTime(String timeString){
		return Time.valueOf(timeString);		
	}

}