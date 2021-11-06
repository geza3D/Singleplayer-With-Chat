package me.geza3D.singleplayerwithchat.network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.geza3D.singleplayerwithchat.SingleplayerWithChat;
import me.geza3D.singleplayerwithchat.events.PacketEvent;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class ChannelHandler {

	boolean canConnect = true;
	
	@SubscribeEvent
	public void init(ClientConnectedToServerEvent e) {
		if(canConnect) {
			canConnect = false;
			
			ChannelPipeline pipeline = e.getManager().channel().pipeline();
			
			pipeline.addBefore("packet_handler", "listener_singleplayerwithchat", new ChannelDuplexHandler() {
				
				@Override
				public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
					if(msg instanceof Packet<?>) {
						PacketEvent.In event = new PacketEvent.In((Packet<?>) msg);
						SingleplayerWithChat.EVENTBUS.post(event);
						if(event.isCanceled()) return;
						msg = event.getPacket();
					}
					super.channelRead(ctx, msg);
				}
				
				@Override
				public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
					if(msg instanceof Packet<?>) {
						PacketEvent.Out event = new PacketEvent.Out((Packet<?>) msg);
						SingleplayerWithChat.EVENTBUS.post(event);
						if(event.isCanceled()) return;
						msg = event.getPacket();
					}
					super.write(ctx, msg, promise);
				}
			});
		}
	}
	
	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		canConnect = true;
	}
}
