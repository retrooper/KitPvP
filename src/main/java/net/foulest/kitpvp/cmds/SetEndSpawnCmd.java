package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.utils.EndSpawn;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class SetEndSpawnCmd {

	private final EndSpawn endSpawn = EndSpawn.getInstance();

	@Command(name = "setendspawn", usage = "/setendspawn", description = "Sets the End spawn point.", permission = "kitpvp.setendspawn", inGameOnly = true)
	public void onCommand(CommandArgs args) {
		Player player = args.getPlayer();

		endSpawn.setLocation(player.getLocation());
		MiscUtils.messagePlayer(player, "&aEnd spawn has been set.");
	}
}
