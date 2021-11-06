package me.geza3D.singleplayerwithchat;

import com.github.steveice10.packetlib.Session;

import me.geza3D.singleplayerwithchat.commands.CommandConnect;
import me.geza3D.singleplayerwithchat.commands.CommandDisconnect;
import me.geza3D.singleplayerwithchat.network.ChannelHandler;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

@Mod(modid = SingleplayerWithChat.MODID, name = SingleplayerWithChat.NAME, version = SingleplayerWithChat.VERSION)
public class SingleplayerWithChat
{
    public static final String MODID = "singleplayerwithchat";
    public static final String NAME = "Singleplayer With Chat";
    public static final String VERSION = "1.0";
    public static final EventBus EVENTBUS = MinecraftForge.EVENT_BUS;
    public static boolean connected = false;
    public static Session session = null;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        EVENTBUS.register(new ChannelHandler());
        EVENTBUS.register(new ClientHandler());
        ClientCommandHandler.instance.registerCommand(new CommandConnect());
        ClientCommandHandler.instance.registerCommand(new CommandDisconnect());
    }
}
