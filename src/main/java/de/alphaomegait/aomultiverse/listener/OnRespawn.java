package de.alphaomegait.aomultiverse.listener;

import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.daos.MultiverseWorldDao;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class OnRespawn implements Listener {

	private final AOMultiverse       aoMultiverse;
	private final MultiverseWorldDao multiverseWorldDao;

	public OnRespawn(
		final @NotNull AOMultiverse aoMultiverse
	) {
		this.aoMultiverse       = aoMultiverse;
		this.multiverseWorldDao = new MultiverseWorldDao(this.aoMultiverse);
	}

	@EventHandler(
		priority = EventPriority.LOWEST
	)
	public void onPlayerRespawn(
		final @NotNull PlayerRespawnEvent event
	) {
		CompletableFuture.runAsync(() -> {
			final Player                    player      = event.getPlayer();
			final Optional<MultiverseWorld> globalSpawn = this.multiverseWorldDao.getGlobalSpawn();
			final Optional<MultiverseWorld> world       = this.multiverseWorldDao.findByName(player.getWorld().getName());
			
			globalSpawn.ifPresentOrElse(
				globalSpawnWorld -> event.setRespawnLocation(globalSpawnWorld.getSpawnLocation()),
				() -> world.ifPresent(worldWorld -> event.setRespawnLocation(worldWorld.getSpawnLocation()))
			);
		}).join();
	}
}