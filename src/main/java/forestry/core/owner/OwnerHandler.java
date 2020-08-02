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
package forestry.core.owner;

import javax.annotation.Nullable;
import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;

import com.mojang.authlib.GameProfile;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

public class OwnerHandler implements IOwnerHandler, IStreamable, INbtWritable, INbtReadable {
    @Nullable
    private GameProfile owner = null;

    @Override
    @Nullable
    public GameProfile getOwner() {
        return owner;
    }

    @Override
    public void setOwner(GameProfile owner) {
        this.owner = owner;
    }

    @Override
    public void writeData(PacketBufferForestry data) {
        if (owner == null) {
            data.writeBoolean(false);
        } else {
            data.writeBoolean(true);
            data.writeLong(owner.getId().getMostSignificantBits());
            data.writeLong(owner.getId().getLeastSignificantBits());
            data.writeString(owner.getName());
        }
    }

    @Override
    public void readData(PacketBufferForestry data) {
        if (data.readBoolean()) {
            GameProfile owner = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readString());
            setOwner(owner);
        }
    }

    @Override
    public void read(CompoundNBT data) {
        if (data.contains("owner")) {
            GameProfile owner = NBTUtil.readGameProfile(data.getCompound("owner"));
            if (owner != null) {
                setOwner(owner);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT data) {
        if (this.owner != null) {
            CompoundNBT nbt = new CompoundNBT();
            NBTUtil.writeGameProfile(nbt, owner);
            data.put("owner", nbt);
        }
        return data;
    }
}
