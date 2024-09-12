package de.alphaomegait.aomultiverse.listener;

import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.Optional;

/**
 * Represents a listener for player join events.
 * Handles teleporting players to either the global spawn location or their world spawn location.
 */
public class OnJoin implements Listener {
	
	private final AOMultiverse aoMultiverse;
	
	/**
	 * Constructor for OnJoin listener.
	 * @param aoMultiverse The AOMultiverse instance.
	 */
	public OnJoin(final @NotNull AOMultiverse aoMultiverse) {
		this.aoMultiverse = aoMultiverse;
	}
	
	/**
	 * Handles player spawn location event.
	 * @param event The PlayerSpawnLocationEvent.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerSpawn(final @NotNull PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();
		teleportToGlobalSpawn(player);
	}
	
	/**
	 * Teleports the player to the global spawn location if available.
	 * @param player The player to teleport.
	 */
	private void teleportToGlobalSpawn(Player player) {
		getGlobalSpawnWorld().ifPresentOrElse(
			multiverseWorld -> player.teleportAsync(multiverseWorld.getSpawnLocation()),
			() -> teleportToWorldSpawn(player));
	}
	
	/**
	 * Teleports the player to the world spawn location if available.
	 * @param player The player to teleport.
	 */
	private void teleportToWorldSpawn(Player player) {
		MultiverseWorld multiverseWorld = this.aoMultiverse.getMultiverseWorlds().get(player.getWorld().getName());
		if (multiverseWorld != null) {
			player.teleportAsync(multiverseWorld.getSpawnLocation());
		}
	}
	
	/**
	 * Retrieves the first world with a global spawn location.
	 * @return An Optional containing the MultiverseWorld with a global spawn location, if found.
	 */
	private Optional<MultiverseWorld> getGlobalSpawnWorld() {
		return this.aoMultiverse.getMultiverseWorlds().values().stream()
			.filter(MultiverseWorld::isHasGlobalSpawn)
			.findFirst();
	}
}