package net.blazeindustries.actest.module;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Base class for a single togglable "cheat" simulation.
 * Each module owns one keybind (registered under the "AntiCheat Test" category
 * so they don't collide with other mods) and gets a tick callback while enabled.
 *
 * These modules deliberately do NOT hide their behaviour from the server --
 * they exist to produce the same movement/interaction packets a real cheat
 * client would, so your anticheat has something real to catch. This is a
 * test tool for servers you own or have explicit permission to test.
 */
public abstract class TestModule {

	private final String name;
	private final KeyBinding keyBinding;
	private boolean enabled = false;

	protected TestModule(String name, int defaultKey) {
		this.name = name;
		this.keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.anticheat-test." + name.toLowerCase().replace(' ', '_'),
				InputUtil.Type.KEYSYM,
				defaultKey,
				"category.anticheat-test"
		));
	}

	/** Called once per client tick while the module is enabled. */
	protected abstract void onTick(MinecraftClient client);

	/** Called once when the module is toggled on. Override if needed. */
	protected void onEnable(MinecraftClient client) {}

	/** Called once when the module is toggled off. Override to clean up state. */
	protected void onDisable(MinecraftClient client) {}

	public final void tick(MinecraftClient client) {
		while (keyBinding.wasPressed()) {
			setEnabled(client, !enabled);
		}
		if (enabled && client.player != null) {
			onTick(client);
		}
	}

	public final void setEnabled(MinecraftClient client, boolean value) {
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

	protected static int defaultKey(int glfwKey) {
		return glfwKey;
	}
}
