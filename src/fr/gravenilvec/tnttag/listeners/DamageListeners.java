package fr.gravenilvec.tnttag.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import fr.gravenilvec.tnttag.TntGameManager;
import fr.gravenilvec.tnttag.TntState;
import fr.gravenilvec.tnttag.TntTag;

public class DamageListeners implements Listener {

	private TntTag main;
	private TntGameManager gameManager;
	
	public DamageListeners(TntTag main) {
		this.main = main;
		this.gameManager = main.getGameManager();
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event){
		
		if(!main.isState(TntState.PLAYING))
		{
			event.setCancelled(true);
			return;
		}
		
		event.setDamage(0);
	}

	@EventHandler
	public void onPvP(EntityDamageByEntityEvent event){
		
		Entity entityTarget = event.getEntity();
		
		if(entityTarget instanceof Player && event.getDamager() instanceof Player)
		{
			Player target = (Player) entityTarget;
			Player tagger = (Player) event.getDamager();
			
			if(gameManager.isTagger(tagger))
			{
				gameManager.tagPlayer(tagger, target);
			}
			
		}
		
		
	}
	
}
