package me.geza3D.singleplayerwithchat.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.data.game.entity.player.CombatState;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerCombatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.geza3D.singleplayerwithchat.SingleplayerWithChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;

public class ProtocolHandler extends SessionAdapter {

	@Override
	public void connected(ConnectedEvent event) {
		SingleplayerWithChat.session = event.getSession();
		Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§3Successfully connected to " + event.getSession().getHost() + ":" + event.getSession().getPort()));
		super.connected(event);
	}
	
	@Override
	public void disconnected(DisconnectedEvent event) {
		SingleplayerWithChat.connected = false;
		SingleplayerWithChat.session = null;
		Minecraft.getMinecraft().ingameGUI.getTabList().resetFooterHeader();
		Minecraft.getMinecraft().getConnection().playerInfoMap.clear();
		Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§3Disconnected from " + event.getSession().getHost() + ":" + event.getSession().getPort() + " due to: " + event.getReason()));
		super.disconnected(event);
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if(event.getPacket() instanceof ServerChatPacket) {
			ServerChatPacket packet = event.getPacket();
			ITextComponent msg = ITextComponent.Serializer.jsonToComponent(packet.getMessage().toJsonString());
			Minecraft.getMinecraft().player.sendMessage(msg);
		}
		
		if(event.getPacket() instanceof ServerCombatPacket) {
			ServerCombatPacket packet = event.getPacket();
			if(packet.getCombatState() == CombatState.ENTITY_DEAD) {
				event.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
			}
		}
		
		if(event.getPacket() instanceof ServerPlayerListDataPacket) {
			ServerPlayerListDataPacket packet = event.getPacket();
			mc.ingameGUI.getTabList().setHeader(ITextComponent.Serializer.jsonToComponent(packet.getHeader().toJsonString()));
			mc.ingameGUI.getTabList().setFooter(ITextComponent.Serializer.jsonToComponent(packet.getFooter().toJsonString()));
		}
		
		if(event.getPacket() instanceof ServerPlayerListEntryPacket) {
			ServerPlayerListEntryPacket packet = event.getPacket();
			handlePlayerListEntryPacket(packet, mc);
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private void handlePlayerListEntryPacket(ServerPlayerListEntryPacket packet, Minecraft mc) {
		Map<UUID, NetworkPlayerInfo> playerInfoMap = mc.getConnection().playerInfoMap;
		for (PlayerListEntry spacketplayerlistitem$addplayerdata : packet.getEntries())
        {
            if (packet.getAction() == PlayerListEntryAction.REMOVE_PLAYER)
            {
                playerInfoMap.remove(spacketplayerlistitem$addplayerdata.getProfile().getId());
            }
            else
            {
                NetworkPlayerInfo networkplayerinfo = playerInfoMap.get(spacketplayerlistitem$addplayerdata.getProfile().getId());

                if (packet.getAction() == PlayerListEntryAction.ADD_PLAYER)
                {
                	GameProfile gp = new GameProfile(spacketplayerlistitem$addplayerdata.getProfile().getId(), spacketplayerlistitem$addplayerdata.getProfile().getName());
                	try {
                        URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + gp.getId() + "?unsigned=false");
                        InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
                        JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
                        String texture = textureProperty.get("value").getAsString();
                        String signature = textureProperty.get("signature").getAsString();

                        gp.getProperties().put("textures", new Property("textures", texture, signature));
                    } catch (IOException e) {}
                	networkplayerinfo = new NetworkPlayerInfo(gp);
                    playerInfoMap.put(networkplayerinfo.getGameProfile().getId(), networkplayerinfo);
                }

                if (networkplayerinfo != null)
                {
                    switch (packet.getAction())
                    {
                        case ADD_PLAYER:
                            networkplayerinfo.setGameType(GameType.values()[spacketplayerlistitem$addplayerdata.getGameMode().ordinal()+1]);
                            networkplayerinfo.setResponseTime(spacketplayerlistitem$addplayerdata.getPing());
                            break;
                        case UPDATE_GAMEMODE:
                        	networkplayerinfo.setGameType(GameType.values()[spacketplayerlistitem$addplayerdata.getGameMode().ordinal()+1]);
                            break;
                        case UPDATE_LATENCY:
                        	networkplayerinfo.setResponseTime(spacketplayerlistitem$addplayerdata.getPing());
                            break;
                        case UPDATE_DISPLAY_NAME:
                        	if(spacketplayerlistitem$addplayerdata.getDisplayName() != null) {
                        		networkplayerinfo.setDisplayName(ITextComponent.Serializer.jsonToComponent(spacketplayerlistitem$addplayerdata.getDisplayName().toJsonString()));
                        	} else {
                        		networkplayerinfo.setDisplayName(null);
                        	}
                    }
                }
            }
        }
	}
}
