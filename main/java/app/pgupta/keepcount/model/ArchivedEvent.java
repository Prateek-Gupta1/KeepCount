package app.pgupta.keepcount.model;

import java.util.LinkedList;

/**
 * Created by admin on 5/27/2016.
 */
public class ArchivedEvent {

    private String title;
    private int id;
    private int count;
    private double netValue;
    public LinkedList<Event> events;
    public int flag ;

    public ArchivedEvent(String title, int id) {
        this.title = title;
        this.id = id;
        flag = -1;
        events = new LinkedList<>();
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

    public LinkedList<Event> getEvents() {
        return events;
    }

    public void setEvents(LinkedList<Event> events) {
        this.events = events;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getNetValue() {
        return netValue;
    }

    public void setNetValue(double netValue) {
        this.netValue = netValue;
    }

    public int isFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
