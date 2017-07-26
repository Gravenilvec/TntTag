package fr.gravenilvec.tnttag.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.gravenilvec.tnttag.TntGameManager;
import fr.gravenilvec.tnttag.TntState;
import fr.gravenilvec.tnttag.TntTag;
import fr.gravenilvec.tnttag.api.GameTitles;

public class GameAutoStart extends BukkitRunnable{

	private int timer;
	private TntTag main;
	private TntGameManager gameManager;
	
	public GameAutoStart(TntTag main) {
		this.main = main;
		this.gameManager = main.getGameManager();
		this.timer = main.getConfig().getInt("options.autoStartTimer");
	}
	
	@Override
	public void run() {
		
		for(Player pls : gameManager.getPlayers())
		{
			pls.setLevel(timer);
		}
		
		if(timer == 30 || timer == 15 || timer == 10 || timer == 5 || timer == 4 || timer == 3 || timer == 2 || timer == 1)
		{
			for(Player pls : gameManager.getPlayers())
			{
				GameTitles.sendTitle(pls, "", main.get("subStartTitle", false).replace("<timer>", timer+""), 25);
			}
		}
		
		if(timer == 0)
		{
			for(Player pls : gameManager.getPlayers())
			{
				GameTitles.sendTitle(pls, main.get("prefix", false), main.get("startTitle", false), 25);
				pls.teleport(main.getGameSpawn());
				pls.setWalkSpeed(0.6f);
			}
			
			double online = gameManager.size() / 3;
			
			for(int i = 0; i < Math.round(online); i++){
				gameManager.randomTagger();
			}
			
			GameCycle cycle = new GameCycle(main);
			cycle.runTaskTimer(main, 0, 20);
			main.setState(TntState.PLAYING);
			cancel();
		}
		
		timer--;
	}

}
