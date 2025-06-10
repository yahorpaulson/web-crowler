package org.example;

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

        try (Scanner userInput = new Scanner(in)) {

            System.out.println("...Starting Web Crawler");
            System.out.println("Enter URL: \n");
            String url = userInput.nextLine();
            System.out.println("Enter depth: \n");
            MAX_DEPTH = userInput.nextInt();

            visited = Collections.synchronizedList(new ArrayList<>()); // visited links
            executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            FILE = new FileWriter("report.md");
            submitTask(WebCrawlerUtils.normalizeUrl(url), 0);

            boolean finished = executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);

            if(!finished) {
                System.out.println("[ERROR]: Executor did not finish within the expected time.");
            } else {
                System.out.println("All tasks submitted. Waiting for completion...");
            }
            FILE.close();

        } catch (Exception e) {
            System.out.println("[ERROR]: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (executor != null && !executor.isShutdown()) {
                executor.shutdown();
            }
            if (FILE != null) {
                try {
                    FILE.close();
                } catch (IOException e) {
                    System.out.println("[ERROR]: Unable to close file writer. " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


    public void submitTask(String url, int depth) {
        if (url == null || url.isEmpty()) {
            System.out.println("[ERROR]: URL cannot be null or empty.");
            return;
        }

        activeTasks.incrementAndGet();

        executor.submit(() -> {
            try{
                new Task(url, depth, MAX_DEPTH, visited, FILE, this).run();
            } catch (Exception e){
                System.out.println("[ERROR]: An error occurred while processing the URL: " + url);
                e.printStackTrace();

            }finally {
                if (activeTasks.decrementAndGet() == 0) {
                    System.out.println("All tasks completed.");
                    executor.shutdown();
                }
            }

        });
    }
















}