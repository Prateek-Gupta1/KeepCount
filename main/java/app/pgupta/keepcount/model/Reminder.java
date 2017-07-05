package app.pgupta.keepcount.model;

/**
 * Created by admin on 11/21/2016.
 */

public class Reminder {

    private int recordID;
    private long timestamp;
    private String uuid;
    private int eventID;

    public Reminder() {
    }

    public Reminder(int recordID, long timestamp, String uuid, int eventID) {
        this.recordID = recordID;
        this.timestamp = timestamp;
        this.uuid = uuid;
        this.eventID = eventID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
}
