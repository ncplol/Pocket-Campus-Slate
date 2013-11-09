/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;
/**
 * <p>Title: Organization.java</p>
 * <p>Description: Class for organizing a club or organization from XML data.
 * Extends Item.java to handle rest of the club or organization fields.</p>
 * @author jasonscott
 *
 */
public class Organization extends Item{
	
	public String acronym;
	
	/**
	 * Constructs Organization object calling Item.java constructor, then assigning the additional field
	 * @param strs - Array of Strings containing item fields in order of 
	 * id, title, link, description, content, category, imageUrl, pubDate, author, saved, and acronym
	 */
	public Organization(String... strs) {
		super(strs);
		acronym = strs[9];
	}

}
