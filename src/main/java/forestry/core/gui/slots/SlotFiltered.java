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
package forestry.core.gui.slots;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.config.Constants;
import forestry.core.tiles.IFilterSlotDelegate;

/**
 * Slot which only takes specific items, specified by the IFilterSlotDelegate.
 */
public class SlotFiltered extends SlotWatched implements ISlotTextured {
	private final IFilterSlotDelegate filterSlotDelegate;
	@Nullable
	private ResourceLocation backgroundTexture = null;
	private ResourceLocation blockedTexture = new ResourceLocation(Constants.MOD_ID, "slots/blocked");

	public <T extends IInventory & IFilterSlotDelegate> SlotFiltered(T inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		this.filterSlotDelegate = inventory;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		int slotIndex = getSlotIndex();
		return !filterSlotDelegate.isLocked(slotIndex) && (itemstack.isEmpty() || filterSlotDelegate.canSlotAccept(slotIndex, itemstack));
	}

	@Nullable
	@Override
	@OnlyIn(Dist.CLIENT)
	public Pair<ResourceLocation, ResourceLocation> getBackground() {
		ItemStack stack = getStack();
		if (!isItemValid(stack)) {
			return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, blockedTexture);
		} else if (backgroundTexture != null) {
			return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, backgroundTexture);
		} else {
			return null;
		}
	}

	public SlotFiltered setBlockedTexture(String ident) {
		blockedTexture = new ResourceLocation(Constants.MOD_ID, ident);
		return this;
	}

	public SlotFiltered setBackgroundTexture(String ident) {
		backgroundTexture = new ResourceLocation(Constants.MOD_ID, ident);
		return this;
	}
}
