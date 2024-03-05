package de.alphaomegait.aomultiverse.database.daos;

import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import de.alphaomegait.woocore.database.daos.BaseDao;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiverseWorldDao extends BaseDao<MultiverseWorld> {

	private final Logger logger;

	public MultiverseWorldDao(
		final @NotNull AOMultiverse aoMultiverse
	) {
		super(
			aoMultiverse.getWooCore(),
			aoMultiverse.getLogger()
		);
		this.logger = aoMultiverse.getLogger();
	}

	/**
	 * Finds a MultiverseWorld by name.
	 *
	 * @param  worldName   the name of the world to find
	 * @return             an Optional containing the found MultiverseWorld, or empty if not found
	 */
	public Optional<MultiverseWorld> findByName(
		final @NotNull String worldName
	) {
		return
			Optional.ofNullable(
				this
					.createNamedQuery("MultiverseWorld.findByName")
					.setParameter(
						"worldName",
						worldName
					)
					.getSingleResultOrNull()
			);
	}

	/**
	 * Finds MultiverseWorld by type.
	 *
	 * @param  worldType  the type of the world to find
	 * @return            a list of MultiverseWorld objects matching the given type
	 */
	public List<MultiverseWorld> findByType(
		final @NotNull String worldType
	) {
		return
			this
				.createNamedQuery("MultiverseWorld.findType")
				.setParameter(
					"worldType",
					worldType
				)
				.getResultList();
	}

	/**
	 * Find all MultiverseWorlds
	 *
	 * @return         list of MultiverseWorld objects
	 */
	public List<MultiverseWorld> findAll() {
		return
			this
				.createNamedQuery("MultiverseWorld.findAll")
				.getResultList();
	}

	/**
	 * Retrieves the global spawn location of the MultiverseWorld entity.
	 *
	 * @return         	an Optional containing the global spawn MultiverseWorld, if present
	 */
	public Optional<MultiverseWorld> getGlobalSpawn() {
		return
			Optional.ofNullable(
				this
					.createNamedQuery("MultiverseWorld.findByIsGlobalSpawn")
					.getSingleResultOrNull()
			);
	}

	/**
	 * Updates the MultiverseWorld entity with the given id.
	 *
	 * @param  multiverseWorld   the MultiverseWorld entity to be updated
	 * @param  id                the id of the MultiverseWorld entity
	 */
	public void update(
		final @NotNull MultiverseWorld multiverseWorld,
		final long id
	) {
		try (
			final Session session = this.getOrCreateSession()
		) {
			final String hqlQuery =
				"UPDATE MultiverseWorld m " +
				"SET m.worldName = :worldName, " +
				"m.worldType = :worldType, " +
				"m.spawnLocation = :spawnLocation, " +
				"m.worldSize = :worldSize, " +
				"m.worldSeed = :worldSeed, " +
				"m.allowNether = :allowNether, " +
				"m.hasGlobalSpawn = :hasGlobalSpawn " +
				"WHERE m.id = :id";

			session.beginTransaction();
			session
				.createMutationQuery(hqlQuery)
				.setParameter(
					"worldName",
					multiverseWorld.getWorldName()
				)
				.setParameter(
					"worldType",
					multiverseWorld.getWorldType()
				)
				.setParameter(
					"spawnLocation",
					multiverseWorld.getSpawnLocation()
				)
				.setParameter(
					"worldSize",
					multiverseWorld.getWorldSize()
				)
				.setParameter(
					"worldSeed",
					multiverseWorld.getWorldSeed()
				)
				.setParameter(
					"allowNether",
					multiverseWorld.getAllowNether()
				)
				.setParameter(
					"hasGlobalSpawn",
					multiverseWorld.getHasGlobalSpawn()
				)
				.setParameter(
					"id",
					id
				)
				.executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e) {
			this.logger.log(
				Level.SEVERE,
				"Failed to update MultiverseWorld with id " + id,
				e
			);
		}
	}

	/**
	 * A description of the entire Java function.
	 *
	 * @return         	description of return value
	 */
	@Override
	protected Class<MultiverseWorld> getClazzType() {
		return MultiverseWorld.class;
	}
}