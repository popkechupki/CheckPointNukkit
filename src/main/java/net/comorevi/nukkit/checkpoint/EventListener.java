package net.comorevi.nukkit.checkpoint;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Location;

public class EventListener implements Listener {

	private CheckPoint mainClass;

	public EventListener(CheckPoint plugin) {
		this.mainClass = plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location location = event.getTo();
		location.y = location.y - 1;
		if (this.mainClass.getCheckPointBlock() != location.getLevelBlock().getId()) return;

		if (!this.mainClass.hasCheckPoint(event.getPlayer())) {
			this.mainClass.setPlayerCheckPoint(event.getPlayer());
		}
	}
}
