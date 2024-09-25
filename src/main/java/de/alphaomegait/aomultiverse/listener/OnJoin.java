package de.alphaomegait.aomultiverse.listener;

import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.concurrent.CompletableFuture;

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
		CompletableFuture.runAsync(() -> {
			Player player = event.getPlayer();
			this.aoMultiverse.getMultiverseWorldDao().findAll().stream().filter(MultiverseWorld::isHasGlobalSpawn).findFirst().ifPresentOrElse(
				multiverseWorld -> player.teleportAsync(multiverseWorld.getSpawnLocation().clone().toCenterLocation().add(0, 1, 0)),
				() -> this.aoMultiverse.getMultiverseWorldDao().findByName(player.getWorld().getName()).ifPresent(
					multiverseWorld -> player.teleportAsync(multiverseWorld.getSpawnLocation().clone().toCenterLocation().add(0, 1, 0))
				)
			);
		}).join();
	}
}