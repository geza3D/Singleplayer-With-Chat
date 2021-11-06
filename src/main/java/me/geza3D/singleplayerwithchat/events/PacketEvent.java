package me.geza3D.singleplayerwithchat.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PacketEvent extends Event {

	private final Packet<?> packet;

	public PacketEvent(Packet<?> packet) {
		this.packet = packet;
	}

	@SuppressWarnings("unchecked")
	public <T extends Packet<?>> T getPacket() {
		return (T) packet;
	}

	public static class In extends PacketEvent {

		public In(Packet<?> packet) {
			super(packet);
		}

	}

	public static class Out extends PacketEvent {

		public Out(Packet<?> packet) {
			super(packet);
			// TODO Auto-generated constructor stub
		}

	}
}
