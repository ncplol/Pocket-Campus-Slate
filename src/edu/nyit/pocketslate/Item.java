/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;
/**
 * <p>Title: Item.java</p>
 * <p>Description: Class for organizing items from xml data to insert or pull from the database.</p>
 * @author jasonscott
 *
 */
public class Item {

	public String id;
	public String title;
	public String link;
	public String description;
	public String content;
	public String category;
	public String imageUrl;
	public String pubDate;
	public String author;
	public String saved;

	/**
	 * Constructs an Item object which represents an article, staff member, club, or organization.
	 * @param strs - Array of Strings containing item fields in order of 
	 * id, title, link, description, content, category, imageUrl, pubDate, author, and saved
	 * @param s - saved flag
	 */
	public Item(String... strs) {
		id = strs[0];
		title = strs[1];
		link = strs[2];
		description = strs[3];
		content = strs[4];
		category = strs[5];
		imageUrl = strs[6];
		pubDate = strs[7];
		author = strs[8];
		saved = strs[9];
	}
	
	/**
	 *  Returns a String representing the Item
	 */
	public String toString() {
		return "Title:" + title + "\n" + "Link: " + link + "\n" + "Description: " +
				description + "\n" + "Content: " + content + "\n" + "Category: " + 
				category + "\n" + "ImageUrl: " + imageUrl + "\n" + "Publication Date: " +
				pubDate + "\n" + "Author: " + author + "Saved: " + saved;
	}
}
