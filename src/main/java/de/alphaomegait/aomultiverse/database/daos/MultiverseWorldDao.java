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

	public List<MultiverseWorld> findAll() {
		return
			this
				.createNamedQuery("MultiverseWorld.findAll")
				.getResultList();
	}

	public Optional<MultiverseWorld> getGlobalSpawn() {
		return
			Optional.ofNullable(
				this
					.createNamedQuery("MultiverseWorld.findByIsGlobalSpawn")
					.getSingleResultOrNull()
			);
	}

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

	@Override
	protected Class<MultiverseWorld> getClazzType() {
		return MultiverseWorld.class;
	}
}