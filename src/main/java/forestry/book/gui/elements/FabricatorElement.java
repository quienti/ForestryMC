package forestry.book.gui.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.CompoundIngredient;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.IngredientElement;
import forestry.core.gui.elements.TankElement;
import forestry.core.utils.ClientUtils;

@OnlyIn(Dist.CLIENT)
public class FabricatorElement extends SelectionElement<IFabricatorRecipe> {
	private static final ResourceLocation BOOK_CRAFTING_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "almanac/crafting.png");
	private static final Drawable FABRICATOR_BACKGROUND = new Drawable(BOOK_CRAFTING_TEXTURE, 0, 60, 108, 56);
	private static final Drawable FABRICATOR_TANK_OVERLAY = new Drawable(BOOK_CRAFTING_TEXTURE, 109, 61, 16, 16);

	public FabricatorElement(int xPos, int yPos, ItemStack stack) {
		this(0, 0, new ItemStack[]{stack});
	}

	public FabricatorElement(int xPos, int yPos, ItemStack[] stacks) {
		this(0, 0, Arrays.stream(stacks).map(x -> RecipeManagers.fabricatorManager.getRecipes(ClientUtils.getRecipeManager(), x)).flatMap(Collection::stream).toArray(IFabricatorRecipe[]::new));
	}

	public FabricatorElement(int xPos, int yPos, IFabricatorRecipe[] recipes) {
		super(xPos, yPos, 108, 58, recipes, 2);

		drawable(0, 2, FABRICATOR_BACKGROUND);
		add(selectedElement);
		setIndex(0);
	}

	@Override
	protected void onIndexUpdate(int index, IFabricatorRecipe recipe) {
		selectedElement.add(new TankElement(1, 33, null, recipe.getLiquid(), 2000, FABRICATOR_TANK_OVERLAY, 16, 16));
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int ingredientIndex = y * 3 + x;
				if (ingredientIndex >= ingredients.size()) {
					continue;
				}

				Ingredient ingredient = ingredients.get(ingredientIndex);
				selectedElement.add(new IngredientElement(21 + x * 19, 1 + y * 19, ingredient));
			}
		}

		Ingredient plan = recipe.getPlan();
		if (!plan.hasNoMatchingItems()) {
			selectedElement.item(91, 1, plan.getMatchingStacks()[0]);
		}

		selectedElement.item(91, 39, recipe.getCraftingGridRecipe().getRecipeOutput());
		NonNullList<Ingredient> smeltingInput = NonNullList.create();
		Fluid recipeFluid = recipe.getLiquid().getFluid();
		for (IFabricatorSmeltingRecipe s : getSmeltingInputs().get(recipeFluid)) {
			smeltingInput.add(s.getResource());
		}

		if (!smeltingInput.isEmpty()) {
			selectedElement.add(new IngredientElement(1, 6, new CompoundIngredient(smeltingInput) {
			}));
		}
	}

	private static Map<Fluid, List<IFabricatorSmeltingRecipe>> getSmeltingInputs() {
		RecipeManager recipeManager = ClientUtils.getRecipeManager();
		Map<Fluid, List<IFabricatorSmeltingRecipe>> smeltingInputs = new HashMap<>();
		for (IFabricatorSmeltingRecipe smelting : RecipeManagers.fabricatorSmeltingManager.getRecipes(recipeManager)) {
			Fluid fluid = smelting.getProduct().getFluid();
			if (!smeltingInputs.containsKey(fluid)) {
				smeltingInputs.put(fluid, new ArrayList<>());
			}

			smeltingInputs.get(fluid).add(smelting);
		}

		return smeltingInputs;
	}
}
