package net.blazeindustries.actest.module;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.InputConstants;

/**
 * Base class for a single togglable "cheat" simulation. See the 1.21.11
 * version of this file for full documentation -- logic is identical here,
 * only the vanilla class names differ (Mojang mappings, since 26.2 has no
 * Yarn build to fall back to).
 *
 * VERIFY: class is net.minecraft.client.KeyMapping in Mojang mappings
 * (Yarn's equivalent is net.minecraft.client.option.KeyBinding). Confirm
 * the package via your IDE if this doesn't resolve.
 */
public abstract class TestModule {

	private final String name;
	private final KeyMapping keyBinding;
	private boolean enabled = false;

	protected TestModule(String name, int defaultKey) {
		this.name = name;
		this.keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.anticheat-test." + name.toLowerCase().replace(' ', '_'),
				InputConstants.Type.KEYSYM,
				defaultKey,
				"category.anticheat-test"
		));
	}

	protected abstract void onTick(Minecraft client);

	protected void onEnable(Minecraft client) {}

	protected void onDisable(Minecraft client) {}

	public final void tick(Minecraft client) {
		// VERIFY: KeyMapping's "was pressed since last poll" method may be
		// named consumeClick() rather than wasPressed() in Mojang mappings.
		while (keyBinding.consumeClick()) {
			setEnabled(client, !enabled);
		}
		if (enabled && client.player != null) {
			onTick(client);
		}
	}

	public final void setEnabled(Minecraft client, boolean value) {
		if (value == enabled) return;
		enabled = value;
		if (enabled) onEnable(client);
		else onDisable(client);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getName() {
		return name;
	}
}
