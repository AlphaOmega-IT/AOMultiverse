package de.alphaomegait.aomultiverse.database.daos;

import de.alphaomegait.aocore.AOCore;
import de.alphaomegait.aocore.database.daos.BaseDao;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for interacting with MultiverseWorld entities in the database.
 */
public class MultiverseWorldDao extends BaseDao<MultiverseWorld> {
	
	/**
	 * Constructs a new MultiverseWorldDao instance.
	 *
	 * @param aoCore The AOCore instance to use for database operations.
	 */
	public MultiverseWorldDao(final @NotNull AOCore aoCore) {
		super(aoCore, MultiverseWorld.class);
	}
	
	/**
	 * Find a MultiverseWorld entity by its name.
	 *
	 * @param worldName The name of the world to search for.
	 * @return An Optional containing the found MultiverseWorld entity, or empty if not found.
	 */
	public Optional<MultiverseWorld> findByName(final @NotNull String worldName) {
		return this.createNamedQuery("MultiverseWorld.findByName")
			.setParameter("worldName", worldName)
			.getResultStream()
			.findFirst();
	}
	
	/**
	 * Find all MultiverseWorld entities by their type.
	 *
	 * @param worldType The type of the worlds to search for.
	 * @return A list of MultiverseWorld entities matching the given type.
	 */
	public List<MultiverseWorld> findByType(final @NotNull String worldType) {
		return this.createNamedQuery("MultiverseWorld.findType")
			.setParameter("worldType", worldType)
			.getResultStream()
			.toList();
	}
	
	/**
	 * Find all MultiverseWorld entities.
	 *
	 * @return A list of all MultiverseWorld entities in the database.
	 */
	public List<MultiverseWorld> findAll() {
		return this.createNamedQuery("MultiverseWorld.findAll")
			.getResultStream()
			.toList();
	}
	
	/**
	 * Get the MultiverseWorld entity representing the global spawn.
	 *
	 * @return An Optional containing the global spawn MultiverseWorld entity, or empty if not found.
	 */
	public Optional<MultiverseWorld> getGlobalSpawn() {
		return this.createNamedQuery("MultiverseWorld.findByIsGlobalSpawn")
			.getResultStream()
			.findFirst();
	}
}