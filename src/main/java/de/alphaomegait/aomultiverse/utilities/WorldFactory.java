package de.alphaomegait.aomultiverse.utilities;

import de.alphaomegait.ao18n.i18n.I18n;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.commands.aomultiverse.EAOMultiverseWorldType;
import de.alphaomegait.aomultiverse.database.daos.MultiverseWorldDao;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import de.alphaomegait.aomultiverse.worldgenerator.plotworldgenerator.PlotBiomeProvider;
import de.alphaomegait.aomultiverse.worldgenerator.plotworldgenerator.PlotChunkGenerator;
import de.alphaomegait.aomultiverse.worldgenerator.voidworldgenerator.VoidBiomeProvider;
import de.alphaomegait.aomultiverse.worldgenerator.voidworldgenerator.VoidChunkGenerator;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A class responsible for creating, deleting, and managing worlds in the multiverse.
 */
public class WorldFactory {
	
	private final AOMultiverse aoMultiverse;
	private final MultiverseWorldDao multiverseWorldDao;
	
	/**
	 * Constructs a new WorldFactory instance.
	 *
	 * @param aoMultiverse The AOMultiverse instance to use for world operations.
	 */
	public WorldFactory(
		final @NotNull AOMultiverse aoMultiverse
	) {
		this.aoMultiverse = aoMultiverse;
		this.multiverseWorldDao = this.aoMultiverse.getMultiverseWorldDao();
	}
	
	/**
	 * Creates a new world based on the specified parameters.
	 *
	 * @param worldName The name of the world to be created.
	 * @param worldType The type of the world to be created.
	 * @param player The player initiating the world creation.
	 * @param force Whether to force create the world if it already exists.
	 */
	public void createWorld(
		final @NotNull String worldName,
		final @NotNull EAOMultiverseWorldType worldType,
		final @NotNull Player player,
		final boolean force
	) {
		World world = Bukkit.getWorld(worldName);
		
		if (world != null && !force) {
			new I18n.Builder("aomultiverse-world_already_exists", player)
				.hasPrefix(true)
				.setArgs(worldName)
				.build()
				.sendMessageAsComponent();
			return;
		}
		
		new I18n.Builder("aomultiverse-creating_world", player)
			.hasPrefix(true)
			.build()
			.sendMessageAsComponent();
		
		CompletableFuture.supplyAsync(() -> {
			WorldCreator worldCreator = switch (worldType) {
				case VOID -> new WorldCreator(worldName + "-" + EAOMultiverseWorldType.VOID.name())
					.generator(new VoidChunkGenerator())
					.keepSpawnLoaded(TriState.TRUE);
				case PLOT -> new WorldCreator(worldName + "-" + EAOMultiverseWorldType.PLOT.name())
					.generator(new PlotChunkGenerator(30, 6, 30, Material.STONE, Material.COBBLESTONE_SLAB, Material.IRON_BLOCK))
					.keepSpawnLoaded(TriState.TRUE);
				default -> new WorldCreator(worldName).keepSpawnLoaded(TriState.TRUE);
			};
			return worldCreator.createWorld();
		}).whenCompleteAsync((worldCreated, throwable) -> {
			if (throwable != null) return;
			
			if (worldCreated == null) {
				new I18n.Builder("aomultiverse-world_creation_failed", player)
					.hasPrefix(true)
					.setArgs(worldName)
					.build()
					.sendMessageAsComponent();
				return;
			}
			
			try {
				this.multiverseWorldDao.create(new MultiverseWorld(worldCreated, worldType));
				
				new I18n.Builder("aomultiverse-world_created", player)
					.hasPrefix(true)
					.setArgs(worldName)
					.build()
					.sendMessageAsComponent();
			} catch (Exception exception) {
				new I18n.Builder("aomultiverse-already_exists", player)
					.hasPrefix(true)
					.setArgs(worldName)
					.build()
					.sendMessageAsComponent();
			}
		}).join();
	}
	
	/**
	 * Deletes the specified world if it exists and is empty of players. Sends appropriate messages to the player based on the result of the deletion attempt.
	 *
	 * @param worldName the name of the world to be deleted
	 * @param player    the player initiating the world deletion
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
				"aomultiverse-world_doesnt_exist",
				player
			).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
			return;
		}
		
		new I18n.Builder(
			"aomultiverse-deleting_world",
			player
		).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
		
		if (
			!world.getEntitiesByClass(Player.class).isEmpty()
		) {
			new I18n.Builder(
				"aomultiverse-world_contains_players",
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
			
			CompletableFuture.supplyAsync(
				() -> this.multiverseWorldDao.findByName(worldName)
			).thenAcceptAsync(oMultiverseWorld -> {
				oMultiverseWorld.ifPresentOrElse(
					multiverseWorld -> {
						this.multiverseWorldDao.delete(multiverseWorld.getId());
						new I18n.Builder(
							"aomultiverse-world_deleted",
							player
						).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
					},
					() -> new I18n.Builder(
						"aomultiverse-failed_to_delete_world_in_database",
						player
					).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent()
				);
			});
		} catch (
			final Exception exception
		) {
			new I18n.Builder(
				"aomultiverse-failed_to_delete_world",
				player
			).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
			this.aoMultiverse.getAoCore().getLogger().logDebug("Failed to delete world with id: " + world.getUID(), exception);
		}
	}
	
	/**
	 * Teleports the specified player to the specified world.
	 *
	 * @param multiverseWorld the world to teleport the player to
	 * @param player          the player to teleport
	 */
	public void teleport(
		final @NotNull MultiverseWorld multiverseWorld,
		final @NotNull Player player
	) {
		player.teleportAsync(multiverseWorld.getSpawnLocation());
		new I18n.Builder(
			"aomultiverse_teleported",
			player
		).hasPrefix(true).setArgs(multiverseWorld.getWorldName()).build().sendMessageAsComponent();
	}
	
	/**
	 * Loads all existing worlds from the folder.
	 */
	public Map<String, MultiverseWorld> loadExistingWorlds() {
		this.aoMultiverse.getAoCore().getLogger().logInfo("Loading existing worlds...");
		
		final Map<String, MultiverseWorld> multiverseWorlds = new HashMap<>();
		this.multiverseWorldDao.findAll().forEach(multiverseWorld -> {
			WorldCreator worldCreator = new WorldCreator(multiverseWorld.getWorldName()).keepSpawnLoaded(TriState.TRUE);
			if (
				multiverseWorld.getWorldType().equalsIgnoreCase(EAOMultiverseWorldType.VOID.name())
			) worldCreator.generator(new VoidChunkGenerator()).biomeProvider(new VoidBiomeProvider());
			
			if (
				multiverseWorld.getWorldType().equalsIgnoreCase(EAOMultiverseWorldType.PLOT.name())
			) worldCreator.generator(new PlotChunkGenerator(
				30,
				6,
				30,
				Material.STONE,
				Material.COBBLESTONE_SLAB,
				Material.IRON_BLOCK
			)).biomeProvider(new PlotBiomeProvider());
			
			worldCreator.createWorld();
			this.aoMultiverse.getAoCore().getLogger().logInfo("Loaded world: " + multiverseWorld.getWorldName() + " with type: " + multiverseWorld.getWorldType());
			
			multiverseWorlds.put(multiverseWorld.getWorldName(), multiverseWorld);
		});
		
		return multiverseWorlds;
	}
}