package net.blazeindustries.actest;

import net.blazeindustries.actest.hud.ActiveModulesHud;
import net.blazeindustries.actest.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/**
 * AntiCheat Test Suite
 *
 * A client-side cheat simulator for exercising your own server's
 * anticheat. Every module here reproduces the actual client behaviour a
 * real cheat client would produce (real movement packets, real
 * interaction packets) rather than faking results locally, so your
 * anticheat has genuine signal to detect.
 *
 * DO NOT install this on servers you don't own or don't have explicit
 * permission to test against -- using it elsewhere is exactly what it
 * looks like: cheating, and will get you banned same as any other client.
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

		// VERIFY: HudRenderCallback's second parameter type has changed across
		// 1.20.x/1.21.x (float tickDelta vs. RenderTickCounter) -- if this
		// doesn't compile, let your IDE's error tell you the expected type
		// and adjust the lambda signature accordingly; the body doesn't use it.
		HudRenderCallback.EVENT.register((context, tickDelta) -> {
			hud.render(context, context.getScaledWindowWidth());
		});
	}
}
