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


class ArticleHandler extends DefaultHandler {

    // Feed and Article objects to use for temporary storage
    private Article currentArticle = new Article();
    private final List<Article> articleList = new ArrayList<Article>();

    // Number of articles added so far
    private int articlesAdded = 0;

    // Number of articles to download, if this is not limited user is waiting a while for all the info to download before articles appear
    private static final int ARTICLES_LIMIT = 25;

    //Current characters being accumulated
    private StringBuffer chars = new StringBuffer();

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

        if (localName.equalsIgnoreCase("title")) {
            currentArticle.setTitle(chars.toString());
        } else if (localName.equalsIgnoreCase("description")) {
            currentArticle.setDescription(Html.fromHtml(chars.toString()).toString());
        } else if (localName.equalsIgnoreCase("pubDate")) {
            currentArticle.setPubDate(chars.toString());
        } else if (localName.equalsIgnoreCase("encoded")) {
            currentArticle.setEncodedContent(chars.toString());
        } else if (localName.equalsIgnoreCase("link")) {
            currentArticle.setUrl(chars.toString());
        } else if (localName.equalsIgnoreCase("thumbnail")){
        	currentArticle.setImg(chars.toString());
        } else if (localName.equalsIgnoreCase("author")){
        	currentArticle.setAuthor(chars.toString());
        }

        // Check if looking for article, and if article is complete
        if (localName.equalsIgnoreCase("item")) {

            articleList.add(currentArticle);
            articlesAdded++;
            Log.d("ArticleHandler", "Article added: " + currentArticle.getTitle() + " Article Number: " + articleList.size());
            currentArticle = new Article();

            // Lets check if we've hit our limit on number of articles
            if (articlesAdded >= ARTICLES_LIMIT) {
                Log.d("ArticleHandler", "Aricle Limit Reached!");
                throw new SAXException();
            }
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
     * @param feedUrl
     * @return
     */
    public List<Article> getLatestArticles(String feedUrl) {
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
            Log.e("RSS Handler SAX", e.toString());
        } catch (ParserConfigurationException e) {
            Log.e("RSS Handler Parser Config", e.toString());
        }
        return articleList;
    }

    public Article getArticle(int location) {
        return articleList.get(location);
    }

    public List<Article> getList() {
        return articleList;
    }

}
