package com.pg85.otg.util.materials;

import com.pg85.otg.common.LocalMaterialData;
import com.pg85.otg.common.LocalWorld;

//TODO: This seems really inefficient and riddiculously overcomplicated, burn with fire.
//Looks like this is optimised mainly for use with blockchecks and BOfunctions, resources like oregen also use it though,
//they shouldn't need any other functionality than containing a list of materials.
public class MaterialSetEntry
{
    public LocalMaterialData material;
    private final boolean includesBlockData;

    MaterialSetEntry(LocalMaterialData material, boolean includesBlockData)
    {
        this.material = material;
        this.includesBlockData = includesBlockData;
    }

    @Override
    public boolean equals(Object other)
    {
        // Uses hashCode, as it is guaranteed to be unique for this class
        if (other instanceof MaterialSetEntry)
        {
            return other.hashCode() == hashCode();
        }
        return false;
    }

    /**
     * Gets the hashCode of this entry, which is equal to either
     * {@link LocalMaterialData#hashCode()} or
     * {@link LocalMaterialData#hashCodeWithoutBlockData()}. This means that
     * the hashCode is unique.
     *
     * @return The unique hashCode.
     */
    @Override
    public int hashCode()
    {
    	return material.hashCode();
    }

    public void parseForWorld(LocalWorld world)
    {
    	material.parseForWorld(world);
    }

    @Override
    public String toString()
    {
        return material.toString();
    }

    /**
     * Rotates this check 90 degrees. If block data was ignored in this check,
     * it will still be ignored, otherwise the block data will be rotated too.
     * 
     * @return The rotated check.
     */
    MaterialSetEntry rotate()
    {
        if (!includesBlockData)
        {
            // Don't rotate block data
            return new MaterialSetEntry(material, false);
        } else {
            // Actually rotate block data, to maintain check correctness
            return new MaterialSetEntry(material.rotate(), true);
        }
    }
}
