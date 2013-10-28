package edu.nyit.pocketcampusslate;

import android.text.Html;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


class OrgHandler extends DefaultHandler {

    // Feed and Article objects to use for temporary storage
    private Orgs currentOrg;
    private final List<Orgs> orgList = new ArrayList<Orgs>();


    //Current characters being accumulated
    private StringBuffer chars = new StringBuffer();

    public OrgHandler() {
        currentOrg = new Orgs();
    }


    /*
     * This method is called every time a start element is found (an opening XML marker)
     * here we always reset the characters StringBuffer as we are only currently interested
     * in the the text values stored at leaf nodes
     *
     * (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        chars = new StringBuffer();
    }


    /*
     * This method is called every time an end element is found (a closing XML marker)
     * here we check what element is being closed, if it is a relevant leaf node that we are
     * checking, such as Title, then we get the characters we have accumulated in the StringBuffer
     * and set the current Article's title to the value
     *
     * If this is closing the "Item", it means it is the end of the article, so we add that to the list
     * and then reset our Article object for the next one on the stream
     *
     *
     * (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {


        if (localName.equalsIgnoreCase("lname")) {
            currentOrg.setLname(chars.toString());
        } else if (localName.equalsIgnoreCase("sname")) {
            currentOrg.setSname(chars.toString());

        } else if (localName.equalsIgnoreCase("description")) {
            currentOrg.setDescription(Html.fromHtml(chars.toString()).toString());

        } else if (localName.equalsIgnoreCase("logo")) {
            currentOrg.setLogo(chars.toString());
        }

        // Check if looking for article, and if article is complete
        if (localName.equalsIgnoreCase("org")) {
            orgList.add(currentOrg);

            currentOrg = new Orgs();
        }
    }


    /*
     * This method is called when characters are found in between XML markers, however, there is no
     * guarantee that this will be called at the end of the node, or that it will be called only once
     * , so we just accumulate these and then deal with them in endElement() to be sure we have all the
     * text
     *
     * (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char ch[], int start, int length) {
        chars.append(new String(ch, start, length));

    }

    /*
     * This is the entry point to the parser and creates the feed to be parsed
     *
     * @return orgList
     */
    public List<Orgs> getOrgs(String feedUrl) {
        URL url;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            url = new URL(feedUrl);

            xr.setContentHandler(this);
            xr.parse(new InputSource(url.openStream()));
        } catch (IOException e) {
            Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
        } catch (SAXException e) {
            Log.e("RSS Handler SAX", "SAX EXCEPTION: " + e.toString());
        } catch (ParserConfigurationException e) {
            Log.e("Bio Handler Parser", e.getMessage().toString());
        }

        return orgList;
    }

    public Orgs getOrg(int location) {
        return orgList.get(location);
    }
}
