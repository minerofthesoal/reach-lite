package net.blazeindustries.actest;

import net.blazeindustries.actest.hud.ActiveModulesHud;
import net.blazeindustries.actest.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/**
 * AntiCheat Test Suite (26.2 build) -- see the 1.21.11 build's copy of this
 * file for full module documentation. Architecture is identical; only
 * vanilla symbol names differ (Mojang mappings, since 26.2 has no Yarn).
 *
 * DO NOT install this on servers you don't own or don't have explicit
 * permission to test against.
 *
 * Default keybinds (rebindable in Options > Controls > AntiCheat Test):
 *   Numpad 1 - Fly
 *   Numpad 2 - Speed
 *   Numpad 3 - NoSlowdown (cobweb / soul sand)
 *   Numpad 4 - AutoClutch (auto bucket clutch)
 *   Numpad 5 - Combat (extended reach + killaura)
 */
public class AntiCheatTestClient implements ClientModInitializer {

	private final ModuleManager modules = new ModuleManager();

	@Override
	public void onInitializeClient() {
		ActiveModulesHud hud = new ActiveModulesHud(modules);

		ClientTickEvents.END_CLIENT_TICK.register(modules::tick);

		// VERIFY: HudRenderCallback's second parameter type -- pass whatever
		// type your IDE reports if this doesn't match (the body ignores it).
		HudRenderCallback.EVENT.register((context, tickDelta) -> {
			hud.render(context, context.guiWidth());
		});
	}
}
