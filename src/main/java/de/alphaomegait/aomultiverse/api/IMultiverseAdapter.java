package de.alphaomegait.aomultiverse.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an adapter for handling teleportation functionalities in a multiverse environment.
 * Provides methods to retrieve global and world-specific spawn locations, as well as teleporting players accordingly.
 */
public interface IMultiverseAdapter {
	
	/**
	 * Retrieves the global spawn location if available.
	 *
	 * @return An Optional containing the global spawn location.
	 */
	CompletableFuture<Optional<Location>> getGlobalSpawnLocation();
	
	/**
	 * Retrieves the spawn location for the player's current world.
	 * @param entity The entity whose world spawn location is to be retrieved.
	 * @return An Optional containing the world spawn location.
	 */
	CompletableFuture<Optional<Location>> getWorldSpawnLocation(Entity entity);

	/**
	 * Retrieves a boolean for the current world.
	 * @param world the World to check.
	 * @return a boolean telling if there is a multiverse world or not.
	 */
	CompletableFuture<Boolean> hasMultiverseSpawn(final World world);
	
	/**
	 * Teleports the player to the global spawn location if available, otherwise to the world spawn location.
	 * @param player The player to teleport.
	 */
	void teleportToSpawn(Player player);
}
