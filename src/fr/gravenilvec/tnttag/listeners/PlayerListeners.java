package fr.gravenilvec.tnttag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.gravenilvec.tnttag.TntGameManager;
import fr.gravenilvec.tnttag.TntState;
import fr.gravenilvec.tnttag.TntTag;
import fr.gravenilvec.tnttag.api.GameTitles;
import fr.gravenilvec.tnttag.tasks.GameAutoStart;

public class PlayerListeners implements Listener{
	
	private TntTag main;
	private TntGameManager gameManager;

	public PlayerListeners(TntTag main) { 
		this.main = main; 
		this.gameManager = main.getGameManager();
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		
		Player player = event.getPlayer();
		
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().clear();
		player.setWalkSpeed(0.2f);
		player.setLevel(0);
		
		if(main.isState(TntState.PLAYING))
		{
			player.teleport(main.getGameSpawn());
			player.setGameMode(GameMode.SPECTATOR);
			GameTitles.sendTitle(player, main.get("prefix", false), main.get("hasStarted", false), 30);
			event.setJoinMessage(null);
			return;
		}
		
		gameManager.add(player);
		player.setGameMode(GameMode.ADVENTURE);
		player.teleport(main.getSpawn());
		
		event.setJoinMessage(main.get("joinMessage", true).replace("<size>", ""+gameManager.size()).replace("<player>", player.getName()).replace("<maxsize>", Bukkit.getMaxPlayers()+""));
		
		// autostart
		if(main.isState(TntState.WAITING) && gameManager.size() == main.getConfig().getInt("options.minPlayers"))
		{
			GameAutoStart start = new GameAutoStart(main);
			start.runTaskTimer(main, 0, 20);
			main.setState(TntState.STARTING);
		}
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		
		Player player = event.getPlayer();
		
		if(!main.isState(TntState.WAITING))
		{
			gameManager.eliminate(player);
		}
		
		if(gameManager.isTagger(player))
		{
			gameManager.removeTagger(player);
			gameManager.randomTagger();
		}
		
		if(gameManager.getPlayers().contains(player))
		{
			gameManager.getPlayers().remove(player);
		}
		
		event.setQuitMessage(main.get("quitMessage", true).replace("<player>", player.getName()));
		
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		event.setFormat(main.get("chatFormat", false)
		.replace("<message>", event.getMessage())
		.replace("<player>", player.getName()));
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event){
		
		Player player = event.getPlayer();
		
		if(gameManager.isTagger(player))
		{
			player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 8);
		}
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		
		Player player = event.getPlayer();
		
		if(gameManager.isTagger(player) && player.isSneaking())
		{
			player.setWalkSpeed(0.6f);
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 3));
			player.sendMessage("§cVous avez atteint la limite du plugin ! Vous avez été troll par le developpeur");
		}
		
	}
	
	@EventHandler
	public void onMove(EntitySpawnEvent event){
		if(!(event.getEntity() instanceof Player))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFoodLevel(FoodLevelChangeEvent event){
		event.setFoodLevel(20);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		ItemStack it = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		
		if(it == null) return;
		
		if(gameManager.isTagger(player) && it.getType() == Material.TNT)
		{
			event.setCancelled(true);
		}
		
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event){
		event.setCancelled(true);
	}
	
}
