package forestry.book.gui.elements;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.book.data.TextData;
import forestry.core.gui.elements.GuiElement;

//TODO Move to component system
@OnlyIn(Dist.CLIENT)
public class TextDataElement extends GuiElement {

	private final List<TextData> textElements = new ArrayList<>();

	public TextDataElement(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
	}

	@Override
	public int getHeight() {
		if (height >= 0) {
			return height;
		}
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		boolean unicode = fontRenderer.getBidiFlag();
		//fontRenderer.setBidiFlag(true);
		boolean lastEmpty = false;
		for (TextData data : textElements) {
			if (data.text.equals("\n")) {
				if (lastEmpty) {
					height += fontRenderer.FONT_HEIGHT;
				}
				lastEmpty = true;
				continue;
			}
			lastEmpty = false;

			if (data.paragraph) {
				height += fontRenderer.FONT_HEIGHT * 1.6D;
			}

			String modifiers = "";

			modifiers += TextFormatting.getValueByName(data.color);

			if (data.bold) {
				modifiers += TextFormatting.BOLD;
			}
			if (data.italic) {
				modifiers += TextFormatting.ITALIC;
			}
			if (data.underlined) {
				modifiers += TextFormatting.UNDERLINE;
			}
			if (data.strikethrough) {
				modifiers += TextFormatting.STRIKETHROUGH;
			}
			if (data.obfuscated) {
				modifiers += TextFormatting.OBFUSCATED;
			}
			height += fontRenderer.getWordWrappedHeight(modifiers + data.text, width);
		}
		//fontRenderer.setBidiFlag(unicode);
		return height;
	}

	@Override
	public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		boolean unicode = fontRenderer.getBidiFlag();
		//fontRenderer.setBidiFlag(true);
		int x = 0;
		int y = 0;
		for (TextData data : textElements) {
			if (data.text.equals("\n")) {
				x = 0;
				y += fontRenderer.FONT_HEIGHT;
				continue;
			}

			if (data.paragraph) {
				x = 0;
				y += fontRenderer.FONT_HEIGHT * 1.6D;
			}

			String text = getFormattedString(data);
			List<IReorderingProcessor> split = fontRenderer.trimStringToWidth(new StringTextComponent(text), width);
			for (int i = 0; i < split.size(); i++) {
				IReorderingProcessor s = split.get(i);
				int textLength;
				if (data.shadow) {
					textLength = fontRenderer.func_238407_a_(transform, s, x, y, 0);
				} else {
					textLength = fontRenderer.func_238422_b_(transform, s, x, y, 0);
				}

				if (i == split.size() - 1) {
					x += textLength;
				} else {
					y += fontRenderer.FONT_HEIGHT;
				}
			}
		}
		//fontRenderer.setBidiFlag(unicode);
	}

	private String getFormattedString(TextData data) {
		StringBuilder modifiers = new StringBuilder();

		TextFormatting color = TextFormatting.getValueByName(data.color);
		if (color != null) {
			modifiers.append(color);
		}

		if (data.bold) {
			modifiers.append(TextFormatting.BOLD);
		}
		if (data.italic) {
			modifiers.append(TextFormatting.ITALIC);
		}
		if (data.underlined) {
			modifiers.append(TextFormatting.UNDERLINE);
		}
		if (data.strikethrough) {
			modifiers.append(TextFormatting.STRIKETHROUGH);
		}
		if (data.obfuscated) {
			modifiers.append(TextFormatting.OBFUSCATED);
		}

		modifiers.append(data.text);
		return modifiers.toString();
	}

	public void addData(TextData textData) {
		textElements.add(textData);
		setHeight(-1);
	}
}
