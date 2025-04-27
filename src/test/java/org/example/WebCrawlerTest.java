package org.example;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.ArrayList;


import static org.example.WebCrawler.getLinks;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebCrawlerTest {

    @Test
    public void testNormalizeUrl() {
        assertEquals("https://example.com", WebCrawler.normalizeUrl("http://example.com/"));

        assertEquals("https://example.com", WebCrawler.normalizeUrl("example.com"));

        assertEquals("https://example.com/page", WebCrawler.normalizeUrl("http://example.com/page#section"));

    }

    @Test
    public void getHashTagsTest(){
        int headingLevel = 4;
        assertEquals(WebCrawler.getHashTag(headingLevel), "####");
    }

    @Test
    public void testGetLinksWithMockedHtml() throws IOException {

        String fakeHtml = "<html><body>" +
                "<a href=\"https://example.com/page1\">Page 1</a>" +
                "<a href=\"/relative/page2\">Page 2</a>" +
                "</body></html>";


        Document fakeDoc = Jsoup.parse(fakeHtml, "https://example.com");


        try (MockedStatic<Jsoup> jsoupMocked = mockStatic(Jsoup.class)) {
            Connection connectionMock = mock(Connection.class);
            when(connectionMock.get()).thenReturn(fakeDoc);
            jsoupMocked.when(() -> Jsoup.connect("https://example.com")).thenReturn(connectionMock);

            ArrayList<String> links = getLinks("https://example.com");

            assertEquals(2, links.size());
            assertTrue(links.contains("https://example.com/page1"));
            assertTrue(links.contains("https://example.com/relative/page2"));
        }
    }
    @Test
    public void testGetLinksEmptyPage() throws IOException {
        String fakeHtml = "<html><body>Empty html</body></html>";

        Document fakeDoc = Jsoup.parse(fakeHtml, "https://example.com");

        try (MockedStatic<Jsoup> jsoupMocked = mockStatic(Jsoup.class)) {
            Connection connectionMock = mock(Connection.class);
            when(connectionMock.get()).thenReturn(fakeDoc);
            jsoupMocked.when(() -> Jsoup.connect("https://example.com")).thenReturn(connectionMock);

            ArrayList<String> links = getLinks("https://example.com");

            assertTrue(links.isEmpty());
        }
    }


}
