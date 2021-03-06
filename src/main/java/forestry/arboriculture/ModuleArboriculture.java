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
package forestry.arboriculture;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.feature.Feature;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.Forestry;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IArmorNaturalist;
import forestry.api.modules.ForestryModule;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.arboriculture.capabilities.ArmorNaturalist;
import forestry.arboriculture.commands.CommandTree;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.ArboricultureFeatures;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeFactory;
import forestry.arboriculture.genetics.TreeMutationFactory;
import forestry.arboriculture.network.PacketRegistryArboriculture;
import forestry.arboriculture.proxy.ProxyArboriculture;
import forestry.arboriculture.proxy.ProxyArboricultureClient;
import forestry.arboriculture.villagers.RegisterVillager;
import forestry.core.ModuleCore;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.features.CoreItems;
import forestry.core.items.ItemFruit;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.ForgeUtils;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.ARBORICULTURE, name = "Arboriculture", author = "Binnie & SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.arboriculture.description", lootTable = "arboriculture")
public class ModuleArboriculture extends BlankForestryModule {

	public static final List<Block> validFences = new ArrayList<>();
	private static final String CONFIG_CATEGORY = "arboriculture";
	@SuppressWarnings("NullableProblems")
	//@SidedProxy(clientSide = "forestry.arboriculture.proxy.ProxyArboricultureClient", serverSide = "forestry.arboriculture.proxy.ProxyArboriculture")
	public static ProxyArboriculture proxy = null;
	public static String treekeepingMode = "NORMAL";
	@Nullable
	public static VillagerProfession villagerArborist;

	public ModuleArboriculture() {
		proxy = DistExecutor.safeRunForDist(() -> ProxyArboricultureClient::new, () -> ProxyArboriculture::new);
		ForgeUtils.registerSubscriber(this);

		if (Config.enableVillagers) {
			RegisterVillager.Registers.POINTS_OF_INTEREST.register(FMLJavaModLoadingContext.get().getModEventBus());
			RegisterVillager.Registers.PROFESSIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
			MinecraftForge.EVENT_BUS.register(new RegisterVillager.Events());
		}

		if (TreeConfig.getSpawnRarity(null) > 0.0F) {
			IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
			modEventBus.addGenericListener(Feature.class, ArboricultureFeatures::registerFeatures);
			MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ArboricultureFeatures::onBiomeLoad);
		}
	}

	@Override
	public void setupAPI() {
		TreeManager.treeFactory = new TreeFactory();
		TreeManager.treeMutationFactory = new TreeMutationFactory();

		TreeManager.woodAccess = WoodAccess.getInstance();
	}

	@Override
	public void disabledSetupAPI() {
		TreeManager.woodAccess = WoodAccess.getInstance();
	}

	@Override
	public void preInit() {
		// Capabilities
		CapabilityManager.INSTANCE.register(IArmorNaturalist.class, new NullStorage<>(), () -> ArmorNaturalist.INSTANCE);

		MinecraftForge.EVENT_BUS.register(this);

		// Init rendering
		proxy.initializeModels();

		// Commands
		ModuleCore.rootCommand.then(CommandTree.register());

		if (ModuleHelper.isEnabled(ForestryModuleUids.SORTING)) {
			ArboricultureFilterRuleType.init();
		}
	}

	@Override
	public void doInit() {
		TreeDefinition.initTrees();

		File configFile = new File(Forestry.instance.getConfigFolder(), CONFIG_CATEGORY + ".cfg");

		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");
		if (!Objects.equals(config.getLoadedConfigVersion(), config.getDefinedConfigVersion())) {
			boolean deleted = configFile.delete();
			if (deleted) {
				config = new LocalizedConfiguration(configFile, "1.0.0");
			}
		}
		TreeConfig.parse(config);
		config.save();
	}

	@Override
	public void addLootPoolNames(Set<String> lootPoolNames) {
		lootPoolNames.add("forestry_arboriculture_items");
	}

	@Override
	public boolean processIMCMessage(InterModComms.IMCMessage message) {
		//TODO: IMC
		//		if (message.getMethod().equals("add-fence-block")) {
		//			Supplier<String> blockName = message.getMessageSupplier();
		//			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(message.getMessageSupplier().get()));
		//
		//			if (block != null) {
		//				validFences.add(block);
		//			} else {
		//				IMCUtil.logInvalidIMCMessage(message);
		//			}
		//			return true;
		//		} else if (message.getMethod().equals("blacklist-trees-dimension")) {
		//			String treeUID = message.getNBTValue().getString("treeUID");
		//			int[] dims = message.getNBTValue().getIntArray("dimensions");
		//			for (int dim : dims) {
		//				TreeConfig.blacklistTreeDim(treeUID, dim);
		//			}
		//			return true;
		//		}
		//		return false;
		return false;
	}

	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		crateRegistry.registerCrate(CoreItems.FRUITS.stack(ItemFruit.EnumFruit.CHERRY));
		crateRegistry.registerCrate(CoreItems.FRUITS.stack(ItemFruit.EnumFruit.WALNUT));
		crateRegistry.registerCrate(CoreItems.FRUITS.stack(ItemFruit.EnumFruit.CHESTNUT));
		crateRegistry.registerCrate(CoreItems.FRUITS.stack(ItemFruit.EnumFruit.LEMON));
		crateRegistry.registerCrate(CoreItems.FRUITS.stack(ItemFruit.EnumFruit.PLUM));
		crateRegistry.registerCrate(CoreItems.FRUITS.stack(ItemFruit.EnumFruit.PAPAYA));
		crateRegistry.registerCrate(CoreItems.FRUITS.stack(ItemFruit.EnumFruit.DATES));
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// sapling itemBlock is different from the normal item
		hiddenItems.add(ArboricultureBlocks.SAPLING_GE.stack());
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryArboriculture();
	}

	//    @SubscribeEvent
	//    public void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
	//        BlockState state = event.getState();
	//        Block block = state.getBlock();
	//        if (block instanceof LeavesBlock && !(block instanceof BlockForestryLeaves)) {
	//            PlayerEntity player = event.getHarvester();
	//            if (player != null) {
	//                ItemStack harvestingTool = player.getHeldItemMainhand();
	//                if (harvestingTool.getItem() instanceof IToolGrafter) {
	//                    if (event.getDrops().isEmpty()) {
	//                        World world = event.getWorld();
	//                        Item itemDropped = block.getItemDropped(state, world.rand, 3);
	//                        if (itemDropped != Items.AIR) {
	//                            event.getDrops().add(new ItemStack(itemDropped, 1, block.damageDropped(state)));
	//                        }
	//                    }
	//
	//                    harvestingTool.damageItem(1, player, (entity) -> {
	//                        entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
	//                    });
	//                    if (harvestingTool.isEmpty()) {
	//                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, harvestingTool, Hand.MAIN_HAND);
	//                    }
	//                }
	//            }
	//        }
	//    }

	@Override
	public ISidedModuleHandler getModuleHandler() {
		return proxy;
	}
}
