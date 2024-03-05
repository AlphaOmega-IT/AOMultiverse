package de.alphaomegait.aomultiverse.commands.spawn;

import me.blvckbytes.bukkitevaluable.section.IPermissionNode;
import org.jetbrains.annotations.NotNull;

public enum EAOSpawnPermissionNode implements IPermissionNode {

	SET_SPAWN("aomultiverse.set-spawn", "aomultiverse.setspawn"),
	SPAWN("aomultiverse.spawn", "aomultiverse.spawn")
	;

	private final String internalName;
	private final String fallbackNode;

	EAOSpawnPermissionNode(
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