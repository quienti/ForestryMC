package forestry.factory.recipes.jei.carpenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;

public class CarpenterRecipeWrapper extends ForestryRecipeWrapper<ICarpenterRecipe> {
	public CarpenterRecipeWrapper(ICarpenterRecipe recipe) {
		super(recipe);
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		ICarpenterRecipe recipe = getRecipe();
		ShapedRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();

		NonNullList<Ingredient> itemIngredients = recipe.getCraftingGridRecipe().getIngredients();
		List<Ingredient> inputStacks = new ArrayList<>();
		inputStacks.addAll(itemIngredients);

		ingredients.setInputIngredients(inputStacks);

		FluidStack fluidResource = recipe.getFluidResource();
		if (fluidResource != null) {
			ingredients.setInputs(VanillaTypes.FLUID, Collections.singletonList(fluidResource));
		}

		ItemStack recipeOutput = craftingGridRecipe.getRecipeOutput();
		ingredients.setOutput(VanillaTypes.ITEM, recipeOutput);
	}
}
