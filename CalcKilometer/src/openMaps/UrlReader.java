package openMaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Collectors;

public class UrlReader {
    public String readfromUrlt(String[] args) throws IOException {
        String urlString = args[0];
        URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return reader.lines()
                     .collect(Collectors.joining());
    }

    public String encodeforosm(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8")
                             .replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";

    }

}