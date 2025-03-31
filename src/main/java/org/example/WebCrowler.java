package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


import static java.lang.System.in;


public class WebCrowler {
    private static final int MAX_DEPTH = 2; // maximum depth to crawl, can be changed
    private static FileWriter FILE;

    public static void main(String[] args) {

        Scanner userInput = new Scanner(in);

        System.out.println("Enter URL: \n");
        String url = userInput.nextLine();
        System.out.println("Enter depth: \n");
        int depth = userInput.nextInt();



        int numberOfHeadings = 6; // possible headings are <h1>, <h2>, <h3>, <h4>, <h5>, <h6>

        try {
            ArrayList<String> visitedLinks = new ArrayList<>(); // cache to store visited links

            FILE = new FileWriter("report.md");

            startFetch(normalizeUrl(url), 0, visitedLinks);

            FILE.close();


        } catch (Exception e){

            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    public static void startFetch(String url, int depth, ArrayList<String> visited) throws IOException {
        if(depth >= MAX_DEPTH){
            return;
        }


        if(visited.contains(normalizeUrl(url))){

            FILE.write("[VISITED]: " + url + "\n");
            System.out.println("[VISITED]: " + url);
            return;
        }
        depth++;

        visited.add(normalizeUrl(url));
        ArrayList<String> links = getLinks(url);


        System.out.println("input: " + url );
        System.out.println("<br>depth: " + depth);
        FILE.write("input: " + url + "\n");
        FILE.write("<br>depth: " + depth + "\n");



        for(String heading : getHeadings(url)){
            System.out.println(heading);

            FILE.write(heading + "\n");
        }

        for(String link : links){
            startFetch(link, depth, visited);
        }
    }

    public static ArrayList<String> getHeadings(String url) throws IOException {

        Document document = Jsoup.connect(url).get();

        ArrayList<String> headingsList = new ArrayList<>();

        int numHeadings = 6;

        for(int i = 1; i<=numHeadings; i++){

            Elements headings = document.select("h" + i);

            for(Element heading : headings){
                headingsList.add(getHashTag(i) + " ---> "+ heading.text());
            }
        }

        return headingsList;
    }

    public static String getHashTag(int headingLevel){
        StringBuilder hashTags = new StringBuilder();

        for(int i = 0; i<headingLevel; i++){
            hashTags.append("#");
        }
        return hashTags.toString();
    }

    public static ArrayList<String> getLinks(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        ArrayList<String> linksList = new ArrayList<>();
        Elements links = document.select("a[href]");


        for(Element link : links){
            linksList.add(link.attr("abs:href"));
        }
        return linksList;
    }


    public static String urlToSecure(String url){
        if (url.startsWith("http://")) {
            url = "https://" + url.substring(7);
        }
        return url;
    }

    public static String normalizeUrl(String url) {

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
}