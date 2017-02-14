package mapmakingtools.helper;

import java.util.Objects;

public class VillagerHelper {
    public static String FixProfessionName(int professionId)
    {
        String reallyLongAssName = ((net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry<net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession>)
                net.minecraftforge.fml.common.registry.VillagerRegistry.instance().getRegistry()).getObjectById(professionId).getRegistryName().getResourcePath();
        if(Objects.equals(reallyLongAssName, "priest"))
            reallyLongAssName = "cleric";
        else if(Objects.equals(reallyLongAssName, "smith")){
            reallyLongAssName = "armor";
        }
        return "entity.Villager."+reallyLongAssName;
    }
}
