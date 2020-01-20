package com.palmergames.bukkit.towny.database;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.TownyRuntimeException;
import com.palmergames.bukkit.towny.object.Dirty;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.util.FileMgmt;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Lukas Mansour (Articdive)
 * @author Suneet Tipirneni (Siris)
 * @see Saveable
 */
public abstract class TownyDatabase {
	protected static final Logger DATABASE_LOGGER = LogManager.getLogger(TownyDatabase.class);
	
	protected TownyDatabase() {
		// Make sure we move the data around correctly
		// After the database update we store stuff in
		// Towny/database/flatfile/... instead of /Towny/data/...
		File conversionBackup = new File(Towny.getPlugin().getDataFolder() + File.separator + "conversionbackup");
		File oldDatabase = new File(Towny.getPlugin().getDataFolder() + File.separator + "data");
		File newDatabase = new File(Towny.getPlugin().getDataFolder() + File.separator + "database" + File.separator + "flatfile");
		if (oldDatabase.exists() && oldDatabase.isDirectory()) {
			try {
				FileMgmt.copyDirectory(oldDatabase, conversionBackup);
			} catch (IOException e) {
				DATABASE_LOGGER.log(Level.ERROR, "Failed to backup database before conversion.");
				DATABASE_LOGGER.log(Level.ERROR, e.getMessage(), e);
				throw new TownyRuntimeException("Failed to backup database before conversion.");
			}
			try {
				FileMgmt.copyDirectory(oldDatabase, newDatabase);
			} catch (IOException e) {
				e.printStackTrace();
				DATABASE_LOGGER.log(Level.ERROR, "Failed to copy database to its new location.");
				DATABASE_LOGGER.log(Level.ERROR, e.getMessage(), e);
				throw new TownyRuntimeException("Failed to copy database to its new location.");
			}
			FileMgmt.deleteFile(oldDatabase);
		}
	}
	
	/**
	 * Backup the database
	 *
	 * @return true, if the backup was successfully carried out, false if otherwise.
	 */
	public abstract boolean backup();
	
	// Residents
	
	/**
	 * Finds all {@link Resident} dataset identifiers.
	 *
	 * @return {@link List} dataset identifiers.
	 */
	@Nonnull
	public abstract Map<UUID, Resident> loadResidents();
	
	/**
	 * Loads a {@link Resident} dataset with its identifier.
	 *
	 * @param identifier {@link Resident} name
	 * @return true, if the {@link Resident} was successfully loaded, false if otherwise.
	 */
	public abstract boolean loadResident(String identifier);
	
	// Towns
	
	/**
	 * Finds all {@link Town} datasets identifiers.
	 *
	 * @return {@link List} dataset identifiers.
	 */
	@Nonnull
	public abstract Map<UUID, Town> loadTowns();
	
	/**
	 * Loads a {@link Town} dataset with its identifier.
	 *
	 * @param name {@link Town} name
	 * @return true, if the {@link Town} was successfully loaded, false if otherwise.
	 */
	public abstract boolean loadTown(String name);
	
	// Nations
	
	/**
	 * Finds all {@link Nation} dataset identifiers.
	 *
	 * @return {@link List} dataset identifiers.
	 */
	@Nonnull
	public abstract Map<UUID, Nation> loadNations();
	
	/**
	 * Loads a {@link Nation} dataset with its identifier.
	 *
	 * @param name {@link Nation} name
	 * @return true, if the {@link Nation} was successfully loaded, false if otherwise.
	 */
	public abstract boolean loadNation(String name);
	
	// Worlds
	
	/**
	 * Finds all {@link TownyWorld} dataset identifiers.
	 *
	 * @return {@link List} of Strings that correspond to dataset identifiers.
	 */
	@Nonnull
	public abstract Map<UUID, TownyWorld> loadWorlds();
	
	/**
	 * Loads a {@link TownyWorld} dataset with its identifier.
	 *
	 * @param name {@link TownyWorld} name
	 * @return true, if the {@link TownyWorld} was successfully loaded, false if otherwise.
	 */
	public abstract boolean loadTownyWorld(String name);
	
	// Townblocks
	
	/**
	 * Finds all {@link TownBlock} dataset identifiers.
	 *
	 * @return {@link List} dataset identifiers.
	 */
	@Nonnull
	public abstract Map<UUID, TownBlock> loadTownBlocks();
	
	/**
	 * Loads a {@link TownBlock} dataset with its identifier.
	 *
	 * @param x     {@link Integer} x-coordinate of the {@link TownBlock}
	 * @param z     {@link Integer} z-coordinate of the {@link TownBlock}
	 * @param world {@link TownyWorld} of the {@link TownBlock}
	 * @return true, if the {@link TownBlock} was successfully loaded, false if otherwise.
	 */
	public abstract boolean loadTownBlock(int x, int z, TownyWorld world);
	
	/**
	 * Saves any object conforming to the {@link Saveable} interface.
	 *
	 * @param obj The {@link Saveable} object to be saved.
	 * @return A boolean indicating if the save was successful.
	 */
	public abstract boolean save(Saveable obj);

	/**
	 * A wrapper function that allows the use of {@link TownyDatabase#save(Saveable...)} without
	 * the need for prior conversion to a primitive java array.
	 *
	 * @param objs The {@link Collection} of {@link Saveable} objects to save.
	 * @param <T> An object that implements {@link Saveable}.   
	 * @return Success or failure.
	 */
	public final <T extends Saveable> boolean save(Collection<T> objs) {
		// Create primitive array copy;
		Saveable[] copy = new Saveable[objs.size()];
		return save(objs.toArray(copy));
	}
	
	/**
	 * Deletes any object conforming to the {@link Saveable} interface.
	 *
	 * @param objs The {@link Saveable} object(s) to be deleted.
	 * @return A boolean indicating if the deletion was successful.
	 */
	public abstract boolean delete(Saveable... objs);

	/**
	 * A database function which saves a batch of saveable objects, in an
	 * efficient manner.
	 * 
	 * @param objs The Saveable objects to be save.
	 * @return A boolean indicating success or failure.
	 */
	public boolean save(Saveable... objs) {
		for (Saveable obj : objs) {
			// If the object implements a dirty conformance then use it.
			// We don't want to save objects that haven't been changed.
			// This is much more efficient.
			if (obj instanceof Dirty && ((Dirty) obj).isDirty()) {
				// Save
				save(obj);

				// Make sure to clean the object after saving.
				((Dirty) obj).setDirty(false);
				continue;
			}

			// Do not save an object that isn't dirty.
			if (obj instanceof Dirty) {
				continue;
			}

			// Otherwise just save without using dirty conformance (less efficient).
			save(obj);
		}
		
		return true;
	}
	
	// Regen
	public abstract void loadRegenQueue();
	
	public abstract void saveRegenQueue();
	
	public abstract void saveRegenData();
	
	public abstract void loadRegenData();
}