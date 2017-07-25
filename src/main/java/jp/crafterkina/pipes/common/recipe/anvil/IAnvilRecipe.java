package jp.crafterkina.pipes.common.recipe.anvil;

import net.minecraftforge.event.AnvilUpdateEvent;

/**
 * @author Kina
 */
public interface IAnvilRecipe{
    boolean matches(AnvilUpdateEvent event);

    void process(AnvilUpdateEvent event);
}
