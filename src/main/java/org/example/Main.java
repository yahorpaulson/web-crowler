package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {

        //Scanner userInput = new Scanner(in);

        //String params = userInput.nextLine();

        int numberOfHeadings = 6;

        //String url = params;

        String url = "https://example.com/";

        try {
            int depth = 1;
            getHeadings(url, numberOfHeadings);

            ArrayList<String>links = getLinks(url);

            for(String link : links){
                getHeadings(link, numberOfHeadings);
            }


        } catch (Exception e){
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    public static void getHeadings(String url, int numHeadings) throws IOException {

        StringBuilder builder = new StringBuilder();

        Document document = Jsoup.connect(url).get();

        for(int i = 1; i<=numHeadings; i++){

            Elements headings = document.select("h" + i);

            builder.append("#" );

            for(Element heading : headings){
                System.out.println(builder + "-->"+heading.text()+"\n");

            }
        }
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
}