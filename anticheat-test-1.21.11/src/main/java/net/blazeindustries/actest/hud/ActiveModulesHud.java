package net.blazeindustries.actest.hud;

import net.blazeindustries.actest.module.ModuleManager;
import net.blazeindustries.actest.module.TestModule;
import net.minecraft.client.gui.DrawContext;

/**
 * Small always-on-top list of which test modules are currently active,
 * purely so the tester can see mod state at a glance -- this is client-side
 * only and has no bearing on what the server/anticheat actually sees.
 */
public class ActiveModulesHud {

	private final ModuleManager modules;

	public ActiveModulesHud(ModuleManager modules) {
		this.modules = modules;
	}

	public void render(DrawContext context, int screenWidth) {
		int x = screenWidth - 110;
		int y = 6;
		int color = 0xFF55FF55;

		context.drawText(
				net.minecraft.client.MinecraftClient.getInstance().textRenderer,
				"AntiCheat Test",
				x, y, 0xFFFFFFFF, true
		);
		y += 12;

		for (TestModule module : modules.all) {
			if (!module.isEnabled()) continue;
			context.drawText(
					net.minecraft.client.MinecraftClient.getInstance().textRenderer,
					module.getName(),
					x, y, color, true
			);
			y += 10;
		}
	}
}
