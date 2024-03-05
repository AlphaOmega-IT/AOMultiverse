package de.alphaomegait.aomultiverse.inventories;

import de.alphaomegait.ao18n.I18n;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.daos.MultiverseWorldDao;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import de.alphaomegait.woocore.invhandler.AOCItem;
import de.alphaomegait.woocore.invhandler.AOInv;
import de.alphaomegait.woocore.invhandler.InvManager;
import de.alphaomegait.woocore.invhandler.content.InvContents;
import de.alphaomegait.woocore.invhandler.content.InvProvider;
import de.alphaomegait.woocore.utilities.AnvilFactory;
import de.alphaomegait.woocore.utilities.ItemBuildable;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultiverseWorldEditorInventory implements InvProvider {

	private final AOMultiverse       aoMultiverse;
	private final MultiverseWorld    multiverseWorld;
	private final MultiverseWorldDao multiverseWorldDao;

	public MultiverseWorldEditorInventory(
		final @NotNull AOMultiverse aoMultiverse,
		final @NotNull Player player,
		final @NotNull String worldName
	) {
		this.aoMultiverse       = aoMultiverse;
		this.multiverseWorldDao = new MultiverseWorldDao(this.aoMultiverse);
		this.multiverseWorld = this.multiverseWorldDao.findByName(worldName).orElseGet(() -> {

			new I18n.Builder(
				"aomultiverse.world-doesnt-exist",
				player
			).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();

			return new MultiverseWorld();
		});
	}

	@Override
	public AOInv getInventory(final Player player) {
		return
			AOInv
				.builder()
				.id("aomultiverse_world_selector")
				.size(
					6,
					9
				)
				.provider(this)
				.manager(
					new InvManager(this.aoMultiverse)
				)
				.title(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title",
						player
					).build().displayMessage()
				)
				.build();
	}

	@Override
	public void init(
		final Player player,
		final InvContents invContents
	) {
		invContents.fill(AOCItem.empty());

		if (this.multiverseWorld.getId() == null) {
			Bukkit.getScheduler().runTaskLater(this.aoMultiverse, () -> {
				invContents.inv().close(player);
			}, 1L);
			return;
		}

		invContents.set(
			1,
			1,
			AOCItem.from(
				new ItemBuildable.Builder(
					Material.RED_BED
				).setName(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.spawnpoint-name",
						player
					).setArgs(this.multiverseWorld.getSpawnLocation().toString())
					 .build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.spawnpoint-lore",
						player
					).build().displayMessages()
				).build(),
				event -> {
					this.multiverseWorld.setSpawnLocation(player.getLocation());
					invContents.inv().close(player);

					this.updateMultiverseWorld();

					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.spawnpoint-set",
						player
					).setArgs(this.multiverseWorld.getSpawnLocation().toString())
					 .hasPrefix(true).build().sendMessageAsComponent();
				}
			)
		);

		invContents.set(
			1,
			3,
			AOCItem.from(
				new ItemBuildable.Builder(
					Material.RESPAWN_ANCHOR
				).setName(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.is-global-spawn-name",
						player
					).setArgs(this.multiverseWorld.getHasGlobalSpawn() ? "✓" : "✗").build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.is-global-spawn-name",
						player
					).setArgs(this.multiverseWorld.getHasGlobalSpawn() ? "✓" : "✗").build().displayMessages()
				).build(),
				event -> {
					final Optional<MultiverseWorld> globalSpawn = this.multiverseWorldDao.getGlobalSpawn();

					if (
						globalSpawn.isPresent() &&
						! this.multiverseWorld.getHasGlobalSpawn()
					) {
						new I18n.Builder(
							"aomultiverse.edit-world-inventory-title.global-spawn-already-set",
							player
						).setArgs(globalSpawn.get().getWorldName()).hasPrefix(true).build().sendMessageAsComponent();
						return;
					}

					this.multiverseWorld.setHasGlobalSpawn(! this.multiverseWorld.getHasGlobalSpawn());
					this.updateMultiverseWorld();

					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.global-spawn-set",
						player
					).setArgs(this.multiverseWorld.getHasGlobalSpawn() ? "✓" : "✗")
					 .hasPrefix(true)
					 .build().sendMessageAsComponent();

					invContents.inv().close(player);
				}
			)
		);

		invContents.set(
			1,
			5,
			AOCItem.from(
				new ItemBuildable.Builder(
					Material.NETHERRACK
				).setName(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.nether-allowed-name",
						player
					).setArgs(this.multiverseWorld.getAllowNether() ? "✓" : "✗")
					 .build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.nether-allowed-lore",
						player
					).setArgs(this.multiverseWorld.getAllowNether() ? "✓" : "✗")
					 .build().displayMessages()
				).build(),
				event -> {
					this.multiverseWorld.setAllowNether(! this.multiverseWorld.getAllowNether());
					this.updateMultiverseWorld();

					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.nether-allowed-set",
						player
					).hasPrefix(true)
					 .setArgs(this.multiverseWorld.getAllowNether() ? "✓" : "✗")
					 .build()
					 .sendMessageAsComponent();

					invContents.inv().close(player);
				}
			)
		);

		invContents.set(
			1,
			7,
			AOCItem.from(
				new ItemBuildable.Builder(
					Material.END_STONE
				).setName(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.end-allowed-name",
						player
					).setArgs(this.multiverseWorld.getAllowTheEnd() ? "✓" : "✗").build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.end-allowed-lore",
						player
					).setArgs(this.multiverseWorld.getAllowTheEnd() ? "✓" : "✗")
					 .build().displayMessages()
				).build(),
				event -> {
					this.multiverseWorld.setAllowTheEnd(! this.multiverseWorld.getAllowTheEnd());
					this.updateMultiverseWorld();

					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.end-allowed-set",
						player
					).hasPrefix(true)
					 .setArgs(this.multiverseWorld.getAllowTheEnd() ? "✓" : "✗")
					 .build().sendMessageAsComponent();

					invContents.inv().close(player);
				}
			)
		);

		invContents.set(
			3,
			3,
			AOCItem.from(
				new ItemBuildable.Builder(
					Material.BARRIER
				).setName(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.world-size-name",
						player
					).setArgs(this.multiverseWorld.getWorldSize())
					 .build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.world-size-lore",
						player
					).setArgs(this.multiverseWorld.getWorldSize())
					 .build().displayMessages()
				).build(),
				event -> {
					new AnvilFactory()
						.getAnvil(
							new ItemBuildable.Builder(
								Material.BARRIER
							).build(),
							this.aoMultiverse,
							new I18n.Builder(
								"aomultiverse.edit-world-inventory-title.world-size-title",
								player
							).build().displayMessage()
						).onClick(
							(slot, stateSnapshot) -> {
								if (
									slot != AnvilGUI.Slot.OUTPUT
								) return new ArrayList<>();

								long size;

								try {
									size = Long.parseLong(stateSnapshot.getText());
								} catch (
									final NumberFormatException exception
								) {
									new I18n.Builder(
										"aomultiverse.edit-world-inventory-title.world-size-invalid",
										player
									).setArgs(stateSnapshot.getText()).hasPrefix(true).build().sendMessageAsComponent();
									return new ArrayList<>();
								}
								this.multiverseWorld.setWorldSize(
									size
								);

								this.updateMultiverseWorld();
								new I18n.Builder(
									"aomultiverse.edit-world-inventory-title.world-size-set",
									player
								).setArgs(String.valueOf(size)).hasPrefix(true).build().sendMessageAsComponent();
								return List.of(
									AnvilGUI.ResponseAction.close()
								);
							}
						).open(player);

					invContents.inv().close(player);
				}
			)
		);

		invContents.set(
			3,
			5,
			AOCItem.from(
				new ItemBuildable.Builder(
					Material.DIAMOND_SWORD
				).setName(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.allowed-pvp-name",
						player
					).setArgs(this.multiverseWorld.getAllowPVP() ? "✓" : "✗")
					 .build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.allowed-pvp-lore",
						player
					).setArgs(this.multiverseWorld.getAllowPVP() ? "✓" : "✗")
					 .build().displayMessages()
				).build(),
				event -> {
					this.multiverseWorld.setAllowPVP(! this.multiverseWorld.getAllowPVP());
					this.updateMultiverseWorld();

					new I18n.Builder(
						"aomultiverse.edit-world-inventory-title.allowed-pvp-set",
						player
					).hasPrefix(true)
					 .setArgs(this.multiverseWorld.getAllowPVP() ? "✓" : "✗")
					 .build()
					 .sendMessageAsComponent();

					invContents.inv().close(player);
				}
			)
		);
	}

	@Override
	public void update(
		final Player player,
		final InvContents invContents
	) {
		//NOT NEEDED
	}

	private void updateMultiverseWorld() {
		this.multiverseWorldDao.update(
			this.multiverseWorld,
			this.multiverseWorld.getId()
		);
	}
}