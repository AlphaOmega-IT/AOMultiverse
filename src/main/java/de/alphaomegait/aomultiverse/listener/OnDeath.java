package de.alphaomegait.aomultiverse.listener;

import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.daos.MultiverseWorldDao;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import de.alphaomegait.woocore.utilities.TeleportFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class OnDeath implements Listener {

	private final AOMultiverse       aoMultiverse;
	private final MultiverseWorldDao multiverseWorldDao;

	public OnDeath(
		final @NotNull AOMultiverse aoMultiverse
	) {
		this.aoMultiverse       = aoMultiverse;
		this.multiverseWorldDao = new MultiverseWorldDao(this.aoMultiverse);
	}

	@EventHandler(
		priority = EventPriority.LOWEST
	)
	public void onPlayerDeath(
		final @NotNull PlayerDeathEvent event
	) {
		CompletableFuture.runAsync(() -> {
			final Player                    player      = event.getPlayer();
			final Optional<MultiverseWorld> globalSpawn = this.multiverseWorldDao.getGlobalSpawn();
			final Optional<MultiverseWorld> world       = this.multiverseWorldDao.findByName(player.getWorld().getName());
			
			globalSpawn.ifPresentOrElse(
				globalSpawnWorld -> this.teleport(
					player,
					globalSpawnWorld.getSpawnLocation()
				),
				() -> world.ifPresent(worldWorld -> this.teleport(
					player,
					worldWorld.getSpawnLocation()
				))
			);
		});
	}

	private void teleport(
		final @NotNull Player player,
		final @NotNull Location location
	) {
		Bukkit.getScheduler().runTask(
			this.aoMultiverse,
			() ->
				new TeleportFactory(
					this.aoMultiverse,
					this.aoMultiverse.getConfigManager()
				).teleport(
					player,
					location,
					""
				)
		);
	}
}