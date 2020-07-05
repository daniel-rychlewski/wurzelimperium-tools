import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

// Kommandozeilenapplikation, die ich über die Aufgabenverwaltung ausführen kann
public class CoinsGenerator {
    private static final int COINSVIDEO_ITERATIONEN = Integer.MAX_VALUE;//810; // wie oft der Request geschickt wird. 1 Iteration = 1 Video, 50 Coins maximal pro Tag, 10 Videos entsprechen einem Coin
    private static final int TIMEOUT = 1500; // zwischen Abrufen eines Tokens und Verwenden dieses einen Tokens
    private static final int INFINITEQUESTS_ITERATIONEN = 8;
    private static final int AUTOKINO_ITERATIONEN = Integer.MAX_VALUE;//10;
    private static final String SERVER = "server7";
    private static final String USER = "redacted";
    private static final String PASSWORD = "redacted";

    public static void main(String[] args) throws IOException, InterruptedException {
        // CHOOSE EXACTLY ONE OPTION
        // Option 1: GENERATE COINS
        generateCoins();
        // Option 2: REDEEM COINS
//        while (true) {
//            System.out.println(redeemCoins());
//            Thread.sleep(4 * 60 * 1000); // 4 Minuten bei 9 generate Coins Threads bei den bestehenden Timeout-Parametern
//        }
//        System.out.println(redeemCoins());
        // Option 3: INFINITEQUESTS
//        Stream.iterate(1, i -> i + 1).limit(INFINITEQUESTS_ITERATIONEN).forEach(i -> {
//            try {
//                fulfillAnInfiniteQuest();
//            } catch (JSONException ignoreMe) {
//                // don't want the program to crash because of some random exception
//            } catch (IOException e) {
//                // should not happen & don't care at all
//                e.printStackTrace();
//            }
//        });
        // Option 4: AUTOKINO
//        Stream.iterate(1, i -> i + 1).limit(AUTOKINO_ITERATIONEN).forEach(i -> {
//            try {
//                viewAutokinoVideo();
//                Thread.sleep(1000 * (60 * 5 + 5)); // 5 Minuten Wartezeit + paar Sekunden, um sicher zu gehen
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
    }

