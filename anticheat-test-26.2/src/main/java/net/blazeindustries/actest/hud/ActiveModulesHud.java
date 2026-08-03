package net.blazeindustries.actest.hud;

import net.blazeindustries.actest.module.ModuleManager;
import net.blazeindustries.actest.module.TestModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * VERIFY: DrawContext -> GuiGraphics, drawText -> drawString (Mojmap).
 */
public class ActiveModulesHud {

	private final ModuleManager modules;

	public ActiveModulesHud(ModuleManager modules) {
		this.modules = modules;
	}

	public void render(GuiGraphics context, int screenWidth) {
		int x = screenWidth - 110;
		int y = 6;
		int color = 0xFF55FF55;

		context.drawString(
				Minecraft.getInstance().font,
				"AntiCheat Test",
				x, y, 0xFFFFFFFF, true
		);
		y += 12;

		for (TestModule module : modules.all) {
			if (!module.isEnabled()) continue;
			context.drawString(
					Minecraft.getInstance().font,
					module.getName(),
					x, y, color, true
			);
			y += 10;
		}
	}
}
