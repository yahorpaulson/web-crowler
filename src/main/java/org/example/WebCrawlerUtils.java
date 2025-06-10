package org.example;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class WebCrawlerUtils {


    static String getHashTag(int headingLevel) {
        return "#".repeat(Math.max(0, headingLevel));
    }


    private static String urlToSecure(String url){
        if (url.startsWith("http://")) {
            url = "https://" + url.substring(7);
        }
        return url;
    }
    public static List<String> getHeadings(Document doc){
        List<HeadingData> headings = extractHeadings(doc);
        List<String> result = new ArrayList<>();
        for (HeadingData h : headings) {
            result.add(formatHeading(h));
        }
        return result;
    }

    public static String normalizeUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("[Error]: URL cannot be null or empty. Please provide a valid URL to normalize.");
        }

        if(!url.startsWith("http")){
            url = "https://"+url;
        }

        url = urlToSecure(url);

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (url.contains("://www.")) {
            url = url.replace("://www.", "://");
        } else if (url.startsWith("www.")) {
            url = url.replace("www.", "");
        }
        int fragmentIndex = url.indexOf("#");
        if (fragmentIndex != -1) {
            url = url.substring(0, fragmentIndex);
        }
        return url.toLowerCase();
    }
    public static ArrayList<String> getLinks(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null. Error: Unable to extract links. Please provide a valid HTML document.");
        }
        ArrayList<String> linksList = new ArrayList<>();
        Elements links = document.select("a[href]");


        for(Element link : links){
            linksList.add(link.attr("abs:href"));
        }
        return linksList;
    }
    private static List<HeadingData> extractHeadings(Document doc) {
        if (doc == null) {
            throw new IllegalArgumentException("[Error]: Document cannot be null. Error: Unable to extract headings. Please provide a valid HTML document.");
        }
        List<HeadingData> headingsList = new ArrayList<>();
        int[] headingCounters = new int[6];

        Elements allHeadings = doc.select("h1, h2, h3, h4, h5, h6");

        for (Element heading : allHeadings) {
            int level = Integer.parseInt(heading.tagName().substring(1));
            headingCounters[level - 1]++;

            for (int i = level; i < 6; i++) headingCounters[i] = 0;

            StringBuilder numbering = new StringBuilder();
            for (int i = 0; i < level; i++) {
                if (headingCounters[i] == 0) continue;
                if (numbering.length() > 0) numbering.append(".");
                numbering.append(headingCounters[i]);
            }

            headingsList.add(new HeadingData(level, heading.text(), numbering.toString()));
        }

        return headingsList;
    }

    private static String formatHeading(HeadingData heading) {
        return getHashTag(heading.level) + " " + heading.text + " " + heading.numbering;
    }


}