package forestry.factory.recipes.jei.squeezer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.crafting.RecipeManager;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;

public class SqueezerRecipeMaker {
	public static List<SqueezerRecipeWrapper> getSqueezerRecipes(RecipeManager manager) {
		List<SqueezerRecipeWrapper> recipes = new ArrayList<>();
		for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.getRecipes(manager)) {
			recipes.add(new SqueezerRecipeWrapper(recipe));
		}

		return recipes;
	}
}
