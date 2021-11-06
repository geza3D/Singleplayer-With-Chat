package me.geza3D.singleplayerwithchat.network;

import java.net.Proxy;
import java.util.Arrays;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import me.geza3D.singleplayerwithchat.SingleplayerWithChat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.text.TextComponentString;

public class ProtocolThread extends Thread {

	final String HOST;
	final int PORT;
	
	public ProtocolThread(String host, int port) {
		HOST = host;
		PORT = port;
		SingleplayerWithChat.connected = true;
	}

	@Override
	public void run() {
		if(SingleplayerWithChat.connected = status()) {
			login();
		}
	}
	
	private boolean status() {
        MinecraftProtocol protocol = new MinecraftProtocol(SubProtocol.STATUS);
        Client client = new Client(HOST, PORT, protocol, new TcpSessionFactory(Proxy.NO_PROXY));
        client.getSession().setFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        client.getSession().setFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY, new ServerInfoHandler() {
            @Override
            public void handle(com.github.steveice10.packetlib.Session session, ServerStatusInfo info) {
                System.out.println("Version: " + info.getVersionInfo().getVersionName() + ", " + info.getVersionInfo().getProtocolVersion());
                System.out.println("Player Count: " + info.getPlayerInfo().getOnlinePlayers() + " / " + info.getPlayerInfo().getMaxPlayers());
                System.out.println("Players: " + Arrays.toString(info.getPlayerInfo().getPlayers()));
                System.out.println("Description: " + info.getDescription().getFullText());
                System.out.println("Icon: " + info.getIcon());
            }
        });

        client.getSession().setFlag(MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY, new ServerPingTimeHandler() {
			@Override
			public void handle(com.github.steveice10.packetlib.Session session, long pingTime) {
				System.out.println("Server ping took " + pingTime + "ms");
			}
        });

        client.getSession().connect();
        while(client.getSession().isConnected()) {
            try {
                Thread.sleep(5);
                return true;
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§cCannot ping server! Connection cannot be established!"));
        return false;
    }
	
	private void login() {
		Session session = Minecraft.getMinecraft().getSession();
        MinecraftProtocol protocol = new MinecraftProtocol(new GameProfile(session.getProfile().getId(), session.getUsername()), session.getToken());
        
        final Client client = new Client(HOST, PORT, protocol, new TcpSessionFactory(Proxy.NO_PROXY));
        client.getSession().setFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        
        client.getSession().addListener(new ProtocolHandler());
	  
        client.getSession().connect();
    }
}
