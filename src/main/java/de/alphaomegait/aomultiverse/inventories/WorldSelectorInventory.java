package de.alphaomegait.aomultiverse.inventories;

import de.alphaomegait.ao18n.I18n;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.heads.WorldHead;
import de.alphaomegait.woocore.invhandler.AOCItem;
import de.alphaomegait.woocore.invhandler.AOInv;
import de.alphaomegait.woocore.invhandler.InvManager;
import de.alphaomegait.woocore.invhandler.content.InvContents;
import de.alphaomegait.woocore.invhandler.content.InvProvider;
import de.alphaomegait.woocore.utilities.HeadFactory;
import de.alphaomegait.woocore.utilities.ItemEditable;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorldSelectorInventory implements InvProvider {

	private final AOMultiverse aoMultiverse;
	private final List<World> worlds = new ArrayList<>();

	public WorldSelectorInventory(
		final @NotNull AOMultiverse aoMultiverse
	) {
		this.aoMultiverse = aoMultiverse;
		this.worlds.addAll(this.aoMultiverse.getServer().getWorlds());
		this.worlds.sort(Comparator.comparing(World::getName));
	}

	@Override
	public AOInv getInventory(final Player player) {
		return
			AOInv
				.builder()
				.id("aomultiverse_world_selector")
				.size(6, 9)
				.provider(this)
				.maxItemsPerPageAndMaxPages(
					13,
					this.worlds,
					false
				)
				.manager(
					new InvManager(this.aoMultiverse)
				)
				.title(
					new I18n.Builder(
						"aomultiverse.select-world-inventory-title",
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
		final AOCItem[] items = new AOCItem[invContents.inv().getMaxItemsPerPage() * invContents.inv().getMaxPages()];

		for (
			int i = 0; i < items.length; i++
		) {
			if (
				i >= this.worlds.size()
			) {
				items[i] = AOCItem.empty();
				continue;
			}

			items[i] = AOCItem.from(
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
				event -> {
					invContents.inv().close(player);
					// TODO teleport to world
				}
			);
		}

		
	}

	@Override
	public void update(
		final Player player,
		final InvContents invContents
	) {

	}
}