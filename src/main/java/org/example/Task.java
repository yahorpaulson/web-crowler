package org.example;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import java.io.FileWriter;
import java.util.List;

public class Task implements Runnable{

    private final String url;
    private final int depth;
    private final int maxDepth;
    private final List<String> visitedLinks;
    private final FileWriter fileWriter;
    private final WebCrawler crawler;


    public Task(String url, int depth, int maxDepth, List<String> visitedLinks, FileWriter fileWriter, WebCrawler crawler) {
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
            String normalizedUrl = WebCrawlerUtils.normalizeUrl(url);

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
                    for(String heading : WebCrawlerUtils.getHeadings(doc)) {
                        fileWriter.write(heading + "\n");
                    }
                    for(String link : WebCrawlerUtils.getLinks(doc)) {

                        crawler.submitTask(link, depth + 1);
                    }
                } catch (Exception e) {
                    System.out.println("[ERROR]: " + e.getMessage());
                }
            }

            System.out.println("Thread " + Thread.currentThread().getName() + " processed URL: " + url + " at depth: " + depth + "\n");

        } catch (Exception e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }
}
