package com.rychlewski.mypackage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
public class Hauptseite {

    private static final int ITERATIONEN = (510 / 3) + (510 / 3); // "/ 3", weil alles immer dreimal scheinbar ausgeführt wird
    private static final int TIMEOUT = 3500; // zwischen Abrufen eines Tokens und Verwenden dieses einen Tokens
    private static int DURCHLAUF = 1; // apparently läuft das dreimal durch

    @GetMapping("/")
    public String index(Model m) {
        m.addAttribute("iterationen", ITERATIONEN);
        List<String> allToken = new ArrayList<>();
        String findMe = "unique_token\":\"";
        int findMeLength = findMe.length();
        for (int i = 0; i < ITERATIONEN; i++) {
            try {
                String tokenResponse = getToken();
                // Parse JSON-String to find only token
                int beginIndex = tokenResponse.indexOf(findMe) + findMeLength;
                String token = tokenResponse.substring(beginIndex, tokenResponse.length()-3); // Token ist 40 Zeichen lang
                allToken.add(token);
                Thread.sleep(TIMEOUT); // um nicht zu überlasten, d.h. als DDOS zu gelten bzw. zu auffällig zu werden
                System.out.println("Durchlauf "+(DURCHLAUF++)+": Statuscode "+sendToken(token)); // Token für Request verwenden
            } catch (IOException | InterruptedException /*| InterruptedException*/ e) {
                e.printStackTrace();
            }
        }
        m.addAttribute("tokens", allToken/*allToken.toString().substring(1,allToken.toString().length()-1) to remove [ ]*/);
        // todo statt forEach 510 (ITERATIONEN) separate Attribute erstellen und printen, wenn überhaupt. (Aktuelle Iteration dranhängen)
        return "index";
    }
    private String sendToken(String token) throws IOException {
        URL url = new URL("https://www.up-pay.com/callbacks/smartstream/smartstream_postback.php");
        String str = "vrid=001348d1_0030_5b2f970c&uniqueUser=1263825_48&token="+token+"&state=ad+completed";
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        // Setting Request Headers
        con.setRequestProperty("Host", "www.up-pay.com");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Content-Length", str.getBytes().length+"");
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Origin", "https://www.up-pay.com");
        con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        con.setRequestProperty("Referer", "https://www.up-pay.com/gnpf/index.php?user=1263825&game=2&land=de&hash=8d2196c04368aba6bfbdf53a85358c69&server=7");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        con.setRequestProperty("Cookie", "session-set=true; __utmz=26691449.1529701816.47.4.utmcsr=s7.wurzelimperium.de|utmccn=(referral)|utmcmd=referral|utmcct=/main.php; PHPSESSID=tt3b8vp8q6t78605bbuls1vhm4; __utma=26691449.531230800.1528749044.1529788985.1529844885.51; __utmc=26691449; __utmt=1; __utmb=26691449.10.9.1529845500544; GED_PLAYLIST_ACTIVITY=W3sidSI6ImNpOGQiLCJ0c2wiOjE1Mjk4NDU1NDIsIm52IjowLCJ1cHQiOjE1Mjk4NDU1MTYsImx0IjoxNTI5ODQ1NTE4fV0.");
        // Configuring Timeouts
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        // Various settings
        con.setInstanceFollowRedirects(false);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        // Set Request Body https://stackoverflow.com/questions/20020902/android-httpurlconnection-how-to-set-post-data-in-http-body

        byte[] outputInBytes = str.getBytes(StandardCharsets.UTF_8);
        OutputStream os = con.getOutputStream();
        os.write( outputInBytes );
        os.close();

        // Now execute
        con.connect();
        return "" + con.getResponseCode();
    }
    private String getToken() throws IOException {
        // https://www.baeldung.com/java-http-request
        URL url = new URL("https://api.playroll.de/l?1529845542346");
        String str = "{\"key\":\"e0rbu-II-awSuzD77kjh\",\"event\":\"hash%3DWvCpy%20ad%20complete%20system%3DDCM%20id%3D421464859\",\"token\":\"mwWcaXIwNGgFCLpcwfmruXGoh8BFiJbNkafuApBZ\",\"sec\":\"b8d8b0a9aa755750cb525cfc4282d6595b57fcd48726dd382a171241c8ca312f\"}";
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        // Setting Request Headers
        con.setRequestProperty("Host", "api.playroll.de");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Content-Length", str.getBytes().length+"");
        con.setRequestProperty("Origin", "https://www.up-pay.com");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Referer", "https://www.up-pay.com/gnpf/index.php?controller=Video&action=getVideoFrame&provider=smartstream");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        // Configuring Timeouts
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        // Various settings
        con.setInstanceFollowRedirects(false);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        // Set Request Body https://stackoverflow.com/questions/20020902/android-httpurlconnection-how-to-set-post-data-in-http-body

        byte[] outputInBytes = str.getBytes(StandardCharsets.UTF_8);
        OutputStream os = con.getOutputStream();
        os.write( outputInBytes );
        os.close();

        // Now execute
        con.connect();
        int status = con.getResponseCode();
        if (status == 200) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            bufferedReader.close();
            //return received string
            return sb.toString();
        }
        return "Fehler in Ausführung des Requests aufgetreten, Statuscode ist "+status+", nicht 200";
    }
}
