package fuzs.mutantmonsters.api.event.entity.living;

import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Internal
public interface CapturedDropsEntity {

    Collection<ItemEntity> mutantmonsters$setCapturedDrops(Collection<ItemEntity> collection);

    Collection<ItemEntity> mutantmonsters$getCapturedDrops();
}
