package com.ujamaaonline.customer.models;

public class DaysModel {
    private boolean isEvent=false;
   public String dayName;
   public String date;

    public DaysModel(String dayName, String date) {
        this.dayName = dayName;
        this.date = date;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }
}
