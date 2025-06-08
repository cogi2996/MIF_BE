package com.mif.movieInsideForum.Module.User;

public class UserMonthCount {
    private int month;
    private int count;

    public UserMonthCount() {}
    public UserMonthCount(int month, int count) {
        this.month = month;
        this.count = count;
    }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
} 