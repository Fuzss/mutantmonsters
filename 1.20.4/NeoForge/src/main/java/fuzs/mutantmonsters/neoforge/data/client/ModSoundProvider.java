package fuzs.mutantmonsters.neoforge.data.client;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.neoforge.api.data.v2.client.AbstractSoundDefinitionProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.ForgeDataProviderContext;
import net.neoforged.neoforge.common.data.SoundDefinition;

public class ModSoundProvider extends AbstractSoundDefinitionProvider {

    public ModSoundProvider(ForgeDataProviderContext context) {
        super(context);
    }

    @Override
    public void addSoundDefinitions() {
        this.add(ModRegistry.ENTITY_CREEPER_MINION_AMBIENT_SOUND_EVENT.value(), sound("entity.creeper.hurt", SoundDefinition.SoundType.EVENT).volume(0.6));
    }
}
