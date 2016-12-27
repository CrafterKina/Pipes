package jp.crafterkina.pipes.plugin.jei.crafting;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

import javax.annotation.Nonnull;

/**
 * Created by Kina on 2016/12/25.
 */
public class CoverPipeWithWoolRecipeHandler implements IRecipeHandler<CoverPipeWithWoolRecipeWrapper>{
    @Nonnull
    @Override
    public Class<CoverPipeWithWoolRecipeWrapper> getRecipeClass(){
        return CoverPipeWithWoolRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull CoverPipeWithWoolRecipeWrapper recipe){
        return VanillaRecipeCategoryUid.CRAFTING;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull CoverPipeWithWoolRecipeWrapper recipe){
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull CoverPipeWithWoolRecipeWrapper recipe){
        return true;
    }
}
