package forestry.core.gui.elements;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.gatgets.DatabaseMode;
import forestry.core.gui.elements.layouts.PaneLayout;
import forestry.core.gui.elements.layouts.VerticalLayout;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import forestry.core.gui.elements.lib.IDatabaseElement;
import forestry.core.gui.elements.lib.IElementGroup;
import forestry.core.gui.elements.lib.IElementLayout;
import forestry.core.gui.elements.lib.IGuiElement;

public class DatabaseElement extends VerticalLayout implements IDatabaseElement {
	private DatabaseMode mode = DatabaseMode.ACTIVE;
	@Nullable
	private IIndividual individual;
	private int secondColumn;
	private int thirdColumn = 0;

	public DatabaseElement(int width) {
		super(0, 0, width);
		this.secondColumn = width / 2;
	}

	public void addLine(ITextComponent firstText, ITextComponent secondText, ITextComponent thirdText, boolean secondDominant, boolean thirdDominant) {

	}

	@Override
	public final void addLine(ITextComponent chromosomeName, IChromosomeType chromosome) {
		addLine(chromosomeName, (allele, b) -> allele.getDisplayName(), chromosome);
	}

	@Override
	public <A extends IAllele> void addLine(ITextComponent chromosomeName, BiFunction<A, Boolean, ITextComponent> toText, IChromosomeType chromosome) {
		addAlleleRow(chromosomeName, toText, chromosome, null);
	}

	@Override
	public <A extends IAllele> void addLine(ITextComponent chromosomeName, BiFunction<A, Boolean, ITextComponent> toText, IChromosomeType chromosome, boolean dominant) {
		addAlleleRow(chromosomeName, toText, chromosome, dominant);
	}

	@Override
	public void addLine(ITextComponent leftText, Function<Boolean, ITextComponent> toText, IChromosomeType chromosome) {
		IGenome genome = getGenome();
		IAllele activeAllele = genome.getActiveAllele(chromosome);
		IAllele inactiveAllele = genome.getInactiveAllele(chromosome);
		if (mode == DatabaseMode.BOTH) {
			addLine(leftText, toText.apply(true), toText.apply(false), activeAllele.isDominant(), inactiveAllele.isDominant());
		} else {
			boolean active = mode == DatabaseMode.ACTIVE;
			IAllele allele = active ? activeAllele : inactiveAllele;
			addLine(leftText, toText.apply(active), allele.isDominant());
		}
	}

	@Override
	public void addLine(ITextComponent firstText, ITextComponent secondText, Style firstStyle, Style secondStyle) {
		IElementLayout first = addSplitText(width, firstText, firstStyle);
		IElementLayout second = addSplitText(width, secondText, secondStyle);
		addLine(first, second);
	}

	@Override
	public void addLine(ITextComponent firstText, ITextComponent secondText, boolean dominant) {
		addLine(firstText, secondText, GuiElementFactory.INSTANCE.guiStyle, GuiElementFactory.INSTANCE.getStateStyle(dominant));
	}

	@Override
	public void addLine(ITextComponent leftText, Function<Boolean, ITextComponent> toText, boolean dominant) {
		if (mode == DatabaseMode.BOTH) {
			addLine(leftText, toText.apply(true), toText.apply(false), dominant, dominant);
		} else {
			addLine(leftText, toText.apply(mode == DatabaseMode.ACTIVE), dominant);
		}
	}

	@Override
	public void addFertilityLine(ITextComponent chromosomeName, IChromosomeType chromosome, int texOffset) {
		IGenome genome = getGenome();
		IAllele activeAllele = genome.getActiveAllele(chromosome);
		IAllele inactiveAllele = genome.getInactiveAllele(chromosome);
		if (mode == DatabaseMode.BOTH) {
			if (!(activeAllele instanceof IAlleleValue) || !(inactiveAllele instanceof IAlleleValue)) {
				return;
			}
			addLine(chromosomeName, GuiElementFactory.INSTANCE.createFertilityInfo((IAlleleValue<Integer>) activeAllele, texOffset), GuiElementFactory.INSTANCE.createFertilityInfo((IAlleleValue<Integer>) inactiveAllele, texOffset));
		} else {
			boolean active = mode == DatabaseMode.ACTIVE;
			IAllele allele = active ? activeAllele : inactiveAllele;
			if (!(allele instanceof IAlleleValue)) {
				return;
			}
			addLine(chromosomeName, GuiElementFactory.INSTANCE.createFertilityInfo((IAlleleValue<Integer>) allele, texOffset));
		}
	}

	/*@Override
	public void addRow(String firstText, String secondText, String thirdText, IIndividual individual, IChromosomeType chromosome) {
		addRow(firstText, secondText, thirdText, GuiElementFactory.GUI_STYLE,
			GuiElementFactory.INSTANCE.getStateStyle(individual.getGenome().getActiveAllele(chromosome).isDominant()),
			GuiElementFactory.INSTANCE.getStateStyle(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
	}*/

