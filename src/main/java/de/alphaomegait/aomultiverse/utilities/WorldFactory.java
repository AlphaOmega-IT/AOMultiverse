package de.alphaomegait.aomultiverse.utilities;

import de.alphaomegait.ao18n.I18n;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.commands.aomultiverse.EAOMultiverseWorldType;
import de.alphaomegait.aomultiverse.database.daos.MultiverseWorldDao;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import de.alphaomegait.aomultiverse.voidworldgenerator.VoidBiomeProvider;
import de.alphaomegait.aomultiverse.voidworldgenerator.VoidChunkGenerator;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldFactory {

	private final AOMultiverse       aoMultiverse;
	private final MultiverseWorldDao multiverseWorldDao;
	private final Logger             logger;

	public WorldFactory(
		final @NotNull AOMultiverse aoMultiverse,
		final @NotNull Logger logger
	) {
		this.aoMultiverse       = aoMultiverse;
		this.logger             = logger;
		this.multiverseWorldDao = new MultiverseWorldDao(this.aoMultiverse);
	}

	/**
	 * A method to create a new world in the game.
	 *
	 * @param  worldName  the name of the world to be created
	 * @param  worldType  the type of world to be created
	 * @param  player     the player triggering the world creation
	 */
	public void createWorld(
		final @NotNull String worldName,
		final @NotNull EAOMultiverseWorldType worldType,
		final @NotNull Player player,
		final boolean force
	) {
		final World world = Bukkit.getWorld(worldName);

		if (
			world != null && ! force
		) {
			new I18n.Builder(
				"aomultiverse.already-exists",
				player
			).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
			return;
		}

		new I18n.Builder(
			"aomultiverse.creating-world",
			player
		).hasPrefix(true).build().sendMessageAsComponent();

		CompletableFuture.supplyAsync(() -> {
			if (worldType.equals(EAOMultiverseWorldType.VOID)) {
				return new WorldCreator(worldName + "-" + EAOMultiverseWorldType.VOID.name())
					.generator(
						new VoidChunkGenerator()
					)
					.biomeProvider(new VoidBiomeProvider())
					.keepSpawnLoaded(TriState.TRUE);
			} else {
				return new WorldCreator(worldName).keepSpawnLoaded(TriState.TRUE);
			}
		}).whenCompleteAsync((
													 (worldCreator, throwable) -> {
														 if (
															 throwable != null
														 ) return;

														 Bukkit.getScheduler().runTask(
															 this.aoMultiverse,
															 () -> {
																 final World createdWorld = worldCreator.createWorld();

																 if (createdWorld == null) {
																	 new I18n.Builder(
																		 "aomultiverse.world-creation-failed",
																		 player
																	 ).hasPrefix(true)
																		.setArgs(worldName)
																		.build()
																		.sendMessageAsComponent();
																	 return;
																 }

																 try {
																	 final MultiverseWorld multiverseWorld = new MultiverseWorld(
																		 createdWorld,
																		 worldType
																	 );

																	 this.multiverseWorldDao.persistEntity(multiverseWorld);

																	 new I18n.Builder(
																		 "aomultiverse.world-created",
																		 player
																	 ).hasPrefix(true)
																		.setArgs(worldName)
																		.build()
																		.sendMessageAsComponent();
																 } catch (
																	 final Exception exception
																 ) {
																	 new I18n.Builder(
																		 "aomultiverse.already-exists",
																		 player
																	 ).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
																 }
															 }
														 );
													 }
												 )).join();
	}

	/**
	 * Deletes the specified world if it exists and is empty of players. Sends appropriate messages to the player based on the result of the deletion attempt.
	 *
	 * @param  worldName  the name of the world to be deleted
	 * @param  player     the player initiating the world deletion
	 */
	public void deleteWorld(
		final @NotNull String worldName,
		final @NotNull Player player
	) {
		final World world = Bukkit.getWorld(worldName);

		if (
			world == null
		) {
			new I18n.Builder(
				"aomultiverse.world-doesnt-exist",
				player
			).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
			return;
		}

		new I18n.Builder(
			"aomultiverse.deleting-world",
			player
		).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();

		if (
			! world.getEntitiesByClass(Player.class).isEmpty()
		) {
			new I18n.Builder(
				"aomultiverse.world-has-players",
				player
			).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
			return;
		}

		try {
			Bukkit.unloadWorld(
				world,
				false
			);
			FileUtils.deleteDirectory(world.getWorldFolder());

			final Optional<MultiverseWorld> multiverseWorld = this.multiverseWorldDao.findByName(world.getName());

			multiverseWorld.ifPresent(this.multiverseWorldDao::removeEntity);

			new I18n.Builder(
				"aomultiverse.world-deleted",
				player
			).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
		} catch (
			final Exception exception
		) {
			this.logger.log(
				Level.WARNING,
				"Failed to delete the world: " + worldName,
				exception
			);
			new I18n.Builder(
				"aomultiverse.failed-to-delete-world",
				player
			).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
		}
	}

	/**
	 * Teleports the specified player to the specified world.
	 *
	 * @param  worldName  the name of the world to teleport the player to
	 * @param  player     the player to teleport
	 */
	public void teleport(
		final @NotNull String worldName,
		final @NotNull Player player
	) {
		final World world = Bukkit.getWorld(worldName);

		if (
			world == null
		) {
			new I18n.Builder(
				"aomultiverse.world-doesnt-exist",
				player
			).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
			return;
		}

		player.teleportAsync(world.getSpawnLocation());
		new I18n.Builder(
			"aomultiverse.teleported",
			player
		).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
	}

	/**
	 * Loads all existing worlds from the folder.
	 */
	public void loadExistingWorlds(

	) {
		this.logger.info("Loading existing worlds...");

		this.multiverseWorldDao.findAll().forEach(multiverseWorld -> {
			if (
				multiverseWorld.getWorldType().equalsIgnoreCase(EAOMultiverseWorldType.VOID.name())
			) {
				new WorldCreator(
					multiverseWorld.getWorldName()
				).generator(
					 new VoidChunkGenerator()
				 ).biomeProvider(
					 new VoidBiomeProvider()
				 ).keepSpawnLoaded(TriState.TRUE)
				 .createWorld();
			} else {
				new WorldCreator(multiverseWorld.getWorldName()).keepSpawnLoaded(TriState.TRUE).createWorld();
			}

			this.logger.info("Loaded world: " + multiverseWorld.getWorldName() + " with type: " + multiverseWorld.getWorldType());
		});
	}
}