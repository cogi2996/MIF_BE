package com.mif.movieInsideForum.Module.Post.repository;

public class GroupPostMonthCount {
    private int month;
    private int count;

    public GroupPostMonthCount() {}
    public GroupPostMonthCount(int month, int count) {
        this.month = month;
        this.count = count;
    }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
} 