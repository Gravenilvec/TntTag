package fr.gravenilvec.tnttag;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.gravenilvec.tnttag.listeners.DamageListeners;
import fr.gravenilvec.tnttag.listeners.PlayerListeners;

public class TntTag extends JavaPlugin{

	private TntState state;
	private World playWorld;
	private Location spawn, gameSpawn;
	private TntGameManager gameManager = new TntGameManager(this);
	
	@Override
	public void onEnable() {
		
		saveDefaultConfig();
		setState(TntState.WAITING);
		
		// load configuration
		this.playWorld = Bukkit.getWorld(getConfig().getString("locations.worldName"));
		this.spawn = getLocation("spawn");
		this.gameSpawn = getLocation("gameSpawn");
		
		// register events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListeners(this), this);
		pm.registerEvents(new DamageListeners(this), this);
		
	}
	
	public void setState(TntState state){
		this.state = state;
	}
	
	public boolean isState(TntState state){
		return this.state == state;
	}
	
	public TntGameManager getGameManager(){
		return gameManager;
	}

	public World getWorld(){
		return playWorld;
	}
	
	public Location getSpawn(){
		return spawn;
	}
	
	public Location getGameSpawn(){
		return gameSpawn;
	}
	
	public void teleport(Player player) {
		
		if(getConfig().getBoolean("bungee.enable") == false) return;
	
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try{
			out.writeUTF("Connect");
			out.writeUTF(getConfig().getString("bungee.hubName"));
		}catch(IOException e){
			e.printStackTrace();
		}
		
		player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
		
	}
	
	private Location getLocation(String string) {
		String parseloc = getConfig().getString("locations." + string);
		String[] args = parseloc.split(",");
		double x = Double.valueOf(args[0]);
		double y = Double.valueOf(args[1]);
		double z = Double.valueOf(args[2]);
		float yaw = Float.valueOf(args[3]);
		float pitch = Float.valueOf(args[4]);
		return new Location(playWorld, x, y, z, yaw, pitch);
	}
	
	public String get(String path, boolean withPrefix){
		String msg = getConfig().getString("messages." + path).replace("&", "§");
		if(withPrefix) msg = get("prefix", false) + " " + msg;
		return msg;
	}
	
}
