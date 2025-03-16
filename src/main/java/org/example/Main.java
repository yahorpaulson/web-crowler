package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;



public class Main {
    private static final int MAX_DEPTH = 2;

    public static void main(String[] args) {

        //Scanner userInput = new Scanner(in);

        //String params = userInput.nextLine();

        int numberOfHeadings = 6;

        //String url = params;

        String url = "https://example.com/";


        try {
            ArrayList<String> visitedLinks = new ArrayList<>();

            startFetch(normalizeUrl(url), 0, numberOfHeadings, visitedLinks);


        } catch (Exception e){
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    public static void startFetch(String url, int depth, int numHeadings, ArrayList<String> visited) throws IOException {
        if(depth >= MAX_DEPTH){
            return;
        }

        if(visited.contains(normalizeUrl(url))){
            System.out.println("[VISITED]: " + url);
            return;
        }
        depth++;

        visited.add(normalizeUrl(url));
        ArrayList<String> links = getLinks(url);
        System.out.println("input: " + url );
        System.out.println("<br>depth: " + depth);

        for(String heading : getHeadings(url, numHeadings)){
            System.out.println(heading);
        }

        for(String link : links){
            startFetch(link, depth, numHeadings, visited);
        }
    }



    public static ArrayList<String> getHeadings(String url, int numHeadings) throws IOException {

        Document document = Jsoup.connect(url).get();

        ArrayList<String> headingsList = new ArrayList<>();

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

    private static String normalizeUrl(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        int fragmentIndex = url.indexOf("#");
        if (fragmentIndex != -1) {
            url = url.substring(0, fragmentIndex);
        }
        return url.toLowerCase();
    }
}