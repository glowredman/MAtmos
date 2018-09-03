package eu.ha3.matmos.util;

import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class Tags implements Iterable<NBTTagCompound>, Iterator<NBTTagCompound> {
    private static final Iterable<NBTTagCompound> EMPTY = new Tags(null);

    private final NBTTagList list;
    private int index = 0;

    public static Iterable<NBTTagCompound> of(NBTTagList tags) {
        if (tags == null || tags.tagCount() == 0) return EMPTY;
        return new Tags(tags);
    }

    private Tags(NBTTagList tags) {
        list = tags;
    }

    @Override
    public Iterator<NBTTagCompound> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return list != null && index < list.tagCount();
    }

    @Override
    public NBTTagCompound next() {
        return list.getCompoundTagAt(index++);
    }
}
