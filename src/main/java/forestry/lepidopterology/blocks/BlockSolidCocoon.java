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
package forestry.lepidopterology.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.core.items.ItemScoop;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.lepidopterology.genetics.alleles.AlleleButterflyCocoon;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;
import forestry.lepidopterology.tiles.TileCocoon;

public class BlockSolidCocoon extends Block {
	private static final PropertyCocoon COCOON = AlleleButterflyCocoon.COCOON;

	public BlockSolidCocoon() {
		super(Block.Properties.create(MaterialCocoon.INSTANCE).harvestTool(ItemScoop.SCOOP).harvestLevel(0)
				.hardnessAndResistance(0.5F).tickRandomly().sound(SoundType.GROUND)
				.setOpaque((state, reader, pos) -> false));
		setDefaultState(this.getStateContainer().getBaseState().with(COCOON, ButterflyAlleles.cocoonDefault)
				.with(AlleleButterflyCocoon.AGE, 0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(COCOON, AlleleButterflyCocoon.AGE);
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
		if (cocoon != null) {
			state = state.with(COCOON, cocoon.getCaterpillar().getGenome().getActiveAllele(ButterflyChromosomes.COCOON))
					.with(AlleleButterflyCocoon.AGE, cocoon.getAge());
		}

		return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
	}

	//	@OnlyIn(Dist.CLIENT)
	//	@Override
	//	public void registerStateMapper() {
	//		ModelLoader.setCustomStateMapper(this, new CocoonStateMapper());
	//	}
	//
	//	@OnlyIn(Dist.CLIENT)
	//	@Override
	//	public void registerModel(Item item, IModelManager manager) {
	//		// To delete the error message
	//		manager.registerItemModel(item, 0, "cocoon_late");
	//	}

	//	@Override
	//	public boolean isFullBlock(BlockState state) {
	//		return false;
	//	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BlockCocoon.BOUNDING_BOX;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileCocoon(true);
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		if (canHarvestBlock(state, world, pos, player)) {
			TileUtil.actOnTile(world, pos, TileCocoon.class, cocoon -> {
				NonNullList<ItemStack> drops = cocoon.getCocoonDrops();
				for (ItemStack stack : drops) {
					ItemStackUtil.dropItemStackAsEntity(stack, world, pos);
				}
			});
		}

		return world.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

}
