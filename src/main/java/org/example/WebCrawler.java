package org.example;
/**
 * Imports
 * */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.in;


public class WebCrawler {

    private static final int THREAD_POOL_SIZE = 10;

    private ExecutorService executor;
    private ArrayList<String> visited;
    private static FileWriter FILE;
    static int MAX_DEPTH;



    public static void main(String[] args) {

        try {
            ArrayList<String> visitedLinks = new ArrayList<>(); // cache to store visited links



        } catch (Exception e){

            System.out.println("[ERROR]: " + e.getMessage());
        }
    }


    public void run(){
        System.out.println("Web Crawler is running...");

        try {

            Scanner userInput = new Scanner(in);

            System.out.println("Enter URL: \n");
            String url = userInput.nextLine();
            System.out.println("Enter depth: \n");
            MAX_DEPTH = userInput.nextInt();

            visited = (ArrayList<String>) Collections.synchronizedList(new ArrayList<String>()); // thread-safe list to store visited links
            executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            FILE = new FileWriter("report.md");
            submitTask(normalizeUrl(url), 0);

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
            FILE.close();



        } catch (Exception e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }


    public void submitTask(String url, int depth) {
        executor.submit(new Task(url, depth, MAX_DEPTH, visited, FILE, this));
    }

    public static void startFetch(String url, int depth, ArrayList<String> visited) throws IOException {
        if(depth >= MAX_DEPTH){
            return;
        }

        depth++;

        if (visited.contains(normalizeUrl(url))) {
            return;
        }

        visited.add(normalizeUrl(url));
        ArrayList<String> links = getLinks(url);

        FILE.write("input: " + "<a>" +url+ "</a>" +"\n");
        FILE.write("<br>depth: " + depth + "\n");

        for(int i = 1; i<=getHeadings(url).size(); i++){
            FILE.write(getHeadings(url).get(i-1) + "\n");
        }

        for(String link : links){
            startFetch(link, depth, visited);
        }
    }
    private static Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    public static List<String> getHeadings(Document doc){
        List<HeadingData> headings = extractHeadings(doc);
        List<String> result = new ArrayList<>();
        for (HeadingData h : headings) {
            result.add(formatHeading(h));
        }
        return result;
    }

    public static ArrayList<String> getHeadings(String url) throws IOException {
        Document document = fetchDocument(url);
        List<HeadingData> headings = extractHeadings(document);

        ArrayList<String> result = new ArrayList<>();
        for (HeadingData h : headings) {
            result.add(formatHeading(h));
        }
        return result;
    }
    private static String formatHeading(HeadingData heading) {
        return getHashTag(heading.level) + " " + heading.text + " " + heading.numbering;
    }

    public static List<HeadingData> extractHeadings(Document doc) {
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


    public static String getHashTag(int headingLevel){
        return "#".repeat(Math.max(0, headingLevel));
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


}