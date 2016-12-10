package fr.gstraymond.search.model;


import java.util.Date;
import java.util.List;

public class Deck {
    private int id;
    private Date timestamp;
    private String name;
    private List<String> colors;
    private String format;

    public Deck() {
    }

    public Deck(int id, Date timestamp, String name, List<String> colors, String format) {
        this.id = id;
        this.timestamp = timestamp;
        this.name = name;
        this.colors = colors;
        this.format = format;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
