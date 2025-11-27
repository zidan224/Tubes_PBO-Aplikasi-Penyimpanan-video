package com.myplaylist.model;

public class Video {
    private int id;
    private String title;
    private String creator;
    private String category;
    private int year;
    private String genre;
    private double duration;
    private String thumbnailPath; // <--- Field Baru

    // Constructor Lengkap (dengan thumbnail)
    public Video(int id, String title, String creator, String category, int year, String genre, double duration, String thumbnailPath) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.category = category;
        this.year = year;
        this.genre = genre;
        this.duration = duration;
        this.thumbnailPath = thumbnailPath;
    }

    // Constructor Lama (agar kode lain tidak error, default null)
    public Video(int id, String title, String creator, String category, int year, String genre, double duration) {
        this(id, title, creator, category, year, genre, duration, null);
    }

    // Getter Setter Baru
    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }

    // Getter Setter Lama
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCreator() { return creator; }
    public String getCategory() { return category; }
    public int getYear() { return year; }
    public String getGenre() { return genre; }
    public double getDuration() { return duration; }
    
    public void setTitle(String title) { this.title = title; }
    public void setCreator(String creator) { this.creator = creator; }
    public void setCategory(String category) { this.category = category; }
    public void setYear(int year) { this.year = year; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setDuration(double duration) { this.duration = duration; }
}