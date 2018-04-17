package edu.odu.cs441.googlemap.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import java.util.ArrayList;
import edu.odu.cs441.googlemap.dao.IdentifiedLocationDao;
import edu.odu.cs441.googlemap.dao.LocationLogDao;
import edu.odu.cs441.googlemap.model.IdentifiedLocation;
import edu.odu.cs441.googlemap.model.LocationLog;

@Database(entities = {IdentifiedLocation.class, LocationLog.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract IdentifiedLocationDao identifiedLocationDao();
    public abstract LocationLogDao locationLogDao();

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "location_database")
                            .allowMainThreadQueries().build();

        }
        return INSTANCE;
    }
}
