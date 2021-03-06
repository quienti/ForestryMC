package forestry.factory.recipes.jei.moistener;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.crafting.RecipeManager;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.RecipeManagers;

public class MoistenerRecipeMaker {
	public static List<MoistenerRecipeWrapper> getMoistenerRecipes(RecipeManager manager) {
		List<MoistenerRecipeWrapper> recipes = new ArrayList<>();
		for (IMoistenerRecipe recipe : RecipeManagers.moistenerManager.getRecipes(manager)) {
			for (MoistenerFuel fuel : FuelManager.moistenerResource.values()) {
				recipes.add(new MoistenerRecipeWrapper(recipe, fuel));
			}
		}

		return recipes;
	}

}
