package de.alphaomegait.aomultiverse.api;

import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a class that adapts teleportation functionalities in a multiverse environment.
 * Implements IMultiverseAdapter interface.
 * Provides methods to retrieve global and world-specific spawn locations, as well as teleporting players accordingly.
 */
public class MultiverseAdapter implements IMultiverseAdapter {
	
	private final AOMultiverse aoMultiverse;
	
	public MultiverseAdapter(@NotNull AOMultiverse aoMultiverse) {
		this.aoMultiverse = aoMultiverse;
	}
	
	/**
	 * Retrieves the global spawn location if available.
	 *
	 * @return An Optional containing the global spawn location.
	 */
	public CompletableFuture<Optional<Location>> getGlobalSpawnLocation() {
		return
			CompletableFuture.supplyAsync(() -> this.aoMultiverse.getMultiverseWorldDao().getGlobalSpawn().stream()
				.map(MultiverseWorld::getSpawnLocation)
				.findFirst()
			);
	}
	
	/**
	 * Retrieves the spawn location for the player's current world.
	 * @param entity The entity whose world spawn location is to be retrieved.
	 * @return An Optional containing the world spawn location.
	 */
	public CompletableFuture<Optional<Location>> getWorldSpawnLocation(Entity entity) {
		return
			getGlobalSpawnLocation().thenApplyAsync(globalSpawn -> {
				if (
					globalSpawn.isEmpty()
				) return this.aoMultiverse.getMultiverseWorldDao().findByName(entity.getWorld().getName()).map(MultiverseWorld::getSpawnLocation);
				
				return globalSpawn;
			});
	}

	@Override
	public CompletableFuture<Boolean> hasMultiverseSpawn(World world) {
		return
			CompletableFuture.supplyAsync(() -> {
				return this.aoMultiverse.getMultiverseWorldDao().findByName(world.getName()).isPresent();
			});
	}

	/**
	 * Teleports the player to the global spawn location if available, otherwise to the world spawn location.
	 * @param player The player to teleport.
	 */
	public void teleportToSpawn(Player player) {
		getWorldSpawnLocation(player).thenAcceptAsync((oWorld -> oWorld.ifPresent(player::teleportAsync)));
	}
}
