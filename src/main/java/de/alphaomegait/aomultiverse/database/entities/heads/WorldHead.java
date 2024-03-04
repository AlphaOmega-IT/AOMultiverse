package de.alphaomegait.aomultiverse.database.entities.heads;

import de.alphaomegait.woocore.entities.WooHead;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class WorldHead extends WooHead {

	public WorldHead() {
		super(
			UUID.fromString("b3dd3a65-933f-4a09-9701-d0d320d2b38b"),
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgwZDMyOTVkM2Q5YWJkNjI3NzZhYmNiOGRhNzU2ZjI5OGE1NDVmZWU5NDk4YzRmNjlhMWMyYzc4NTI0YzgyNCJ9fX0=",
			"World",
			"World, Welt",
			"#cf9c11"
		);
	}

	@Override
	public void setNames() {
		this.getNames().put(
			Locale.GERMAN.toLanguageTag(),
			""
		);
		this.getNames().put(
			Locale.ENGLISH.toLanguageTag(),
			""
		);
	}

	@Override
	public void setLores() {
		this.getLores().put(
			Locale.GERMAN.toLanguageTag(),
			new ArrayList<>()
		);
		this.getLores().put(
			Locale.ENGLISH.toLanguageTag(),
			new ArrayList<>()
		);
	}
}