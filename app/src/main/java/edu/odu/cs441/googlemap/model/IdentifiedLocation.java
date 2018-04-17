package edu.odu.cs441.googlemap.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = "identified_location",
indices = {@Index(value = {"name"}, unique = true)})
public class IdentifiedLocation {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "identified_location_key")
    private String identifiedLocationKey;

    private String name;

    public IdentifiedLocation() {
        identifiedLocationKey = UUID.randomUUID().toString();
    }

    public IdentifiedLocation(String name) {
        identifiedLocationKey = UUID.randomUUID().toString();
        this.name = name;
    }

    public @NonNull String getIdentifiedLocationKey() {
        return identifiedLocationKey;
    }

    public void setIdentifiedLocationKey(@NonNull String identifiedLocationKey) {
        this.identifiedLocationKey = identifiedLocationKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
