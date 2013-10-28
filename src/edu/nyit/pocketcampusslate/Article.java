package edu.nyit.pocketcampusslate;

public class Article {

    private String title;
    private String description;//Not needed?
    private String img;
    private String pubDate;
    private String url;//Not needed?
    private String encodedContent;
    private String category; // Needs to be implemented

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String string) {
        this.url = string;
    }

    public void setDescription(String description) {//This method logic should probably go into the encoded content method.
        this.description = description;

        //parse description for any image or video links
        if (description.contains("<img ")) {
            String img = description.substring(description.indexOf("<img "));
            String cleanUp = img.substring(0, img.indexOf(">") + 1);
            img = img.substring(img.indexOf("src=") + 5);
            int indexOf = img.indexOf("'");
            if (indexOf == -1) {
                indexOf = img.indexOf("\"");
            }
            img = img.substring(0, indexOf);

            setImg(img);

            this.description = this.description.replace(cleanUp, "");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setEncodedContent(String encodedContent) {
        this.encodedContent = encodedContent;
    }

    public String getEncodedContent() {
        return encodedContent;
    }

    void setImg(String imgLink) {
        this.img = imgLink;
    }

    public String getImg() {
        return img;
    }

}
