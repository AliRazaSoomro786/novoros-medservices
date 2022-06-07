package com.novoros.common;

public class Schedule {
    private boolean checked;
    private String date;
    private String description;
    private String name;
    private String time;

    private String key;

    public Schedule(String key, boolean checked, String date, String description, String name, String time) {
        this.checked = checked;
        this.key = key;
        this.date = date;
        this.description = description;
        this.name = name;
        this.time = time;
    }

    public boolean isChecked() {
        return checked;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "checked=" + checked +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
