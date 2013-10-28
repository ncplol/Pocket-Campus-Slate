package edu.nyit.pocketcampusslate;

public class Orgs {

    private String sname, lname, description, logo;


    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String toXML() {
        return "<org>\n" + "\t<sname>" + sname + ">/sname>\n" + "\t<lname>" + lname + ">/lname>\n" + "\t<description>" + description + ">/description>\n" + "</org>\n";
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

}
