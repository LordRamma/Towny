package com.palmergames.bukkit.towny.object;

import java.util.Collection;

/**
 * Allows objects to contain townblocks to be accessed/manipulated. 
 * 
 * @author EdgarL
 * @author Shade
 * @author Suneet Tipirneni (Siris)
 */
public interface TownBlockHolder extends Nameable {

	/**
	 * Gets the unmodifiable collection of townblocks.
	 * 
	 * @return The townblocks this object contains.
	 */
	Collection<TownBlock> getTownBlocks();

	/**
	 * Checks whether object has townblock or not.
	 * 
	 * @param townBlock The townblock to check for.
	 * @return A boolean indicating if it was found or not.
	 */
	boolean hasTownBlock(TownBlock townBlock);
}
