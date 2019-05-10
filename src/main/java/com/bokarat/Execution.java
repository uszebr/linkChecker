package com.bokarat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Execution {
    public static final String BASE_URL = "http://bokarat.com/";

    public static void main(String[] args) {
        Page page = new Page("http://bokarat.com/", 0);
        System.out.println(page+" "+ page.getPageLinks);
        page.getPageLinks();
        System.out.println(page+" "+ page.getPageLinks);
        Page.addToMainList(page);


       /* System.out.println(page);
        System.out.println(page.isLinksCheckedOnPage);
        System.out.println(Page.pages.size());
        Page.addToMainList(page);
        System.out.println(page);
        System.out.println(page.isLinksCheckedOnPage);*/

        for (Page ipage : Page.pages
        ) {
            System.out.print(ipage.isLinksCheckedOnPage + " - links checked  ");System.out.println(ipage);
        }
        System.out.println(Page.pages.size());

    }
}
