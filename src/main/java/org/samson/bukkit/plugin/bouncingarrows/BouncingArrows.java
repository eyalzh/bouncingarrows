package org.samson.bukkit.plugin.bouncingarrows;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BouncingArrows extends JavaPlugin {

	private final BouncingArrowsEventListener eventListener = new BouncingArrowsEventListener(this);

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() { 
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(eventListener, this);
	}
	
}