    private static void viewAutokinoVideo() throws IOException {
        String authToken = authenticate();
        // initiate video
        URL activateVideoValidity = new URL("http://s7.wurzelimperium.de/ajax/ajax.php?do=videoInit&token="+authToken);
        HttpURLConnection initiate = (HttpURLConnection) activateVideoValidity.openConnection();
        initiate.setRequestMethod("GET");
        setCookie(initiate, false, false, true);
        setVariousSettings(initiate);
        initiate.connect();
        System.out.println("autokino video initiation (expected 200): "+initiate.getResponseCode());
        // make the video viewed
        URL viewVideo = new URL("http://s7.wurzelimperium.de/ajax/ajax.php?do=videoSeen&hash=f63795114677a36e0c060ea21b3a9724&token="+authToken);
        HttpURLConnection view = (HttpURLConnection) viewVideo.openConnection();
        view.setRequestMethod("GET");
        setCookie(view, false, false, true);
        setVariousSettings(view);
        view.connect();
        System.out.println("video hopefully viewed");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(view.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        bufferedReader.close();

        System.out.println(sb.toString());

        // todo why does it not work from a new day on, which requests are important to make it valid
    }

    private static void fulfillAnInfiniteQuest() throws IOException {
        String authToken = authenticate();
        InfiniteQuest infiniteQuest = getInfiniteQuestDetails(authToken);
        // todo automatisches besorgen von produkten aktivieren? und darauf achten, dass geld nicht komplett leer wird
        // todo nächster Schritt: am Marktplatz vergleichen, ggf. am marktplatz nehmen, wenn günstiger
        sendProductsForInfiniteQuest(infiniteQuest, authToken);
    }

    private static void sendProductsForInfiniteQuest(InfiniteQuest infiniteQuest, String authToken) {
        System.out.println("trying to solve this quest: "+infiniteQuest);
        List<URL> urlsThatNeedToBeAccessed = new ArrayList<>();
        infiniteQuest.pidToAmountMap.forEach((key, value) -> {
            try {
                urlsThatNeedToBeAccessed.add(new URL("http://s7.wurzelimperium.de/ajax/ajax.php?do=infinite_quest_entry&pid=" + key + "&amount=" + value + "&questnr="+infiniteQuest.questNr+"&token="+authToken));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        urlsThatNeedToBeAccessed.forEach(url -> {
            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                setCookie(con, false, false, true);
                setVariousSettings(con);
                con.connect();
                System.out.println("ein infinitequest produkt eingeschickt - code: " + con.getResponseCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Finds the URL to authenticate against and does the authentication, the result of which is the token returned.
     */
    private static String authenticate() throws IOException {
        String authenticateURL = findAuthenticationURL();
        return doAuthenticate(authenticateURL);
    }

    /**
     * Does the authentication process with the specified authenticateURL, returning the token to be used for further requests.
     */
    private static String doAuthenticate(String authenticateURL) throws IOException {
        URL url = new URL(authenticateURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        setCookie(con, false, false, false);
        setVariousSettings(con);
        con.connect();
        System.out.println("doAuthenticate: "+con.getResponseCode());
        // extract token

        String findMe = "token=";
        int beginIndex = authenticateURL.indexOf(findMe) + findMe.length();
        return authenticateURL.substring(beginIndex);
    }

    private static String findAuthenticationURL() throws IOException {
        URL url = new URL("https://www.wurzelimperium.de/dispatch.php?r=299645629");
        String content = "do=login&server="+SERVER+"&user="+USER+"&pass="+PASSWORD;
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        setVariousSettings(con);

        // Set Request Body https://stackoverflow.com/questions/20020902/android-httpurlconnection-how-to-set-post-data-in-http-body
        byte[] outputInBytes = content.getBytes(StandardCharsets.UTF_8);
        OutputStream os = con.getOutputStream();
        os.write( outputInBytes );
        os.close();

        // Now execute
        con.connect();

        // Evaluate; get URL for authentication
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        bufferedReader.close();

        JSONObject obj = new JSONObject(sb.toString());
        return obj.getString("url");
    }

    private static InfiniteQuest getInfiniteQuestDetails(String token) throws IOException {
        URL url = new URL("http://s7.wurzelimperium.de/ajax/ajax.php?do=infinite_quest_get&token="+token);
        String content = "";
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // todo which request properties are required? I bet many are not

        // Set request properties
        con.setRequestProperty("Host", "s7.wurzelimperium.de");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Content-Length", content.getBytes().length+"");
        con.setRequestProperty("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
        con.setRequestProperty("X-Prototype-Version", "1.7");
        con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        con.setRequestProperty("Referer", "http://s7.wurzelimperium.de/main.php?page=garden");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        setCookie(con, false, false, true);

        setVariousSettings(con);

        // Now execute
        con.connect();

        // Get product pids and their amounts
        InfiniteQuest theDetails = new InfiniteQuest();
        int status = con.getResponseCode();
        System.out.println("getInfiniteQuest: "+status);

        // Convert response to String
        GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(con.getInputStream()));

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        bufferedReader.close();

        String tokenResponse = sb.toString();
        System.out.println("infinitequestsDetails: "+tokenResponse);

        // todo: Nach Erfüllen eines Quests kann unter questnr nichts gefunden werden (nur beim darauffolgenden Request, ansonsten geht es schon
        // todo: vermutlich muss ein Ajax-Request infinite_quest.get gemacht werden, wie bei infinitequests.open() im Browser):
        /*
        doAuthenticate: 302
        Exception in thread "main" org.json.JSONException: JSONObject["questnr"] not a string.
        	at org.json.JSONObject.getString(JSONObject.java:855)
        	at CoinsGenerator.getInfiniteQuestDetails(CoinsGenerator.java:166)
        	at CoinsGenerator.fulfillAnInfiniteQuest(CoinsGenerator.java:34)
        	at CoinsGenerator.main(CoinsGenerator.java:29)
        getInfiniteQuest: 200
        infinitequestsDetails: {"status":"ok","questnr":267,"questData":{"products":[{"pid":401,"name":"Gemeine Wegwarte","amount":725,"send":0,"missing":725},{"pid":12,"name":"Gurke","amount":262143,"send":0,"missing":262143}],"reward":["17.480 wT"],"text":"","title":"Quest 267: ","allDone":false,"remain":86325},"quest":1}
         */
        // Parse JSON-String
        JSONObject obj = new JSONObject(tokenResponse);
        theDetails.questNr = Integer.parseInt(obj.getString("questnr"));
        JSONArray jsonArray = obj.getJSONObject("questData").getJSONArray("products");
        Map<Long, Long> productIdToMissingAmountMap = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            productIdToMissingAmountMap.put(jsonArray.getJSONObject(i).getLong("pid"), jsonArray.getJSONObject(i).getLong("missing"));
            theDetails.names.add(jsonArray.getJSONObject(i).getString("name"));
        }
        theDetails.pidToAmountMap = productIdToMissingAmountMap;
        return theDetails;
    }

    /**
     * Able to get 50 coins per 24 hours
     */
    private static void generateCoins() {
        String findMe = "unique_token\":\"";
        int findMeLength = findMe.length();

        Stream.iterate(1, i -> i + 1).limit(COINSVIDEO_ITERATIONEN).forEach(durchlauf -> {
            try {
                // Get my ad-viewed-token
                String tokenResponse = getToken();
                // Parse JSON-String to find only token
                int beginIndex = tokenResponse.indexOf(findMe) + findMeLength;
                String token = tokenResponse.substring(beginIndex, tokenResponse.length()-3); // Token ist 40 Zeichen lang
                Thread.sleep(TIMEOUT); // um nicht zu überlasten, d.h. als DDOS zu gelten bzw. zu auffällig zu werden
                System.out.println("Durchlauf "+durchlauf+" von "+ COINSVIDEO_ITERATIONEN +", nutze Token "+token+". Statuscode "+sendToken(token)); // Token für Request verwenden
            } catch (IOException | InterruptedException ignored) {}
        });
    }

    private static void setRequestPropertiesUpPay(HttpURLConnection con, String content) {
        con.setRequestProperty("Host", "www.up-pay.com");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Content-Length", content.getBytes().length+"");
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Origin", "https://www.up-pay.com");
        con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        con.setRequestProperty("Referer", "https://www.up-pay.com/gnpf/index.php?user=1263825&game=2&land=de&hash=8d2196c04368aba6bfbdf53a85358c69&server=7");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
    }

    private static void setCookie(HttpURLConnection con, boolean geoPlaylistIncludedInCookie, boolean amazonPayIncludedInCookie, boolean appendWuNr) {
        StringBuilder cookie = new StringBuilder("session-set=true; __utmz=26691449.1529701816.47.4.utmcsr=s7.wurzelimperium.de|utmccn=(referral)|utmcmd=referral|utmcct=/main.php; PHPSESSID=tt3b8vp8q6t78605bbuls1vhm4");
        cookie.append("; __utma=26691449.531230800.1528749044.1529788985.1529844885.51; __utmc=26691449; __utmt=1; __utmb=26691449.10.9.1529845500544");
        if (geoPlaylistIncludedInCookie) {
            cookie.append("; GED_PLAYLIST_ACTIVITY=W3sidSI6ImNpOGQiLCJ0c2wiOjE1Mjk4NDU1NDIsIm52IjowLCJ1cHQiOjE1Mjk4NDU1MTYsImx0IjoxNTI5ODQ1NTE4fV0.");
        }
        if (amazonPayIncludedInCookie) {
            cookie.append("; amazon-pay-connectedAuth=connectedAuth_general");
        }
        if (appendWuNr) {
            cookie.append("; wunr=1263825");
        }
        con.setRequestProperty("Cookie", cookie.toString());
    }

    /**
     * Collect the coins generated, preferably at 100 coins (for the bonus starting at 100 coins, so anything more is a waste and anything less won't give the bonus).
     */
    private static String redeemCoins() throws IOException {
        URL url = new URL("https://www.up-pay.com/callbacks/videocoins/videocoins.php");
        String content = "user=1263825&game=48&country=";
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        // Setting Request Headers
        setRequestPropertiesUpPay(con, content);
        setCookie(con, false, true, false);

        setVariousSettings(con);

        // Set Request Body https://stackoverflow.com/questions/20020902/android-httpurlconnection-how-to-set-post-data-in-http-body
        byte[] outputInBytes = content.getBytes(StandardCharsets.UTF_8);
        OutputStream os = con.getOutputStream();
        os.write( outputInBytes );
        os.close();

        // Now execute
        con.connect();
        return "" + con.getResponseCode();
    }

    private static String sendToken(String token) throws IOException {
        URL url = new URL("https://www.up-pay.com/callbacks/smartstream/smartstream_postback.php");
        String str = "vrid=001348d1_0030_5b2f970c&uniqueUser=1263825_48&token="+token+"&state=ad+completed";
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        // Setting Request Headers
        setRequestPropertiesUpPay(con, str);
        setCookie(con, true, false, false);
        // Configuring Timeouts
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        setVariousSettings(con);

        // Set Request Body https://stackoverflow.com/questions/20020902/android-httpurlconnection-how-to-set-post-data-in-http-body
        byte[] outputInBytes = str.getBytes(StandardCharsets.UTF_8);
        OutputStream os = con.getOutputStream();
        os.write( outputInBytes );
        os.close();

        // Now execute
        con.connect();
        return "" + con.getResponseCode();
    }

    private static void setVariousSettings(HttpURLConnection con) {
        // Various settings
        con.setInstanceFollowRedirects(false);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
    }

    private static String getToken() throws IOException {
        // https://www.baeldung.com/java-http-request
        URL url = new URL("https://api.playroll.de/l?1529845542346");
        String content = "{\"key\":\"e0rbu-II-awSuzD77kjh\",\"event\":\"hash%3DWvCpy%20ad%20complete%20system%3DDCM%20id%3D421464859\",\"token\":\"mwWcaXIwNGgFCLpcwfmruXGoh8BFiJbNkafuApBZ\",\"sec\":\"b8d8b0a9aa755750cb525cfc4282d6595b57fcd48726dd382a171241c8ca312f\"}";
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        // Setting Request Headers
        con.setRequestProperty("Host", "api.playroll.de");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Content-Length", content.getBytes().length+"");
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
        setVariousSettings(con);
        // Set Request Body https://stackoverflow.com/questions/20020902/android-httpurlconnection-how-to-set-post-data-in-http-body

        byte[] outputInBytes = content.getBytes(StandardCharsets.UTF_8);
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
