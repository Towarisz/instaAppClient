package api.model;

import java.util.ArrayList;

public class PhotoModel {
    public long id;
    public long album;
    public long author;
    public String originalName;
    public String url;
    public String lastChange;
    public ArrayList<History> history;
    public ArrayList<Object> tags;
}
