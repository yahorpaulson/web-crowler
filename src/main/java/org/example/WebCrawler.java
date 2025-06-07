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
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.in;


public class WebCrawler {

    private static final int THREAD_POOL_SIZE = 10;
    private ExecutorService executor;
    private final AtomicInteger activeTasks = new AtomicInteger(0);

    private List<String> visited;
    private static FileWriter FILE;
    static int MAX_DEPTH;

    public static void main(String[] args) {

        new WebCrawler().run();
    }


    public void run(){
        System.out.println("Web Crawler is running...");

        try {

            Scanner userInput = new Scanner(in);

            System.out.println("Enter URL: \n");
            String url = userInput.nextLine();
            System.out.println("Enter depth: \n");
            MAX_DEPTH = userInput.nextInt();

            visited = Collections.synchronizedList(new ArrayList<String>()); // visited links
            executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            FILE = new FileWriter("report.md");
            submitTask(normalizeUrl(url), 0);

            executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
            FILE.close();

        } catch (Exception e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }


    public void submitTask(String url, int depth) {

        activeTasks.incrementAndGet();

        executor.submit(() -> {
            try{
                new Task(url, depth, MAX_DEPTH, visited, FILE, this).run();
            } finally {
                if (activeTasks.decrementAndGet() == 0) {
                    System.out.println("All tasks completed.");
                    executor.shutdown();
                }
            }

        });
    }



    public static List<String> getHeadings(Document doc){
        List<HeadingData> headings = extractHeadings(doc);
        List<String> result = new ArrayList<>();
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

    public static ArrayList<String> getLinks(Document document) throws IOException {

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