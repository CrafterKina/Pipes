package jp.crafterkina.pipes.plugin.jei;

import jp.crafterkina.pipes.common.RegistryEntries;
import jp.crafterkina.pipes.plugin.jei.crafting.CoverPipeWithCarpetRecipeHandler;
import jp.crafterkina.pipes.plugin.jei.crafting.CoverPipeWithCarpetRecipeWrapper;
import jp.crafterkina.pipes.plugin.jei.crafting.CoverPipeWithWoolRecipeHandler;
import jp.crafterkina.pipes.plugin.jei.crafting.CoverPipeWithWoolRecipeWrapper;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;

/**
 * Created by Kina on 2016/12/25.
 */
@JEIPlugin
public class PluginJEI extends BlankModPlugin{
    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry){
        subtypeRegistry.useNbtForSubtypes(RegistryEntries.ITEM.pipe);
    }

    @Override
    public void register(IModRegistry registry){
        registry.addRecipeHandlers(
                new CoverPipeWithCarpetRecipeHandler(),
                new CoverPipeWithWoolRecipeHandler()
        );
        registry.addRecipes(CoverPipeWithCarpetRecipeWrapper.getRecipes());
        registry.addRecipes(CoverPipeWithWoolRecipeWrapper.getRecipes());
    }
}
