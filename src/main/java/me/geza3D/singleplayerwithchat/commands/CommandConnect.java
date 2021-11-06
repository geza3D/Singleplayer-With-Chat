package me.geza3D.singleplayerwithchat.commands;

import me.geza3D.singleplayerwithchat.SingleplayerWithChat;
import me.geza3D.singleplayerwithchat.network.ProtocolThread;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandConnect extends CommandBase {

	@Override
	public String getName() {
		return "connect";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "connect <ip>";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(SingleplayerWithChat.connected) {
			sender.sendMessage(new TextComponentString("§cAlready connected to a server!"));
			return;	
		}
		if(!Minecraft.getMinecraft().isSingleplayer()) {
			sender.sendMessage(new TextComponentString("§cYou can only use this command in Singleplayer!"));
			return;
		}
		if(args.length > 0) {
			sender.sendMessage(new TextComponentString("§3Connecting to " + args[0] + "..."));
			String ip = args[0];
			String host;
			int port;
			if(ip.contains(":")) {
				String[] connectionDetails = ip.split(":");
				host = connectionDetails[0];
				try {
					port = Integer.parseInt(connectionDetails[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(new TextComponentString("§cInvalid port!"));
					return;
				}
			} else {
				host = ip;
				port = 25565;
			}
			new ProtocolThread(host, port).start();
		} else {
			sender.sendMessage(new TextComponentString("§cNo IP given!"));
		}
	}

}
