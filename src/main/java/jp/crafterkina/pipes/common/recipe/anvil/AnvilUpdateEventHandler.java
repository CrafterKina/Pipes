package jp.crafterkina.pipes.common.recipe.anvil;

import com.google.common.collect.Lists;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Optional;

/**
 * @author Kina
 */
public enum AnvilUpdateEventHandler{
    INSTANCE;
    private List<IAnvilRecipe> recipes = Lists.newArrayList();

    {
    }

    @SubscribeEvent
    protected void updateAnvilSlot(AnvilUpdateEvent event){
        Optional<IAnvilRecipe> recipe = recipes.parallelStream().filter(r -> r.matches(event)).findFirst();
        recipe.ifPresent(r -> r.process(event));
    }
}
