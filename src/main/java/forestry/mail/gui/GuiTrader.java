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
package forestry.mail.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.render.ColourProperties;
import forestry.mail.tiles.TileTrader;

public class GuiTrader extends GuiForestry<ContainerTrader> {
	private final TileTrader tile;

	public GuiTrader(ContainerTrader container, PlayerInventory inv, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "mailtrader2.png", container, inv, title);
		this.tile = container.getTile();
		this.xSize = 226;
		this.ySize = 220;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack transform, int mouseX, int mouseY) {
		this.minecraft.fontRenderer.func_243248_b(transform, tile.getDisplayName(), textLayout.getCenteredOffset(tile.getDisplayName()), 6, ColourProperties.INSTANCE.get("gui.mail.text"));

		ITextComponent receive = new TranslationTextComponent("for.gui.mail.receive");
		this.minecraft.fontRenderer.func_243248_b(transform, receive, textLayout.getCenteredOffset(receive, 70) + 51, 45, ColourProperties.INSTANCE.get("gui.mail.text"));

		ITextComponent send = new TranslationTextComponent("for.gui.mail.send");
		this.minecraft.fontRenderer.func_243248_b(transform, send, textLayout.getCenteredOffset(send, 70) + 51, 99, ColourProperties.INSTANCE.get("gui.mail.text"));

		super.drawGuiContainerForegroundLayer(transform, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseX, mouseY);

		this.minecraft.fontRenderer.drawString(transform, container.getAddress().getName(), guiLeft + 19, guiTop + 22, ColourProperties.INSTANCE.get("gui.mail.text"));
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger("trade.station");
		addOwnerLedger(tile);
	}
}
