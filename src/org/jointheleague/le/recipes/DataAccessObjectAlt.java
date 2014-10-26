package org.jointheleague.le.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataAccessObjectAlt implements DataInterface{
    private List<Recipe> recipes = new ArrayList<Recipe>();
    private int nextId = 1;
    @Override
	public boolean open() {
		
		return true;
	}

	@Override
	public int addRecipe(Recipe recipe) {
		Recipe r = new Recipe(nextId++, recipe);
		
		recipes.add(r);
		
		return 0;
	}

	@Override
	public void updateRecipe(int id, Recipe recipe) {
		boolean found = false;
		for (Iterator<Recipe> it = recipes.iterator(); it.hasNext()&&!found;) {
			Recipe r = it.next();
			if(r.getId() == id){
				it.remove();
				found = true;
			}
		}
		if(found){
			Recipe r = new Recipe(id, recipe);
			recipes.add(r);
		}
		
	}

	@Override
	public List<Recipe> searchByCalories(int maxCalories) {
		List<Recipe> result = new ArrayList<Recipe>();
		for (Recipe r : recipes) {
			if (r.getCalories()<=maxCalories) {
				result.add(r);
			}
		}
		return result;
	}

	@Override
	public List<Recipe> searchByName(String name) {
		List<Recipe> result = new ArrayList<Recipe>();
		name = name.replaceAll("%", ".*").toLowerCase();
		for (Recipe r : recipes) {
			if (r.getName().toLowerCase().matches(name)){
				result.add(r);
			}
		}
		return result;
	}

	@Override
	public void deleteAllData() {
		recipes.clear();
		
	}

	@Override
	public void close() {
		
		
	}

}
