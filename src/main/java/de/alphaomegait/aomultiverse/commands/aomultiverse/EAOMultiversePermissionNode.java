package de.alphaomegait.aomultiverse.commands.aomultiverse;

import me.blvckbytes.bukkitevaluable.section.IPermissionNode;
import org.jetbrains.annotations.NotNull;

public enum EAOMultiversePermissionNode implements IPermissionNode {

	AOMULTIVERSE("aomultiverse.command", "aomultiverse.command"),
	AOMULTIVERSE_CREATE_WORLD("aomultiverse.create-world", "aomultiverse.create"),
	AOMULTIVERSE_EDIT("aomultiverse.edit-world", "aomultiverse.edit"),
	AOMULTIVERSE_FORCE_WORLD("aomultiverse.force-world", "aomultiverse.force"),
	AOMULTIVERSE_DELETE_WORLD("aomultiverse.delete-world", "aomultiverse.delete"),
	AOMULTIVERSE_TELEPORT("aomultiverse.teleport", "aomultiverse.teleport"),
	;

	private final String internalName;
	private final String fallbackNode;

	EAOMultiversePermissionNode(
		final @NotNull String internalName,
		final @NotNull String fallbackNode
	) {
		this.internalName = internalName;
		this.fallbackNode = fallbackNode;
	}

	@Override
	public String getInternalName() {
		return this.internalName;
	}

	@Override
	public String getFallbackNode() {
		return this.fallbackNode;
	}
}