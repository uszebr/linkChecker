package com.bokarat;

public enum LinkType {
    ITERNAL("Internal"),
    EXTERNAL("External"),
    IMAGE("Image"),
    OTHER("Other");
    private String title;

    LinkType(String title) { // constructor
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }


    @Override
    public String toString() {
        return this.title + " link";
    }
}
