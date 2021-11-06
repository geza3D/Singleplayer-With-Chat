package me.geza3D.singleplayerwithchat;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;

import me.geza3D.singleplayerwithchat.events.PacketEvent;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ClientHandler {

	@SubscribeEvent
	public void onDisconnect(ClientDisconnectionFromServerEvent e) {
		if(SingleplayerWithChat.session != null) {
			SingleplayerWithChat.session.disconnect("Disconnected.");
		}
	}
	
	@SubscribeEvent
	public void onPacketOut(PacketEvent.Out e) {
		if(SingleplayerWithChat.session != null) {
			if(e.getPacket() instanceof CPacketChatMessage) {
				CPacketChatMessage packet = e.getPacket();
				if(!packet.getMessage().startsWith("//")) {
					SingleplayerWithChat.session.send(new ClientChatPacket(packet.getMessage()));
					e.setCanceled(true);
				} else {
					packet.message = packet.message.substring(1);
				}
			}
		}
	}
}
