/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;

import net.minecraftforge.fluids.FluidStack;

/**
 * Provides an interface to the recipe manager of the hygroregulator and habitatformer.
 * <p>
 * The manager is initialized at the beginning of Forestry's BaseMod.load()
 * cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be
 * null even if your mod loads before Forestry.
 * <p>
 * Accessible via {@link RecipeManagers}
 *
 * @author Nedelosk
 */
public interface IHygroregulatorManager extends ICraftingProvider<IHygroregulatorRecipe> {
	@Nullable
	IHygroregulatorRecipe findMatchingRecipe(RecipeManager manager, FluidStack liquid);

	Set<Fluid> getRecipeFluids(RecipeManager manager);
}