	@Override
	public void addToleranceLine(IChromosomeType chromosome) {
		IAllele allele = getGenome().getActiveAllele(chromosome);
		if (!(allele instanceof IAlleleValue)) {
			return;
		}

		addLine(new StringTextComponent(" ").append(new TranslationTextComponent("for.gui.tolerance")), GuiElementFactory.INSTANCE.createToleranceInfo((IAlleleValue<EnumTolerance>) allele));
	}

	@Override
	public void addMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker) {
		IGuiElement element = GuiElementFactory.INSTANCE.createMutation(x, y, width, height, mutation, species, breedingTracker);
		if (element == null) {
			return;
		}
		add(element);
	}

	@Override
	public void addMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker) {
		IGuiElement element = GuiElementFactory.INSTANCE.createMutationResultant(x, y, width, height, mutation, breedingTracker);
		if (element == null) {
			return;
		}
		add(element);
	}

	@Override
	public void addSpeciesLine(ITextComponent firstText, @Nullable ITextComponent secondText, IChromosomeType chromosome) {
		/*IAlleleSpecies primary = individual.getGenome().getPrimary();
		IAlleleSpecies secondary = individual.getGenome().getSecondary();

		textLayout.drawLine(text0, textLayout.column0);
		int columnwidth = textLayout.column2 - textLayout.column1 - 2;

		Map<String, ItemStack> iconStacks = chromosome.getSpeciesRoot().getAlyzerPlugin().getIconStacks();

		GuiUtil.drawItemStack(this, iconStacks.get(primary.getUID()), guiLeft + textLayout.column1 + columnwidth - 20, guiTop + 10);
		GuiUtil.drawItemStack(this, iconStacks.get(secondary.getUID()), guiLeft + textLayout.column2 + columnwidth - 20, guiTop + 10);

		String primaryName = customPrimaryName == null ? primary.getAlleleName() : customPrimaryName;
		String secondaryName = customSecondaryName == null ? secondary.getAlleleName() : customSecondaryName;

		drawSplitLine(primaryName, textLayout.column1, columnwidth, individual, chromosome, false);
		drawSplitLine(secondaryName, textLayout.column2, columnwidth, individual, chromosome, true);

		textLayout.newLine();*/
	}

	@Override
	public void init(DatabaseMode mode, IIndividual individual, int secondColumn, int thirdColumn) {
		this.mode = mode;
		this.individual = individual;
		this.secondColumn = secondColumn;
		this.thirdColumn = thirdColumn;
	}

	@Nullable
	public IIndividual getIndividual() {
		return individual;
	}

	public IGenome getGenome() {
		Preconditions.checkNotNull(individual, "Database Element has not been initialised.");
		return individual.getGenome();
	}

	private IElementLayout addSplitText(int width, ITextComponent text, Style style) {
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		IElementLayout vertical = new VerticalLayout(width);
		for (ITextProperties splitText : fontRenderer.getCharacterManager().func_238362_b_(text, 70, Style.EMPTY)) {
			vertical.label(new StringTextComponent(splitText.getString())).setStyle(style);
		}

		return vertical;
	}

	private void addLine(ITextComponent chromosomeName, IGuiElement right) {
		int center = width / 2;
		IGuiElement first = addSplitText(center, chromosomeName, GuiElementFactory.INSTANCE.guiStyle);
		addLine(first, right);
	}

	private void addLine(ITextComponent chromosomeName, IGuiElement second, IGuiElement third) {
		int center = width / 2;
		IGuiElement first = addSplitText(center, chromosomeName, GuiElementFactory.INSTANCE.guiStyle);
		addLine(first, second, third);
	}

	private void addLine(IGuiElement first, IGuiElement second, IGuiElement third) {
		IElementGroup panel = new PaneLayout(width, 0);
		first.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		second.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		third.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		panel.add(first);
		panel.add(second);
		panel.add(third);
		second.setXPosition(secondColumn);
		third.setXPosition(thirdColumn);
		add(panel);
	}

	private void addLine(IGuiElement first, IGuiElement second) {
		IElementGroup panel = new PaneLayout(width, 0);
		first.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		second.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		panel.add(first);
		panel.add(second);
		second.setXPosition(secondColumn);
		add(panel);
	}

	@SuppressWarnings("unchecked")
	private <A extends IAllele> void addAlleleRow(ITextComponent chromosomeName, BiFunction<A, Boolean, ITextComponent> toString, IChromosomeType chromosome, @Nullable Boolean dominant) {
		IGenome genome = getGenome();
		A activeAllele = (A) genome.getActiveAllele(chromosome);
		A inactiveAllele = (A) genome.getInactiveAllele(chromosome);
		if (mode == DatabaseMode.BOTH) {
			addLine(chromosomeName, toString.apply(activeAllele, true), toString.apply(inactiveAllele, false), dominant != null ? dominant : activeAllele.isDominant(), dominant != null ? dominant : inactiveAllele.isDominant());
		} else {
			boolean active = mode == DatabaseMode.ACTIVE;
			A allele = active ? activeAllele : inactiveAllele;
			addLine(chromosomeName, toString.apply(allele, active), dominant != null ? dominant : allele.isDominant());
		}
	}
}
