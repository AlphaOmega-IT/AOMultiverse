package de.alphaomegait.aomultiverse.inventories;

import de.alphaomegait.ao18n.i18n.I18n;
import de.alphaomegait.aocore.aoinv.*;
import de.alphaomegait.aocore.aoinv.filter.IUIFilter;
import de.alphaomegait.aocore.utilities.AnvilUIFactory;
import de.alphaomegait.aocore.utilities.itemstack.ItemBuildable;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.daos.MultiverseWorldDao;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MultiverseWorldEditorUI implements IInventoryProvider {

	private final AOMultiverse       aoMultiverse;
	private final MultiverseWorld    multiverseWorld;
	private final MultiverseWorldDao multiverseWorldDao;

	public MultiverseWorldEditorUI(
		final @NotNull AOMultiverse aoMultiverse,
		final @NotNull Player player,
		final @NotNull String worldName
	) {
		this.aoMultiverse       = aoMultiverse;
		this.multiverseWorldDao = this.aoMultiverse.getMultiverseWorldDao();
		this.multiverseWorld    = this.multiverseWorldDao.findByName(worldName).orElseGet(() -> {

			new I18n.Builder(
				"aomultiverse-world_doesnt_exist",
				player
			).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();

			return new MultiverseWorld();
		});
	}

	/**
	 * Get the inventory for a player.
	 *
	 * @param player The player for whom the inventory is being retrieved
	 *
	 * @return The inventory for the player
	 */
	@Override
	public Optional<AOInventory> get(
		final @NotNull Player player
	) {
		return
			Optional.of(new AOInventory.Builder(
				this.aoMultiverse.getAoCore().getInventoryFactory(),
				this
			).id("aomultiverse_world_selector")
				.size(
					6,
					9
				)
				.title(
					new I18n.Builder(
						"aomultiverse-edit_world_inventory_title",
						player
					).build().displayMessageAsComponent()
				)
				.build());
	}
	
	@Override
	public void init(@NotNull Player player, @NotNull IInvContents invContents) {
		invContents.fill(AOItem.empty());
		
		if (
			this.multiverseWorld.getWorldName() == null
		) {
			Bukkit.getScheduler().runTaskLater(
				this.aoMultiverse,
				() -> player.closeInventory(),
				1L
			);
			return;
		}
		
		invContents.putItem(
			1,
			1,
			AOItem.from(
				new ItemBuildable.Builder(
					Material.RED_BED
				).setName(
					new I18n.Builder(
						"aomultiverse_edit_world-spawnpoint_name",
						player
					).setArgs(this.multiverseWorld.getSpawnLocation().toString())
						.build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse_edit_world-spawnpoint_lore",
						player
					).build().displayMessages()
				).build(),
				"edit_spawnpoint_button",
				event -> {
					this.multiverseWorld.setSpawnLocation(player.getLocation());
					player.closeInventory();
					
					this.updateMultiverseWorld();
					
					new I18n.Builder(
						"aomultiverse_edit_world-spawnpoint_set",
						player
					).setArgs(this.multiverseWorld.getSpawnLocation().toString()).hasPrefix(true).build().sendMessageAsComponent();
				}
			)
		);
		
		invContents.putItem(
			1,
			3,
			AOItem.from(
				new ItemBuildable.Builder(
					Material.RESPAWN_ANCHOR
				).setName(
					new I18n.Builder(
						"aomultiverse_edit_world-is_global_spawn_name",
						player
					).setArgs(this.multiverseWorld.isHasGlobalSpawn() ? "✓" : "✗").build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse_edit_world-is_global_spawn_name",
						player
					).setArgs(this.multiverseWorld.isHasGlobalSpawn() ? "✓" : "✗").build().displayMessages()
				).build(),
				"edit_global_spawn_button",
				event -> {
					final Optional<MultiverseWorld> globalSpawn = this.multiverseWorldDao.getGlobalSpawn();
					
					globalSpawn.ifPresent(spawn -> {
						if (!this.multiverseWorld.isHasGlobalSpawn()) {
							new I18n.Builder(
								"aomultiverse_edit_world-global_spawn_already_set",
								player
							).setArgs(spawn.getWorldName()).hasPrefix(true).build().sendMessageAsComponent();
						} else {
							this.multiverseWorld.setHasGlobalSpawn(! this.multiverseWorld.isHasGlobalSpawn());
							this.updateMultiverseWorld();
							
							new I18n.Builder(
								"aomultiverse_edit_world-global_spawn_set",
								player
							).setArgs(this.multiverseWorld.isHasGlobalSpawn() ? "✓" : "✗")
								.hasPrefix(true)
								.build().sendMessageAsComponent();
						}
					});
					player.closeInventory();
				}
			)
		);
		
		invContents.putItem(
			1,
			5,
			AOItem.from(
				new ItemBuildable.Builder(
					Material.NETHERRACK
				).setName(
					new I18n.Builder(
						"aomultiverse_edit_world-nether_allowed_name",
						player
					).setArgs(this.multiverseWorld.isAllowNether() ?
							"✓" :
							"✗")
						.build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse_edit_world-nether_allowed_lore",
						player
					).setArgs(this.multiverseWorld.isAllowNether() ?
							"✓" :
							"✗")
						.build().displayMessages()
				).build(),
				"edit_allow_nether_button",
				event -> {
				}
			)
		);
		
		invContents.putItem(
			1,
			7,
			AOItem.from(
				new ItemBuildable.Builder(
					Material.END_STONE
				).setName(
					new I18n.Builder(
						"aomultiverse_edit_world-end_allowed_name",
						player
					).setArgs(this.multiverseWorld.isAllowTheEnd() ?
						"✓" :
						"✗").build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse_edit_world-end_allowed_lore",
						player
					).build().displayMessages()
				).build(),
				"edit_allow_end_button",
				event -> {
				}
			)
		);
		
		invContents.putItem(
			3,
			3,
			AOItem.from(
				new ItemBuildable.Builder(
					Material.BARRIER
				).setName(
					new I18n.Builder(
						"aomultiverse_edit_world-world_size_name",
						player
					).setArgs(this.multiverseWorld.getWorldSize())
						.build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse_edit_world-world_size_lore",
						player
					).setArgs(this.multiverseWorld.getWorldSize()).build().displayMessages()
				).build(),
				"edit_world_size_button",
				event -> {
					new AnvilUIFactory(
						this.aoMultiverse.getAoCore()
					).create(
						player,
						"aomultiverse_edit_world-world_size_title",
						new ItemBuildable.Builder(
							Material.BARRIER
						).setName("").build(),
						(input) -> () -> {
							long size;
							
							try {
								size = Long.parseLong(input);
							} catch (
								final NumberFormatException exception
							) {
								new I18n.Builder(
									"aomultiverse_edit_world-world_size_invalid",
									player
								).setArgs(input).hasPrefix(true).build().sendMessageAsComponent();
								return;
							}
							this.multiverseWorld.setWorldSize(size);
							
							if (
								size > 0
							) Bukkit.getWorld(this.multiverseWorld.getWorldName()).getWorldBorder().setSize(size);
							
							this.updateMultiverseWorld();
							new I18n.Builder(
								"aomultiverse_edit_world-world_size_set",
								player
							).setArgs(String.valueOf(size)).hasPrefix(true).build().sendMessageAsComponent();
						},
						() -> invContents.inv().display(player)
					);
					player.closeInventory();
				}
			)
		);
		
		invContents.putItem(
			3,
			5,
			AOItem.from(
				new ItemBuildable.Builder(
					Material.DIAMOND_SWORD
				).setName(
					new I18n.Builder(
						"aomultiverse_edit_world-allowed_pvp_name",
						player
					).setArgs(this.multiverseWorld.isAllowPVP() ? "✓" : "✗").build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse_edit_world-allowed_pvp_lore",
						player
					).setArgs(this.multiverseWorld.isAllowPVP() ? "✓" : "✗").build().displayMessages()
				).build(),
				"edit_allow_pvp_button",
				event -> {
					this.multiverseWorld.setAllowPVP(! this.multiverseWorld.isAllowPVP());
					this.updateMultiverseWorld();
					
					new I18n.Builder(
						"aomultiverse_edit_world-allowed_pvp_set",
						player
					).hasPrefix(true)
						.setArgs(this.multiverseWorld.isAllowPVP() ? "✓" : "✗")
						.build()
						.sendMessageAsComponent();
					
					player.closeInventory();
				}
			)
		);
	}

	@Override
	public void update(
		final @NotNull Player player,
		final @NotNull IInvContents invContents
	) {
		//NOT NEEDED
	}
	
	@Override
	public void dispose(@NotNull Player player) {
		//NOT NEEDED
	}
	
	@Override
	public IInvPagination pagination(
		final @NotNull Player player,
		final @NotNull IInvContents invContents
	) {
		return invContents.pagination();
	}
	
	@Override
	public IUIFilter<?> filter(
		final @NotNull Player player,
		final @NotNull IInvContents invContents
	) {
		return invContents.filter();
	}
	
	@Override
	public void handleError(@NotNull Player player, @NotNull Exception e) {
		this.aoMultiverse.getAoCore().getLogger().logDebug("Error occurred within MultiverseWorldEditorUI", e);
		player.closeInventory();
	}
	
	/**
	 * Update the multiverse world.
	 */
	private void updateMultiverseWorld() {
		this.aoMultiverse.getMultiverseWorlds().put(this.multiverseWorld.getWorldName(), this.multiverseWorld);
		this.multiverseWorldDao.update(this.multiverseWorld);
	}
}