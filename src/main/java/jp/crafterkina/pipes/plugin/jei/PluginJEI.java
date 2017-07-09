package jp.crafterkina.pipes.plugin.jei;

import jp.crafterkina.pipes.common.RegistryEntries;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;

import javax.annotation.Nonnull;

/**
 * Created by Kina on 2016/12/25.
 */
@JEIPlugin
public class PluginJEI implements IModPlugin{
    @Override
    public void registerItemSubtypes(@Nonnull ISubtypeRegistry subtypeRegistry){
        subtypeRegistry.useNbtForSubtypes(RegistryEntries.ITEM.pipe);
    }

    @Override
    public void register(@Nonnull IModRegistry registry){
    }
}
