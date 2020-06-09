package org.cft.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HtmlWrapperTest {
    @Test
    public void wrap_returnStringWithMultipleWrappedHtmlUrls_whenPassedStringWithMultipleValidUrls() {
        String stringWithUrl =
                "https://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java\n" +
                "http://google.com\n" +
                "http://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/text/WordUtils.html#wrap(java.lang.String,%20int,%20java.lang.String,%20boolean)\n";
        List<String> stringWithWrappedUrl = new ArrayList<>(Arrays.asList(
                "<a href=\"https://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java\">stackoverflow.com</a>",
                "<a href=\"http://google.com\">google.com</a>",
                "<a href=\"http://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/text/WordUtils.html#wrap(java.lang.String,%20int,%20java.lang.String,%20boolean)\">commons.apache.org</a>"));

        assertEquals(stringWithWrappedUrl, HtmlWrapper.wrapUrl(stringWithUrl));
    }

    @Test
    public void wrap_returnStringWithMultipleWrappedHtmlUrls_whenPassedStringWithMultipleUrls() {
        String stringWithUrl = "http://google123-me_io.com bidi \nsome text";
        List<String> stringWithWrappedUrl = new ArrayList<>(Arrays.asList("http://google123-me_io.com bidi", "some text"));

        assertEquals(stringWithWrappedUrl, HtmlWrapper.wrapUrl(stringWithUrl));
    }
}