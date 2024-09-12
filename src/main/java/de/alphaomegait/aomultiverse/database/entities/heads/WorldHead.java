package de.alphaomegait.aomultiverse.database.entities.heads;

import de.alphaomegait.aocore.database.entities.heads.AOHead;
import de.alphaomegait.aocore.database.entities.heads.EHeadFilter;
import jakarta.persistence.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Entity
public class WorldHead extends AOHead<WorldHead> {

	private static final String UUID = "b3dd3a65-933f-4a09-9701-d0d320d2b38b";
	private static final String TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgwZDMyOTVkM2Q5YWJkNjI3NzZhYmNiOGRhNzU2ZjI5OGE1NDVmZWU5NDk4YzRmNjlhMWMyYzc4NTI0YzgyNCJ9fX0=";
	
	public WorldHead() {
		super(
			"World",
			UUID,
			EHeadFilter.INVENTORY,
			TEXTURE
		);
	}
	
	@Override
	public List<String> getHeadDescription(@NotNull String s) {
		return Collections.emptyList();
	}
	
	@Override
	public String getHeadName(@NotNull String s) {
		return "";
	}
}