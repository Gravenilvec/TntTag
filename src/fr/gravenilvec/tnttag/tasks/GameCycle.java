package fr.gravenilvec.tnttag.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.gravenilvec.tnttag.TntGameManager;
import fr.gravenilvec.tnttag.TntState;
import fr.gravenilvec.tnttag.TntTag;
import fr.gravenilvec.tnttag.api.GameTitles;

public class GameCycle extends BukkitRunnable {

	//integers
	private int timer;
	private int cycles = 0;
	
	private TntTag main;
	private TntGameManager gameManager;
	
	public GameCycle(TntTag main) {
		this.main = main;
		this.gameManager = main.getGameManager();
		this.timer = main.getConfig().getInt("options.gameCycleTimer");
	}
	
	@Override
	public void run() {
		
		for(Player pls : Bukkit.getOnlinePlayers())
		{
			GameTitles.sendActionBar(pls, "§c"+timer);
		}
		
		gameManager.checkWin();
		
		if(timer == 5 || timer == 4 || timer == 3 || timer == 2 || timer == 1)
		{
			for(Player pls : gameManager.getPlayers())
			{
				GameTitles.sendTitle(pls, "", main.get("subStartTitle", false).replace("<timer>", timer+""), 25);
			}
		}
		
		if(timer == 0)
		{			
			timer = main.getConfig().getInt("options.gameCycleTimer");
			cycles++;
			
			main.getWorld().setThundering(false);
			main.getWorld().setTime(1000);
			
			for(Player pls : Bukkit.getOnlinePlayers())
			{
				GameTitles.sendTitle(pls, main.get("boomMessage", false), "", 45);
				
				if(gameManager.isTagger(pls))
				{
					gameManager.eliminate(pls);
				}
				
			}
			
			if(cycles >= 3)
			{
				cycles = 0;
				
				for(Player pls : Bukkit.getOnlinePlayers())
				{
					pls.teleport(main.getGameSpawn());
				}
				
			}
			
			if(gameManager.size() < 3)
			{
				gameManager.randomTagger();
			}
			
			double online = gameManager.size() / 3;
			
			for(int i = 0; i < Math.round(online); i++){
				gameManager.randomTagger();
			}
			
		}
		
		if(main.isState(TntState.FINISH))
		{
			cancel();
		}
		
		timer--;
		
	}

	
	
}
