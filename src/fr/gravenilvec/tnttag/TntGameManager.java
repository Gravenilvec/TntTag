package fr.gravenilvec.tnttag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.gravenilvec.tnttag.api.GameTitles;

public class TntGameManager {

	private List<Player> alivePlayers = new ArrayList<>();
	private List<Player> tagged = new ArrayList<>();
	private TntTag main;

	public TntGameManager(TntTag main) {
		this.main = main;
	}

	public List<Player> getPlayers() {
		return alivePlayers;
	}

	public void add(Player player) {
		if (!alivePlayers.contains(player)) {
			alivePlayers.add(player);
		}
	}

	public void remove(Player player) {
		if (alivePlayers.contains(player)) {
			alivePlayers.remove(player);
		}
	}

	public int size() {
		return alivePlayers.size();
	}

	public void eliminate(Player player) {
		Bukkit.broadcastMessage(main.get("deathMessage", true).replace("<player>", player.getName()));
		player.setGameMode(GameMode.SPECTATOR);
		player.teleport(main.getGameSpawn());
		removeTagger(player);
		remove(player);
		checkWin();
	}

	public void checkWin() {

		if (alivePlayers.size() == 0)
			Bukkit.reload();

		if (alivePlayers.size() == 1) {
			Player winner = alivePlayers.get(0);
			winner.getInventory().addItem(new ItemStack(Material.DIAMOND, 1200));

			Bukkit.broadcastMessage(main.get("winnerMessage", true).replace("<player>", winner.getName()));

			Bukkit.getScheduler().runTaskLater(main, new Runnable() {

				@Override
				public void run() {

					for (Player pls : Bukkit.getOnlinePlayers()) {
						pls.kickPlayer("§eFin du Jeu");
						main.teleport(pls);
					}

					Bukkit.reload();

				}

			}, 20 * main.getConfig().getInt("options.closeTimerSec"));

			main.setState(TntState.FINISH);
		}

	}

	public void tagPlayer(Player tagger, Player target) {

		if(!isTagger(target))
		{
			removeTagger(tagger);

			if (!tagged.contains(target)) {
				tagged.add(target);
				target.getInventory().setHelmet(new ItemStack(Material.TNT));
				target.getInventory().setItem(0, new ItemStack(Material.TNT));
				target.setPlayerListName(main.get("tabListTagged", false) + target.getName());
				
				Bukkit.broadcastMessage(main.get("hitMessage", true).replace("<player>", target.getName()));
				
				tagger.setWalkSpeed(0.5f);
				GameTitles.sendTitle(target, "", main.get("hitChange", false), 25);

				target.playSound(target.getLocation(), Sound.EXPLODE, 8, 7);
				target.getWorld().playEffect(target.getLocation(), Effect.SMOKE, 12);

			}
		}

	}

	public List<Player> getTagged() {
		return tagged;
	}

	public boolean isTagger(Player player) {
		return tagged.contains(player);
	}

	public void randomTagger() {
		Random random = new Random();

		int limit = random.nextInt(size());

		if (size() > tagged.size()) {
			Player randomPlayer = alivePlayers.get(limit);
			tagPlayer(randomPlayer, randomPlayer);
		}

	}

	public void removeTagger(Player tagger) {

		if (tagged.contains(tagger)) {
			tagged.remove(tagger);

			if (tagger.getInventory().getHelmet() != null
					&& tagger.getInventory().getHelmet().getType() == Material.TNT) {
				// remove tnt tag
				tagger.getInventory().setHelmet(new ItemStack(Material.AIR));
				tagger.setPlayerListName(tagger.getName());
				tagger.setWalkSpeed(0.3f);
				tagger.getInventory().clear();
			}

		}

	}

}
