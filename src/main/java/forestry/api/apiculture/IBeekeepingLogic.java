/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;

/**
 * Stores beekeeping logic for bee housings.
 * Get one with BeeManager.beeRoot.createBeekeepingLogic(IBeeHousing housing)
 * Save and load it to NBT using the INbtWritable methods.
 */
public interface IBeekeepingLogic extends INbtWritable, INbtReadable {

    /* SERVER */

    /**
     * Checks that the bees can work, setting error conditions on the housing where needed
     *
     * @return true if no errors are present and doWork should be called
     */
    boolean canWork();

    /**
     * Performs actual work, breeding, production, etc.
     */
    void doWork();

    /**
     * Force the logic to refresh any cached values and error states.
     * Call this when a player opens the gui so that all errors are up to date.
     */
    void clearCachedValues();


    /* CLIENT */

    /**
     * Sync to client by using {@link #write(CompoundNBT)} in your {@link TileEntity#getUpdateTag()}
     */
    void syncToClient();

    void syncToClient(ServerPlayerEntity player);

    /**
     * Get the progress bar for breeding and production.
     * To avoid network spam, this is only available server-side,
     * and must be synced manually to the client when a GUI is open.
     */
    int getBeeProgressPercent();

    /**
     * Whether bee fx should be active.
     * Internally, this is automatically synced to the client.
     */
    @OnlyIn(Dist.CLIENT)
    boolean canDoBeeFX();

    /**
     * Display bee fx. Calls IBee.doFX(IEffectData[] storedData, IBeeHousing housing) on the queen.
     * Internally, the queen is automatically synced to the client for the fx.
     */
    @OnlyIn(Dist.CLIENT)
    void doBeeFX();

    /**
     * Used by bee fx to direct bees to nearby flowers.
     * These positions are synced to the client from the server.
     */
    List<BlockPos> getFlowerPositions();

    default void readData(PacketBuffer data) throws IOException {

    }

    default void writeData(PacketBuffer data) {

    }
}
