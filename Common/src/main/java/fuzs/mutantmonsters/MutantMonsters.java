package fuzs.mutantmonsters;

import fuzs.puzzleslib.core.ModConstructor;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutantMonsters implements ModConstructor {
    public static final String MOD_ID = "mutantmonsters";
    public static final String MOD_NAME = "Mutant Monsters";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

//    @SuppressWarnings("Convert2MethodRef")
//    public static final ConfigHolder CONFIG = CommonFactories.INSTANCE.clientConfig(ClientConfig.class, () -> new ClientConfig());

    @Override
    public void onConstructMod() {
//        CONFIG.bakeConfigs(MOD_ID);
//        ModRegistry.touch();
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
