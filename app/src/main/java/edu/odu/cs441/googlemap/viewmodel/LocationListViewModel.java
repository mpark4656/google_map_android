package edu.odu.cs441.googlemap.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import java.util.ArrayList;
import edu.odu.cs441.googlemap.database.AppDatabase;
import edu.odu.cs441.googlemap.model.IdentifiedLocation;
import edu.odu.cs441.googlemap.model.LocationLog;

public class LocationListViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LocationListViewModel(Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());
    }

    public ArrayList<IdentifiedLocation> findAllIdentifiedLocations() {
        ArrayList<IdentifiedLocation> result = new ArrayList<> ();

        for(IdentifiedLocation identifiedLocation : appDatabase.identifiedLocationDao().findAll()) {
            result.add(identifiedLocation);
        }

        return result;
    }

    public ArrayList<LocationLog> findAllLocationLogs() {
        ArrayList<LocationLog> result = new ArrayList<>();

        for(LocationLog locationLog : appDatabase.locationLogDao().findAll()) {
            result.add(locationLog);
        }

        return result;
    }

    public IdentifiedLocation findIdentifiedLocationByKey(String identifiedLocationKey) {
        return appDatabase.identifiedLocationDao().findByKey(identifiedLocationKey);
    }

    public void insertIdentifiedLocation(IdentifiedLocation identifiedLocation) {
        appDatabase.identifiedLocationDao().insertIdentifiedLocation(identifiedLocation);
    }

    public void insertLocationLog(LocationLog locationLog) {
        appDatabase.locationLogDao().insertLocationLog(locationLog);
    }

    public void deleteAllLocationLogs() {
        appDatabase.locationLogDao().deleteAll();
    }

    public void deleteAllIdentifiedLocations() {
        appDatabase.identifiedLocationDao().deleteAll();
    }
}
