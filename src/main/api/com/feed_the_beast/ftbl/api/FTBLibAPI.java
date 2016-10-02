package com.feed_the_beast.ftbl.api;

import com.feed_the_beast.ftbl.api.config.IConfigContainer;
import com.feed_the_beast.ftbl.api.events.ReloadType;
import com.feed_the_beast.ftbl.api.gui.IGuiSelectors;
import com.feed_the_beast.ftbl.api.info.IInfoPage;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Created by LatvianModder on 11.08.2016.
 */
public interface FTBLibAPI
{
    boolean hasServer(@Nullable String id);

    boolean isClientPlayerOP();

    Collection<ITickable> ticking();

    IPackModes getPackModes();

    ISharedData getSharedData(Side side);

    @Nullable
    IUniverse getUniverse();

    void addServerCallback(int timer, Runnable runnable);

    void reload(ICommandSender sender, ReloadType type);

    void openGui(ResourceLocation guiID, EntityPlayerMP player, @Nullable NBTTagCompound data);

    void sendNotification(@Nullable EntityPlayerMP player, INotification n);

    void editServerConfig(EntityPlayerMP player, @Nullable NBTTagCompound nbt, IConfigContainer configContainer);

    IGuiSelectors selectors();

    void displayInfoGui(EntityPlayerMP player, IInfoPage page);
}