package com.mekaar.presensiku;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampData {
    private String locationName, entityName, locationDesc, uid;
    private long timeStamp, currentDate ;
    private String formattedDate;

    public TimestampData() {
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(long currentDate) {
        this.currentDate = currentDate;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public TimestampData(String locationName, String entityName, String locationDesc, String uid, long timeStamp, long currentDate) {
        this.locationName = locationName;
        this.entityName = entityName;
        this.locationDesc = locationDesc;
        this.uid = uid;
        this.timeStamp = timeStamp;
        this.currentDate = currentDate;
    }
}

