package com.bokarat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Page {
    protected String url;
    protected int responseCode;
    protected int level;// how deep this page from Base_URL
    protected ArrayList<Page> pageLinks;// links found on this page
    protected LinkType linkType;
    protected int parents;//how many times this link were found on other pages
    protected boolean isLinksCheckedOnPage;// is search in pageLinks for new pages and adding them to the pages DONE?
    protected boolean getPageLinks;

    public static final String BASE_URL = "http://bokarat.com/";
    public static ArrayList<Page> pages;// list of all pages

    public static final String PREFIX_HTTP = "http://";
    public static final String PREFIX_HTTPS = "https://";
    public static final String PREFIX_WWW = "www.";
    public static final String BASE_CLEAN_URL = urlCleanUp(BASE_URL);

    static {// static initializing
        pages = new ArrayList<Page>();//initializing list of ALL Pages
    }

    public Page(String url, int level) {// constructor
        this.pageLinks = new ArrayList<Page>();
        this.url = url.trim().toLowerCase();
        this.level = level;
        this.parents = 0; // quanitity of this link on other pages
        this.isLinksCheckedOnPage = false;
        this.getPageLinks = false;
        this.linkType = checkInternalExternal(url) ? LinkType.ITERNAL : LinkType.EXTERNAL;

    }

    public void getPageLinks() {

        if (this.getPageLinks){// if we already get links
            return;
        }
        Document doc = null;//initializing page doc

        if (!this.linkType.equals(LinkType.ITERNAL)) {// if not Internal link - not seacrhing for links on that page

            return;
        }

        try {
            doc = Jsoup.connect(this.url).get();// connecting to URL if ok - get doc in NO change response code and return
            this.responseCode = 200;

        } catch (IOException e) {
            System.out.println("Can not connect to URL :" + this.url);
            this.responseCode = 1000;

            return;
        }
        Elements links = doc.select("a[href]");// selecting all <a tag with href attribute; Elements - iterateble

        for (Element link : links
        ) {

            String linkUrl = link.attr("href").trim().toLowerCase();//get url from <a element

            if (checkLink(linkUrl)) {// check link for protocol prefix and image extension
                this.pageLinks.add(new Page(linkUrl, this.level + 1));
            }
        }
        this.getPageLinks = true;
    }

    public static boolean checkLink(String url) {//checking http https protocol if no - not right link,
        // .jpg .png etc not right pagelikn( external links checking later)
        url = url.trim();
        if (url.startsWith(PREFIX_HTTP) || url.startsWith(PREFIX_HTTPS)) {

            if (url.toLowerCase().endsWith(".jpg")//checking all images extensions
                    || url.toLowerCase().endsWith(".jpeg")
                    || url.toLowerCase().endsWith(".png")
                    || url.toLowerCase().endsWith(".gif")) {
                return false;
            } else {
                return true;
            }
        } else {

            return false;
        }
    }

    public static boolean checkInternalExternal(String url) {//check internal or external link(after separating images and
        // same page links and telephone links etc..
        if (urlCleanUp(url).startsWith(BASE_CLEAN_URL)) {
            return true;
        }
        return false;

    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(this.url)
                .append(" level[").append(this.level).append("]")
                .append(", ").append(this.linkType).append(", Links - ")
                .append(pageLinks != null ? pageLinks.size() : "null").append(", code[")
                .append(this.responseCode != 0 ? this.responseCode : "none").append("]")
                .append(", found in [").append(this.parents).append("] pages.");
        return stringBuffer.toString();
    }

    public static String urlCleanUp(String url) {// clean url from all prefix http www https for easy comparing
        String cleanUrl = url;
        if (cleanUrl.startsWith(PREFIX_HTTPS)) {// remove protocol prefix
            cleanUrl = cleanUrl.substring(8);
        } else if (cleanUrl.startsWith(PREFIX_HTTP)) {
            cleanUrl = cleanUrl.substring(7);
        }
        if (cleanUrl.startsWith(PREFIX_WWW)) {
            cleanUrl = cleanUrl.substring(4);
        }
        // may be think about removing last "/" from url ..
        return cleanUrl;
    }

    @Override
    public boolean equals(Object o) {//overrided method to compare pages, if url's equal => pages equal
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Page secondPage = (Page) o;
        return this.url.equals(secondPage.url);
    }

    public static void addToMainList(Page newPage) {// adding links to  all pages list
       // System.out.println("starting add too main list");
        if (newPage.isLinksCheckedOnPage) {//if links on page already checked - do nothing
            return;
        }

        //if this page not in the main list - add;
        if (pages.size() == 0) {
            pages.add(newPage);
        }


        System.out.println("iterating Main list");

        for (Page localPage : newPage.pageLinks// iterating pages from array of pages, given Page
        ) {
            boolean found = false;
            for (Page globalPage : pages
            ) {
                if (localPage.equals(globalPage)) {
                    found = true;// we found this link in main link list
                    globalPage.parents++;
                    if (globalPage.parents>localPage.parents){
                        globalPage.parents = localPage.parents;
                    }
                }
            }
            if (!found) {// not found this page
                pages.add(new Page(localPage.url, localPage.level));//creating in the main list page from local list
            }

            // checking new page from local list
        }

        newPage.isLinksCheckedOnPage = true; // already checked
    }


}
