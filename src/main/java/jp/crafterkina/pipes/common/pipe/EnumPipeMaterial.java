package jp.crafterkina.pipes.common.pipe;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

import static jp.crafterkina.pipes.common.pipe.EnumPipeMaterial.TextureType.GRADATION;
import static jp.crafterkina.pipes.common.pipe.EnumPipeMaterial.TextureType.NOISE;

/**
 * Created by Kina on 2016/12/24.
 */
@AllArgsConstructor
public enum EnumPipeMaterial{
    WOOD(0x9F844D, NOISE, 1, 1 / 40d, 1 / 20d, 1 / 7d),
    STONE(0x686868, NOISE, 8, 1 / 35d, 1 / 25d, 1 / 5d),
    IRON(0xE6E6E6, GRADATION, 8, 1 / 30d, 1 / 10d, 1 / 2d),
    DIAMOND(0x69DFDA, GRADATION, 16, 1 / 28d, 1 / 2d, 3 / 4d),
    GOLD(0xFFF849, GRADATION, 2, 1 / 25d, 1 / 2d, 5 / 4d),;
    public static final ImmutableList<EnumPipeMaterial> VALUES = ImmutableList.copyOf(values());

    public final int COLOR;
    public final TextureType TYPE;
    public final int STACK_ACCEPTANCE_LIMIT;
    public final double BASE_SPEED;
    public final double LIMIT_SPEED;
    public final double ACCELERATION_COEFFICIENT;

    public enum TextureType implements IStringSerializable{
        NOISE,
        GRADATION;

        @Nonnull
        @Override
        public String getName(){
            return name().toLowerCase();
        }
    }
}
