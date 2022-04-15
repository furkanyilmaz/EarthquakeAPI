import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

    public static final String yesil = "\u001B[32m";

    private static HttpURLConnection baglanti;

    public static void main(String[] Args) {

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        LocalDateTime endtime = LocalDateTime.now();
        String today = formatter.format(Date.from(endtime.toInstant(ZoneOffset.UTC)));
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Country: ");
        String whichCountry = scanner.nextLine();
        System.out.print("Count Of Days: ");
        Long inputdays = scanner.nextLong();
        System.out.println(responseContent.toString());

        String starttime = formatter.format(Date.from(endtime.minusDays(inputdays).toInstant(ZoneOffset.UTC)));


        try {
            String spec = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + starttime + "&endtime=" + today;
            URL url = new URL(spec);
            baglanti = (HttpURLConnection) url.openConnection();
            baglanti.setRequestMethod("GET");
            baglanti.setConnectTimeout(5000);
            baglanti.setReadTimeout(5000);
            baglanti.connect();

            int status = baglanti.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(baglanti.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                String inline = "";
                URL url1 = new URL(spec);
                scanner = new Scanner(url1.openStream());
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                JSONParser parse = new JSONParser();
                JSONObject obj = (JSONObject) parse.parse(inline);
                JSONArray obj1 = (JSONArray) obj.get("features");

                String konum;
                String country = "";
                String tarih = "";
                String saat = "";
                Object buyukluk;

                String[] array = {"---Country---", "---Date---", "----Time----", "-----Place----", "-----Magnitude---"};
                System.out.println(yesil + Arrays.toString(array));


                for (int i = 0; i < obj1.size(); i++) {
                    JSONObject data = (JSONObject) obj1.get(i);
                    try {
                        JSONObject properties = (JSONObject) data.get("properties");
                        Long time = (Long) properties.get("time");
                        String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(new Date(time));
                        tarih = dateAsText.split(" ")[0];
                        saat = dateAsText.split(" ")[1];
                        buyukluk = properties.get("mag");
                        konum = (String) properties.get("place");
                        country = (String) properties.get("place");
                        if (country.contains(",")) {
                            String[] dizi = country.split(", ");
                            country = dizi[dizi.length - 1];
                            konum = dizi[0];

                        }

                        if (!country.equals(whichCountry)) {
                            continue;

                        }

                    } catch (NullPointerException ex) {
                        continue;
                    }
                    System.out.println("   " + country + "------" + tarih + "------" + saat + "------" + konum + "--------" + buyukluk);

                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            baglanti.disconnect();
        }
    }
}
