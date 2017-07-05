package app.pgupta.keepcount.model;

/**
 * Created by Prateek Gupta on 4/12/2016.
 */
public class Event extends EventMaster {

    private int recordID;
    private String description;
    private float value;
    private int eventID;
    private String timestamp;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class ValueGainLoss {
        public static final int GAIN = 0;
        public static final int LOSS = 1;
        public static final int NONE = 2;
    }
}
