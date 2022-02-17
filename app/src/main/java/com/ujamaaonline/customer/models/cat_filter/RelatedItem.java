package com.ujamaaonline.customer.models.cat_filter;

public class RelatedItem {
    private boolean isSelected=false;
    private String  name;


    public RelatedItem(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
