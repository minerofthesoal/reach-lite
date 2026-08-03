package net.blazeindustries.actest.module;

import net.minecraft.client.Minecraft;

import java.util.List;

public class ModuleManager {

	public final FlyModule fly = new FlyModule();
	public final SpeedModule speed = new SpeedModule();
	public final NoSlowdownModule noSlowdown = new NoSlowdownModule();
	public final AutoClutchModule autoClutch = new AutoClutchModule();
	public final CombatTestModule combat = new CombatTestModule();

	public final List<TestModule> all = List.of(fly, speed, noSlowdown, autoClutch, combat);

	public void tick(Minecraft client) {
		for (TestModule module : all) {
			module.tick(client);
		}
	}
}
