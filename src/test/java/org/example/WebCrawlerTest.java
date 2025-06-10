package org.example;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;




import java.util.List;



import static org.junit.jupiter.api.Assertions.*;


public class WebCrawlerTest {

    @Test
    public void testNormalizeUrl() {
        assertEquals("https://example.com", WebCrawlerUtils.normalizeUrl("http://example.com/"));

        assertEquals("https://example.com", WebCrawlerUtils.normalizeUrl("example.com"));

        assertEquals("https://example.com/page", WebCrawlerUtils.normalizeUrl("http://example.com/page#section"));

    }

    @Test
    public void testGetHashTags(){
        int headingLevel = 4;
        assertEquals(WebCrawlerUtils.getHashTag(headingLevel), "####");
    }

    @Test
    public void testGetLinksWithMockedHtml() {
        String html = "<a href=\"https://example.com/page1\">Page 1</a>"
                + "<a href=\"/relative/page2\">Page 2</a>";



        Document doc = Jsoup.parse(html, "https://example.com");
        List<String> links = WebCrawlerUtils.getLinks(doc);


        assertEquals(2, links.size());
        assertTrue(links.contains("https://example.com/page1"));
        assertTrue(links.contains("https://example.com/relative/page2"));

    }
    @Test
    public void testGetLinksEmptyPage() {


        String html = "<html><body>No links here</body></html>";
        Document doc = Jsoup.parse(html, "https://example.com");

        List<String> links = WebCrawlerUtils.getLinks(doc);

        assertTrue(links.isEmpty());
    }


}
