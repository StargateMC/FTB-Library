package com.feed_the_beast.ftblib.integration;

import com.feed_the_beast.ftblib.client.FTBLibClientEventHandler;
import com.feed_the_beast.ftblib.events.client.CustomClickEvent;
import com.feed_the_beast.ftblib.lib.gui.GuiContainerWrapper;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IGlobalGuiHandler;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author mezz
 * @author LatvianModder
 */
@JEIPlugin
public class FTBLibJEIIntegration implements IModPlugin
{
	public static IJeiRuntime RUNTIME;

	@Override
	public void onRuntimeAvailable(IJeiRuntime r)
	{
		RUNTIME = r;
	}

	@Override
	public void register(IModRegistry registry)
	{
		try
		{
			registry.addGlobalGuiHandlers(new IGlobalGuiHandler()
			{
				@Override
				public Collection<Rectangle> getGuiExtraAreas()
				{
					GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;

					if (FTBLibClientEventHandler.areButtonsVisible(currentScreen))
					{
						return Collections.singleton(FTBLibClientEventHandler.lastDrawnArea);
					}

					return Collections.emptySet();
				}
			});
		}
		catch (RuntimeException | LinkageError ignored)
		{
			// only JEI 4.14.0 or higher supports addGlobalGuiHandlers
		}

		try
		{
			registry.addGhostIngredientHandler(GuiContainerWrapper.class, new JEIGhostItemHandler());
		}
		catch (RuntimeException | LinkageError ignored)
		{
		}

		MinecraftForge.EVENT_BUS.register(FTBLibJEIIntegration.class);
	}

	public static boolean openFocus(int key, @Nullable Object object)
	{
		if (object != null && RUNTIME != null)
		{
			if (key == Keyboard.KEY_R) // Replace with JEI Key
			{
				RUNTIME.getRecipesGui().show(RUNTIME.getRecipeRegistry().createFocus(IFocus.Mode.OUTPUT, object));
				return true;
			}
			else if (key == Keyboard.KEY_U) // Replace with JEI Key
			{
				RUNTIME.getRecipesGui().show(RUNTIME.getRecipeRegistry().createFocus(IFocus.Mode.INPUT, object));
				return true;
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void onCustomClick(CustomClickEvent event)
	{
		if (event.getID().getNamespace().equals("jeicategory"))
		{
			if (RUNTIME != null)
			{
				RUNTIME.getRecipesGui().showCategories(Arrays.asList(event.getID().getPath().split(";")));
			}
		}
	}
}
