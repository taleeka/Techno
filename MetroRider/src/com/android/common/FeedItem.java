package com.android.common;

public class FeedItem {
    private int id;
    private String name, status, image, Pic, type, url;
 
    public FeedItem() {
    }
 
    public FeedItem(int id, String name,String status,String type,String url,String Pic) {
        super();
        this.id = id;
        this.name = name;
        this.status = status;
        this.type=type;
        this.url=url;
        this.Pic=Pic;
    }
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getType() {
        return type;
    }
 
    public void setType(String type) {
        this.type = type;
    }
    public String getUrl() {
        return url;
    }
 
    public void setUrl(String url) {
        this.url = url;
    }
 
    public String getPic() {
        return Pic;
    }
 
    public void setPic(String Pic) {
        this.Pic = Pic;
    }
   
}