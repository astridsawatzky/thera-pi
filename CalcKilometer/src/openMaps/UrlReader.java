package openMaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Collectors;

 class UrlReader {
    public String readfromUrlt(String[] args) throws IOException {
        String urlString = args[0];
        URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return reader.lines()
                     .collect(Collectors.joining());
    }

    

}
