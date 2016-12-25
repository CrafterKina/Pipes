package jp.crafterkina.pipes.plugin.jei.crafting;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

import javax.annotation.Nonnull;

/**
 * Created by Kina on 2016/12/25.
 */
public class CoverPipeWithWoolRecipeHandler implements IRecipeHandler<CoverPipeWithCarpetRecipeWrapper>{
    @Nonnull
    @Override
    public Class<CoverPipeWithCarpetRecipeWrapper> getRecipeClass(){
        return CoverPipeWithCarpetRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull CoverPipeWithCarpetRecipeWrapper recipe){
        return VanillaRecipeCategoryUid.CRAFTING;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull CoverPipeWithCarpetRecipeWrapper recipe){
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull CoverPipeWithCarpetRecipeWrapper recipe){
        return true;
    }
}
