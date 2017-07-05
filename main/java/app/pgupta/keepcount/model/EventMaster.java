package app.pgupta.keepcount.model;

import app.pgupta.keepcount.util.Constants;

/**
 * Created by Prateek on 4/30/2016.
 */
public class EventMaster {

    private int id;
    private String title;
    private String createdOn;
    private String unit;
    private Constants.EventCategories eventCategory;
    private int status;

    public int gain_or_loss; //added on 11 sep 2016


    public EventMaster() {
    }

    public EventMaster(int id, String title, String unit, String createdOn, Constants.EventCategories eventCategory, int status, int gain_or_loss) {
        this.id = id;
        this.title = title;
        this.unit = unit;
        this.createdOn = createdOn;
        this.eventCategory = eventCategory;
        this.status = status;

        this.gain_or_loss = gain_or_loss;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Constants.EventCategories getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(Constants.EventCategories eventCategory) {
        this.eventCategory = eventCategory;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public int getGain_or_loss() {
        return gain_or_loss;
    }

    public void setGain_or_loss(int gain_or_loss) {
        this.gain_or_loss = gain_or_loss;
    }


}
