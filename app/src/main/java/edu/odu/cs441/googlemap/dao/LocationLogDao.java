package edu.odu.cs441.googlemap.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;
import edu.odu.cs441.googlemap.model.LocationLog;

@Dao
public interface LocationLogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long insertLocationLog(LocationLog locationLog);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long[] insertLocationLogs(LocationLog ... locationLogs);

    @Update
    public int updateLocationLogs(LocationLog ... locationLogs);

    @Delete
    public int deleteLocationLogs(LocationLog ... locationLogs);

    @Query("SELECT * from location_log")
    public LocationLog[] findAll();

    @Query("SELECT * from location_log WHERE location_log_key = :locationLogKey LIMIT 1")
    public LocationLog findByKey(String locationLogKey);

    @Query("SELECT * from location_log WHERE identified_location_key = :identifiedLocationKey LIMIT 1")
    public LocationLog findByIdentifiedLocationKey(String identifiedLocationKey);

    @Query("DELETE FROM location_log WHERE location_log_key = :locationLogKey")
    public void deleteByKey(String locationLogKey);

    @Query("DELETE FROM location_log")
    public void deleteAll();
}
