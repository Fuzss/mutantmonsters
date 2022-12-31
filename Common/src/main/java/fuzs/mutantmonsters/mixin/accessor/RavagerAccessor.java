package fuzs.mutantmonsters.mixin.accessor;

import net.minecraft.world.entity.monster.Ravager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ravager.class)
public interface RavagerAccessor {

    @Accessor("stunnedTick")
    int mutantmonsters$getStunnedTick();

    @Accessor("stunnedTick")
    void mutantmonsters$setStunnedTick(int stunnedTick);
}
