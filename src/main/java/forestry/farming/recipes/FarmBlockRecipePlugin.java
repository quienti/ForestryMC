/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.recipes;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.config.Constants;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class FarmBlockRecipePlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Constants.MOD_ID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		//        IStackHelper helper = registry.getJeiHelpers().getStackHelper();
		//        Set<FarmBlockRecipeWrapper> recipes = new HashSet<>();
		//        for (FarmBlockRecipe recipe : FarmBlockRecipe.Factory.RECIPES) {
		//            for (EnumFarmBlockTexture tex : EnumFarmBlockTexture.values()) {
		//                recipes.add(new FarmBlockRecipeWrapper(recipe, tex, helper));
		//            }
		//        }
		//        registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
	}
}
