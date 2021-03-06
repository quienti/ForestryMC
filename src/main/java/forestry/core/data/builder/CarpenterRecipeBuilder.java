package forestry.core.data.builder;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class CarpenterRecipeBuilder {

	private int packagingTime;
	@Nullable
	private FluidStack liquid;
	private Ingredient box;
	private ShapedRecipeBuilder.Result recipe;

	public CarpenterRecipeBuilder setPackagingTime(int packagingTime) {
		this.packagingTime = packagingTime;
		return this;
	}

	public CarpenterRecipeBuilder setLiquid(@Nullable FluidStack liquid) {
		this.liquid = liquid;
		return this;
	}

	public CarpenterRecipeBuilder setBox(Ingredient box) {
		this.box = box;
		return this;
	}

	public CarpenterRecipeBuilder recipe(Consumer<Consumer<IFinishedRecipe>> consumer) {
		Holder<IFinishedRecipe> holder = new Holder<>();
		consumer.accept(holder::set);
		recipe = (ShapedRecipeBuilder.Result) holder.get();
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer) {
		build(consumer, recipe.getID());
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, packagingTime, liquid, box, recipe));
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final int packagingTime;
		@Nullable
		private final FluidStack liquid;
		private final Ingredient box;
		private final ShapedRecipeBuilder.Result recipe;

		public Result(ResourceLocation id, int packagingTime, @Nullable FluidStack liquid, Ingredient box, ShapedRecipeBuilder.Result recipe) {
			this.id = id;
			this.packagingTime = packagingTime;
			this.liquid = liquid;
			this.box = box;
			this.recipe = recipe;
		}

		@Override
		public void serialize(JsonObject json) {
			json.addProperty("time", packagingTime);
			if (liquid != null) {
				json.add("liquid", RecipeSerializers.serializeFluid(liquid));
			}

			json.add("box", box.serialize());
			json.add("recipe", recipe.getRecipeJson());
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return ICarpenterRecipe.Companion.SERIALIZER;
		}

		@Override
		public JsonObject getAdvancementJson() {
			return null;
		}

		@Override
		public ResourceLocation getAdvancementID() {
			return null;
		}
	}
}
