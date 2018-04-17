package edu.odu.cs441.googlemap.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;
import edu.odu.cs441.googlemap.converter.DateConverter;

@Entity(tableName = "location_log",
        foreignKeys = @ForeignKey(entity = IdentifiedLocation.class,
        parentColumns = "identified_location_key",
        childColumns = "identified_location_key",
        onDelete = ForeignKey.CASCADE))
public class LocationLog {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "location_log_key")
    private String locationLogKey;

    @ColumnInfo(name = "identified_location_key")
    private String identifiedLocationKey;

    private double latitude;
    private double longitude;

    @TypeConverters({DateConverter.class})
    private Date timestamp;
    private String address;

    public LocationLog() {
        locationLogKey = UUID.randomUUID().toString();
    }

    public LocationLog(double latitude, double longitude, Date timestamp, String address) {
        locationLogKey = UUID.randomUUID().toString();
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.address = address;
    }

    public @NonNull String getLocationLogKey() {
        return locationLogKey;
    }

    public void setLocationLogKey(@NonNull String locationLogKey) {
        this.locationLogKey = locationLogKey;
    }

    public String getIdentifiedLocationKey() {
        return identifiedLocationKey;
    }

    public void setIdentifiedLocationKey(String identifiedLocationKey) {
        this.identifiedLocationKey = identifiedLocationKey;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
