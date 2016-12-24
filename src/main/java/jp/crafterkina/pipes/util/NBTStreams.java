package jp.crafterkina.pipes.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Kina on 2016/12/19.
 */
public enum NBTStreams{
    ;

    public static <T extends NBTTagCompound> Collector<T, ?, NBTTagList> toNBTList(){
        return Collector.of(NBTTagList::new, NBTTagList::appendTag, (l, r) -> {
            IntStream.range(0, r.tagCount()).mapToObj(r::get).filter(Objects::nonNull).forEach(l::appendTag);
            return l;
        }, i -> i);
    }

    public static Stream<NBTTagCompound> nbtListStream(NBTTagList list){
        return list.hasNoTags() ? Stream.empty() : IntStream.range(0, list.tagCount()).mapToObj(list::getCompoundTagAt).filter(Objects::nonNull);
    }
}