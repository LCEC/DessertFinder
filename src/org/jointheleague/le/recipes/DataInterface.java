package org.jointheleague.le.recipes;

import java.util.List;

public interface DataInterface {
	public boolean open();
	public int addRecipe(Recipe recipe);
	public void updateRecipe(int id, Recipe recipe);
	public List<Recipe> searchByCalories(int maxCalories);
	public List<Recipe> searchByName(String name);
	public void deleteAllData();
	public void close();
}
