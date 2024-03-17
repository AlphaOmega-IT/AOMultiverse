package de.alphaomegait.aomultiverse.inventories;

import de.alphaomegait.ao18n.I18n;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.heads.WorldHead;
import de.alphaomegait.woocore.utilities.HeadFactory;
import de.alphaomegait.woocore.utilities.ItemEditable;
import de.alphaomegait.woocore.wooinv.IInvContents;
import de.alphaomegait.woocore.wooinv.IInventoryProvider;
import de.alphaomegait.woocore.wooinv.WooInventory;
import de.alphaomegait.woocore.wooinv.WooItem;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorldSelectorInventory implements IInventoryProvider {

	private final AOMultiverse aoMultiverse;
	private final List<World> worlds = new ArrayList<>();

	public WorldSelectorInventory(
		final @NotNull AOMultiverse aoMultiverse
	) {
		this.aoMultiverse = aoMultiverse;
		this.worlds.addAll(this.aoMultiverse.getServer().getWorlds());
		this.worlds.sort(Comparator.comparing(World::getName));
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
				i >= this.worlds.size()
			) {
				items[i] = WooItem.empty();
				continue;
			}

			items[i] = WooItem.from(
				new ItemEditable.Builder(
					new HeadFactory.Builder(
						WorldHead.class,
						player
					).build()
				).setName(
					new I18n.Builder(
						"aomultiverse.display.world-name",
					  player
					).setArgs(this.worlds.get(i).getName())
					 .build().displayMessageAsComponent()
				).setLore(
					new I18n.Builder(
						"aomultiverse.display.world-lore",
						player
					).build().displayMessages()
				).build(),
				"aomultiverse_world_selector",
				event -> {
					invContents.inv().close(player);
					// TODO teleport to world
				}
			);
		}

		
	}

	@Override
	public void update(
		final @NotNull Player player,
		final @NotNull IInvContents invContents
	) {
		//NOT NEEDED
	}
}