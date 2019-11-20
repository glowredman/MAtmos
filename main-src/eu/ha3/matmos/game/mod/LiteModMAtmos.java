package eu.ha3.matmos.game.mod;

import java.util.List;

import com.mumfrey.liteloader.PacketHandler;

import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.haddon.litemod.TempLiteKey;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

/*
--filenotes-placeholder
*/

public class LiteModMAtmos extends TempLiteKey implements PacketHandler
{
	public LiteModMAtmos()
	{
		super(new MAtMod());
	}

	@Override
	public List<Class<? extends Packet>> getHandledPackets()
	{
		return ((MAtMod)haddon).getHandledPackets();
	}

	@Override
	public boolean handlePacket(INetHandler netHandler, Packet packet)
	{
		return ((MAtMod)haddon).handlePacket(netHandler, packet);
	}
}
