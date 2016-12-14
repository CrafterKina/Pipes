package jp.crafterkina.pipes.common.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;
import static jp.crafterkina.pipes.common.RegistryEntries.ITEM.merchant_phone;

@EventBusSubscriber(modid = MOD_ID)
public class ItemMerchantPhone extends Item{
    private ItemMerchantPhone(){
        setUnlocalizedName(MOD_ID + ".merchant_phone");
        setCreativeTab(CreativeTabs.MISC);
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
            addPropertyOverride(new ResourceLocation("registered"), (stack, world, entity) -> stack.getTagCompound() != null && stack.getTagCompound().hasUniqueId("Merchant") ? 1 : 0);
        }
    }

    @SubscribeEvent
    protected static void interactOnEntity(PlayerInteractEvent.EntityInteract event){
        if(event.getWorld().isRemote) return;
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = player.getHeldItem(event.getHand());
        Entity target = event.getTarget();
        if(!(target instanceof IMerchant)) return;
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) stack.setTagCompound(compound = new NBTTagCompound());
        if(compound.hasUniqueId("Merchant") && !player.isSneaking()) return;
        compound.setUniqueId("Merchant", target.getUniqueID());
        player.sendMessage(new TextComponentTranslation("mes." + merchant_phone.getUnlocalizedName() + ".register"));
        event.setCanceled(true);
    }

    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(new ItemMerchantPhone().setRegistryName(MOD_ID, "merchant_phone"));
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn){
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(!(worldIn instanceof WorldServer)) return new ActionResult<>(EnumActionResult.PASS, stack);
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) stack.setTagCompound(compound = new NBTTagCompound());
        if(!compound.hasUniqueId("Merchant")) return new ActionResult<>(EnumActionResult.PASS, stack);
        UUID uuid = compound.getUniqueId("Merchant");
        if(uuid == null) return new ActionResult<>(EnumActionResult.PASS, stack);
        Entity entity = ((WorldServer) worldIn).getEntityFromUuid(uuid);
        if(!(entity instanceof IMerchant)){
            stack.getTagCompound().removeTag("MerchantMost");
            stack.getTagCompound().removeTag("MerchantLeast");
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        ((IMerchant) entity).setCustomer(playerIn);
        playerIn.displayVillagerTradeGui((IMerchant) entity);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
        for(int i = 0; ; i++){
            if(!I18n.hasKey("tooltip." + merchant_phone.getUnlocalizedName() + "." + i)) break;
            tooltip.add(I18n.format("tooltip." + merchant_phone.getUnlocalizedName() + "." + i));
        }
        if(advanced){
            tooltip.add(String.format("Merchant UUID: %s", stack.getTagCompound() == null || !stack.getTagCompound().hasUniqueId("Merchant") ? "Not Registered" : stack.getTagCompound().getUniqueId("Merchant")));
        }
    }
}
