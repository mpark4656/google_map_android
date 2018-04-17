package edu.odu.cs441.googlemap.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;
import edu.odu.cs441.googlemap.model.IdentifiedLocation;

@Dao
public interface IdentifiedLocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long insertIdentifiedLocation(IdentifiedLocation identifiedLocation);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long[] insertIdentifiedLocations(IdentifiedLocation ... identifiedLocations);

    @Update
    public int updateIdentifiedLocations(IdentifiedLocation ... identifiedLocations);

    @Delete
    public int deleteIdentifiedLocations(IdentifiedLocation ... identifiedLocations);

    @Query("SELECT * from identified_location")
    public IdentifiedLocation[] findAll();

    @Query("SELECT * from identified_location WHERE identified_location_key = :identifiedLocationKey LIMIT 1")
    public IdentifiedLocation findByKey(String identifiedLocationKey);

    @Query("SELECT * from identified_location WHERE name = :name LIMIT 1")
    public IdentifiedLocation findByName(String name);

    @Query("DELETE FROM identified_location WHERE identified_location_key = :identifiedLocationKey")
    public void deleteByKey(String identifiedLocationKey);

    @Query("DELETE FROM identified_location")
    public void deleteAll();
}
