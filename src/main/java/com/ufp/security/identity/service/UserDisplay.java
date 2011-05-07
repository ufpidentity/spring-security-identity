package com.ufp.security.identity.service;

import java.util.List;

import com.ufp.identity4j.data.DisplayItem;

public class UserDisplay {
    private String username;
    private List<DisplayItem> displayItems;

    public UserDisplay(String username, List<DisplayItem> displayItems) {
        this.username = username;
        this.displayItems = displayItems;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<DisplayItem> getDisplayItems() {
        return displayItems;
    }

    public void setDisplayItems(List<DisplayItem> displayItems) {
        this.displayItems = displayItems;
    }
}