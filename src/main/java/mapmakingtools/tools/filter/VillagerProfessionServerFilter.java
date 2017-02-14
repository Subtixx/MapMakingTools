package mapmakingtools.tools.filter;

import mapmakingtools.api.interfaces.IFilterServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class VillagerProfessionServerFilter extends IFilterServer {

	@Override
	public boolean isApplicable(EntityPlayer player, Entity entity) {
		return entity instanceof EntityVillager;
	}
}
