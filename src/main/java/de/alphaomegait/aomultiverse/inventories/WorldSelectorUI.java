package de.alphaomegait.aomultiverse.inventories;

import de.alphaomegait.ao18n.I18n;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.heads.WorldHead;
import de.alphaomegait.woocore.utilities.HeadFactory;
import de.alphaomegait.woocore.utilities.ItemBuildable;
import de.alphaomegait.woocore.utilities.ItemEditable;
import de.alphaomegait.woocore.utilities.TeleportFactory;
import de.alphaomegait.woocore.wooinv.*;
import de.alphaomegait.woocore.wooinv.filter.IInvFilter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WorldSelectorUI implements IInventoryProvider {

	private final AOMultiverse aoMultiverse;
	private final List<World> worlds = new ArrayList<>();
	private final List<WooItem> worldButtons = new LinkedList<>();

	public WorldSelectorUI(
		final @NotNull AOMultiverse aoMultiverse,
		final @NotNull Player player
	) {
		this.aoMultiverse = aoMultiverse;
		this.worlds.addAll(this.aoMultiverse.getServer().getWorlds());
		this.worlds.sort(Comparator.comparing(World::getName));

		this.worlds.forEach(world -> {
			this.worldButtons.add(
				WooItem.from(
					new ItemEditable.Builder(
						new HeadFactory.Builder(
							WorldHead.class,
							player
						).build()
					).setName(
						new I18n.Builder(
							"aomultiverse.display.world-name",
							player
						).setArgs(world.getName())
						 .build().displayMessageAsComponent()
					).setLore(
						new I18n.Builder(
							"aomultiverse.display.world-lore",
							player
						).build().displayMessages()
					).build(),
					"aomultiverse_world_selector",
					event -> {
						new TeleportFactory(
							this.aoMultiverse,
							this.aoMultiverse.getConfigManager()
						).teleport(
							player,
							world.getSpawnLocation(),
							"aomultiverse.teleport"
						);
					}
				));
		});
	}

	/**
	 * Get the inventory for a player.
	 *
	 * @param  player  the player for whom the inventory is being retrieved
	 * @return         the inventory for the player
	 */
	@Override
	public WooInventory get(
		final @NotNull Player player
	) {
		return
			new WooInventory.Builder(
				this.aoMultiverse.getInventoryFactory(),
				this
			)
				.id("aomultiverse_world_selector")
				.size(6, 9)

				.maxItemsPerPageAndMaxPages(
					13,
					this.worlds
				)
				.title(
					new I18n.Builder(
						"aomultiverse.select-world-inventory-title",
						player
					).build().displayMessageAsComponent()
				)
				.build();
	}

	/**
	 * A method to initialize the player and inventory contents.
	 *
	 * @param  player       the player object
	 * @param  invContents  the inventory contents object
	 */
	@Override
	public void init(
		final @NotNull Player player,
		final @NotNull IInvContents invContents
	) {
		invContents.fill(WooItem.empty());
		final WooItem[] items = new WooItem[invContents.inv().getMaxItemsPerPage() * invContents.inv().getMaxPages()];

		for (
			int i = 0; i < items.length; i++
		) {
			if (
				i >= this.worldButtons.size()
			) {
				Arrays.fill(
					items,
					WooItem.empty(new ItemBuildable.Builder(Material.AIR).build())
				);
				break;
			}

			items[i] = this.worldButtons.get(i);
		}
	}

	@Override
	public void update(
		final @NotNull Player player,
		final @NotNull IInvContents invContents
	) {
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
	public IInvFilter<?> filter(
		final @NotNull Player player,
		final @NotNull IInvContents invContents
	) {
		return invContents.filter();
	}
}