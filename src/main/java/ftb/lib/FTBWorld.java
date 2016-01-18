package ftb.lib;

import ftb.lib.api.*;
import latmod.lib.*;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

public class FTBWorld
{
	public static FTBWorld server = null, client = null;
	
	public static FTBWorld get(Side s)
	{ return s.isServer() ? server : client; }
	
	public final Side side;
	private GameMode currentMode;
	
	private File currentModeFile = null;
	private File currentWorldIDFile = null;
	
	public FTBWorld()
	{
		side = Side.CLIENT;
		currentMode = new GameMode("default");
	}
	
	public FTBWorld(WorldServer w)
	{
		side = Side.SERVER;
		currentMode = GameModes.getGameModes().defaultMode;
		try
		{
			currentModeFile = new File(FTBLib.folderWorld, "ftb_gamemode.txt");
			currentMode = GameModes.getGameModes().get(LMFileUtils.loadAsText(currentModeFile).trim());
		}
		catch(Exception ex) { /*ex.printStackTrace();*/ }
		
		for(GameMode s : GameModes.getGameModes().modes.values()) s.getFolder();
		
		FTBLib.logger.info("Current Mode: " + currentMode);
	}
	
	public GameMode getMode()
	{ return currentMode; }
	
	public void writeReloadData(ByteIOStream io)
	{
		io.writeUTF(currentMode.ID);
	}
	
	public void readReloadData(ByteIOStream io)
	{
		String mode = io.readUTF();
		currentMode = GameModes.getGameModes().get(mode);
	}
	
	/**
	 * 0 = OK, 1 - Mode is invalid, 2 - Mode already set (will be ignored and return 0, if forced == true)
	 */
	public int setMode(String s)
	{
		GameMode m = GameModes.getGameModes().modes.get(s);
		
		if(m == null) return 1;
		if(m.equals(currentMode)) return 2;
		
		currentMode = m;
		
		if(side.isServer())
		{
			try { LMFileUtils.save(currentModeFile, currentMode.ID); }
			catch(Exception ex) { ex.printStackTrace(); }
		}
		
		return 0;
	}
}