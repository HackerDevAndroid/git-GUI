package model;

/**
 * Created by MyPC on 08/09/2017.
 */

public class Personnel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public int getDayWorking() {
        return dayWorking;
    }

    public void setDayWorking(int dayWorking) {
        this.dayWorking = dayWorking;
    }

    public Personnel(String name, String dateStart, int dayWorking) {
        this.name = name;
        this.dateStart = dateStart;
        this.dayWorking = dayWorking;
    }

    public Personnel() {
    }

    private String name;
    private String dateStart;
    private int dayWorking;

    @Override
    public String toString() {
        return this.name+"\nDay working at ["+this.dateStart+"]"+"\n Day Working = "+this.dayWorking;
    }
}
