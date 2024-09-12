package de.alphaomegait.aomultiverse.api;

import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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
	 * @return An Optional containing the global spawn location.
	 */
	public Optional<Location> getGlobalSpawnLocation() {
		return aoMultiverse.getMultiverseWorlds().values().stream()
			.filter(MultiverseWorld::isHasGlobalSpawn)
			.map(MultiverseWorld::getSpawnLocation)
			.findFirst();
	}
	
	/**
	 * Retrieves the spawn location for the player's current world.
	 * @param player The player whose world spawn location is to be retrieved.
	 * @return An Optional containing the world spawn location.
	 */
	public Optional<Location> getWorldSpawnLocation(Player player) {
		return Optional.ofNullable(aoMultiverse.getMultiverseWorlds().get(player.getWorld().getName()))
			.map(MultiverseWorld::getSpawnLocation);
	}
	
	/**
	 * Teleports the player to the global spawn location if available, otherwise to the world spawn location.
	 * @param player The player to teleport.
	 */
	public void teleportToSpawn(Player player) {
		getGlobalSpawnLocation().ifPresentOrElse(
			player::teleportAsync,
			() -> getWorldSpawnLocation(player).ifPresent(player::teleportAsync)
		);
	}
}
