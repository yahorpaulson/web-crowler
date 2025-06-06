package org.example;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import java.io.FileWriter;
import java.util.ArrayList;

public class Task implements Runnable{

    private final String url;
    private final int depth;
    private final int maxDepth;
    private final ArrayList<String> visitedLinks;
    private final FileWriter fileWriter;
    private final WebCrawler crawler;

    public Task(String url, int depth, int maxDepth, ArrayList<String> visitedLinks, FileWriter fileWriter, WebCrawler crawler) {
        this.url = url;
        this.depth = depth;
        this.maxDepth = maxDepth;
        this.visitedLinks = visitedLinks;
        this.fileWriter = fileWriter;
        this.crawler = crawler;
    }

    @Override
    public void run() {

        try{
            String normalizedUrl = WebCrawler.normalizeUrl(url);

            synchronized (visitedLinks) {
                if (visitedLinks.contains(normalizedUrl)) {
                    return; // URL already visited
                }
                if (depth >= maxDepth) {
                    return; // Reached maximum depth
                }
                visitedLinks.add(normalizedUrl);
            }
            Document doc = Jsoup.connect(url).get();

            synchronized (fileWriter) {
                try {
                    fileWriter.write("input: " + "<a>" +url+ "</a>" +"\n");
                    fileWriter.write("<br>depth: " + depth + "\n");
                    for(String heading : crawler.getHeadings(doc)) {
                        fileWriter.write(heading + "\n");
                    }
                    /*for(String link : crawler.getLinks(doc)) {
                        // Create a new task for each link
                    }*/
                } catch (Exception e) {
                    System.out.println("[ERROR]: " + e.getMessage());
                }
            }

            System.out.println("Task is running in a separate thread.");

        } catch (Exception e) {
            System.out.println("[ERROR]: " + e.getMessage());
        } finally {
            try {
                fileWriter.close();
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }
}
