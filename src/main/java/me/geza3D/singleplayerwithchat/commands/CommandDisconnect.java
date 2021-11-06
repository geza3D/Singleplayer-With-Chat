package me.geza3D.singleplayerwithchat.commands;

import me.geza3D.singleplayerwithchat.SingleplayerWithChat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandDisconnect extends CommandBase {

	@Override
	public String getName() {
		return "disconnect";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "disconnect";
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		 if(SingleplayerWithChat.connected) {
			 sender.sendMessage(new TextComponentString("§3Disconnected"));
			 SingleplayerWithChat.session.disconnect("Disconnected.");
		 } else {
			 sender.sendMessage(new TextComponentString("§cCannot disconnect. Not connected to a server!"));
		 }
	}

}
