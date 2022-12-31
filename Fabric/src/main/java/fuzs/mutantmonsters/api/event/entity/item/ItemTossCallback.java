package fuzs.mutantmonsters.api.event.entity.item;

import com.google.common.collect.Lists;
import fuzs.mutantmonsters.api.event.entity.living.CapturedDropsEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@FunctionalInterface
public interface ItemTossCallback {
    Event<ItemTossCallback> EVENT = EventFactory.createArrayBacked(ItemTossCallback.class, listeners -> (ItemEntity entityItem, Player player) -> {
        for (ItemTossCallback event : listeners) {
            if (event.onItemToss(entityItem, player).isPresent()) {
                return Optional.of(Unit.INSTANCE);
            }
        }
        return Optional.empty();
    });

    /**
     * Called when an item is tossed from the player inventory, either by pressing 'Q' or by clicking an item stack outside a container screen.
     *
     * <p>This callback can be cancelled so no item entity is added to the level, the item will be lost in that case at it has already been removed from the player inventory.
     *
     * @param entityItem item entity containing the item stack being tossed, not added to the level yet
     * @param player     the player tossing the item stack
     * @return is the stack allowed to be tossed, if cancelled no item will be added to the level, and it will be lost
     */
    Optional<Unit> onItemToss(ItemEntity entityItem, Player player);

    @ApiStatus.Internal
    @Nullable
    static ItemEntity onPlayerTossEvent(@NotNull Player player, @NotNull ItemStack item, boolean includeName) {
        ((CapturedDropsEntity) player).mutantmonsters$setCapturedDrops(Lists.newArrayList());
        ItemEntity ret = player.drop(item, false, includeName);
        ((CapturedDropsEntity) player).mutantmonsters$setCapturedDrops(null);
        if (ret == null) return null;
        Optional<Unit> result = EVENT.invoker().onItemToss(ret, player);
        if (result.isPresent()) return null;
        if (!player.level.isClientSide) player.getCommandSenderWorld().addFreshEntity(ret);
        return ret;
    }
}
