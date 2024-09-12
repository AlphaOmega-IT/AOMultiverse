package de.alphaomegait.aomultiverse.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Represents an adapter for handling teleportation functionalities in a multiverse environment.
 * Provides methods to retrieve global and world-specific spawn locations, as well as teleporting players accordingly.
 */
public interface IMultiverseAdapter {
	
	/**
	 * Retrieves the global spawn location if available.
	 * @return An Optional containing the global spawn location.
	 */
	Optional<Location> getGlobalSpawnLocation();
	
	/**
	 * Retrieves the spawn location for the player's current world.
	 * @param player The player whose world spawn location is to be retrieved.
	 * @return An Optional containing the world spawn location.
	 */
	Optional<Location> getWorldSpawnLocation(Player player);
	
	/**
	 * Teleports the player to the global spawn location if available, otherwise to the world spawn location.
	 * @param player The player to teleport.
	 */
	void teleportToSpawn(Player player);
}
