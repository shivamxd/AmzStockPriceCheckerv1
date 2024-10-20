package com.example.amzstockpricecheckerv1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.loader.content.AsyncTaskLoader;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public boolean inStockVar = false;
    public double priceFromWebsite = 999999999;
    public boolean isStarted;
    public double priceFromApp = 999999999;
    public boolean linkIsForFlipkart = false;
    public int checkEveryMilliSec = 120000;
    MainThread mt;
    public String statusString = "...";




    //this function checks if the character passed to it is a part of a number
    public static boolean isNumber(char c) {
        if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0' || c == '.' || c == ',') {
            return true;
        }
        return false;
    }

    public static boolean inStockAmz(String url) {
        url = getShortURL(url);
        Document doc = null;
        Connection conn = Jsoup.connect(url);
        String avail = "";
        conn.userAgent("Mozilla/5.0 (Linux; Android 13; SM-A528B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Mobile Safari/537.36 EdgA/107.0.1418.43");
        try {
            doc = conn.get();
        } catch (Exception ignored) {
            //System.out.println("error1");
        }
        try {
            Elements availability = doc.getElementsByClass("a-size-medium a-color-success");
            avail = availability.text();
            //System.out.println(avail);
        } catch (Exception ignored) {
            //System.out.println("error2");
        }
        //System.out.println(avail);
        //return avail.equals("In stock.");
        boolean inStock = false;
        try {
            inStock = !(avail.charAt(0) == 'C');
        } catch (Exception e) {
            //System.out.println("error3");
            return true;
        }
        return inStock;
        //System.out.println("yes");
    }


    //this function returns true or false based on if the product is in stock


    public static boolean inStockFlip(String url) throws IOException {
        url = getShortURL(url);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
// optional request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String html = response.toString();




        //
        Document doc = Jsoup.parse(html);

        String avail = "";   //this string will contain the text in the availability id

        //get the text in the availability class. This can throw a null pointer exception.
        try {
            avail = doc.getElementsByClass("_3GOL67 gTLS5r").outerHtml();
        }
        catch (NullPointerException ignored)
        {

        }
        //System.out.println(avail);
        System.out.println(avail);


        //when the product is out of stock, the availability id contains the following text. I can probably improve this code.
        if (avail.equals("<div class=\"_3GOL67 gTLS5r\">\n" +
                " <button class=\"QqFHMw AMnSvF v6sqKe\">NOTIFY ME</button>\n" +
                " <div class=\"IMj1Kv v6sqKe\">\n" +
                "  Get notified when this item comes back in stock.\n" +
                " </div>\n" +
                "</div>")) {
            return false;
        }
        return true;
    }

    public static String getShortURL(String url) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == '?') {
                break;
            }
            res.append(url.charAt(i));
        }
        return res.toString();
    }



    public static double findPriceFlip(String url) throws IOException {
        url = getShortURL(url);


        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
// optional request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String html = response.toString();



        //
        Document doc = Jsoup.parse(html);
        String price = doc.getElementsByClass("Nx9bqj CxhGGd").outerHtml();
        System.out.println("price = " + price);
        //System.out.println(title);
        StringBuilder newPrice = new StringBuilder();

        boolean numFound = false;

        for (int i = 29; i < price.length(); i++) {

            if (numFound && price.charAt(i) == '<') {
                break;
            }

            if (isNumber(price.charAt(i))) {
                if (price.charAt(i) == ',') {
                    continue;
                }
                newPrice.append(price.charAt(i));
                numFound = true;
            }
        }
        System.out.println(Double.parseDouble(String.valueOf(newPrice)));
        return Double.parseDouble(String.valueOf(newPrice));
    }


    public static double findPriceAmz(String url) throws IOException{
        double price = 0;
        Document doc = null;
        Connection conn = Jsoup.connect(url);
        String avail = "";
        Element priceElement = null;
        Elements priceElements = null;
        boolean usedElementsByClass = false;
        conn.userAgent("Mozilla/5.0 (Linux; Android 13; SM-A528B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Mobile Safari/537.36 EdgA/107.0.1418.43");
        try {
            doc = conn.get();
        } catch (Exception ignored) {

        }
        try {
            priceElement = doc.getElementById("corePrice_desktop");
            //System.out.println(priceElement.text());
            avail = priceElement.text();
        } catch (Exception ignored) {
            usedElementsByClass = true;
            priceElements = doc.getElementsByClass("a-price-whole");
            //System.out.println(priceElements.first());
            try {
                avail = priceElements.first().text();
            } catch (Exception e) {
                avail = "<span class=\"a-price-whole\">999999999<span class=\"a-price-decimal\">.</span></span>";
            }
        }

        Log.i("INFO", "ZZZZZZZZZZZZ price = " + avail);

        if (usedElementsByClass) {
            StringBuilder priceStringBuilder = new StringBuilder();
            for (int i = 0; i < avail.length(); i++) {
                Log.i("INFO", "used a-price-whole");
                priceStringBuilder.append(avail.charAt(i));
            }
            String priceString = String.valueOf(priceStringBuilder);
            StringBuilder priceStringBuilder2 = new StringBuilder();
            for (int i = 0; i < priceString.length(); i++) {
                if (priceString.charAt(i) == ',') {
                    continue;
                }
                priceStringBuilder2.append(avail.charAt(i));
            }
            priceString = String.valueOf(priceStringBuilder2);
            price = Double.parseDouble(priceString);
        } else {
            int rsCount = 0;
            int i = 0;
            while (rsCount < 3) {
                if (avail.charAt(i) == '₹') {
                    rsCount++;
                }
                i++;
            }
            StringBuilder priceStringBuilder = new StringBuilder();
            while (avail.charAt(i) != '₹') {
                if (avail.charAt(i) == ',') {
                    i++;
                    //System.out.println('.');
                    continue;
                }
                priceStringBuilder.append(avail.charAt(i));
                //System.out.println(avail.charAt(i));
                i++;
            }
            price = Double.parseDouble(String.valueOf(priceStringBuilder));
        }

        return price;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        ((EditText) findViewById(R.id.URL)).setText(intent.getStringExtra(Intent.EXTRA_TEXT));

        //EditText outputFinal = (EditText) findViewById(R.id.output);



    }




    private class MainThread extends Thread {
        public void run() {
            statusString = "Stopped.";
            try {
                ((TextView) findViewById(R.id.status)).setText(statusString);
            } catch (Exception ignored) {
            }

            EditText url = (EditText) findViewById(R.id.URL);
            EditText price = (EditText) findViewById(R.id.price);

            Uri linkToProduct = Uri.parse(url.getText().toString());
            Intent openProductPage = new Intent(Intent.ACTION_VIEW);
            openProductPage.setData(linkToProduct); //this opens the product url

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
            stackBuilder.addNextIntentWithParentStack(openProductPage);

            PendingIntent openProductPagePendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch sw = (Switch) findViewById(R.id.switch1);





            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("PriceStock", "PriceStock", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }


            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "PriceStock");
            builder.setSmallIcon(R.drawable.logo);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setAutoCancel(true);
            builder.setContentIntent(openProductPagePendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);



            if (sw.isChecked()) {

                try {
                    if (url.getText().toString().charAt(12) != 'f' && url.getText().toString().charAt(12) != 'a') {
                        statusString = "Error. Check URL";
                        try {
                            ((TextView) findViewById(R.id.status)).setText(statusString);
                        } catch (Exception ignored) {
                        }
                        isStarted = false;
                        return;
                    }
                } catch (Exception e) {
                    statusString = "Error. Check URL";
                    try {
                        ((TextView) findViewById(R.id.status)).setText(statusString);
                    } catch (Exception ignored) {
                    }
                    isStarted = false;
                    return;
                }
                try {
                    linkIsForFlipkart = url.getText().toString().charAt(12) == 'f';
                } catch (Exception ignored) {
                }

                statusString = "Started. Checking Stock.";
                try {
                    ((TextView) findViewById(R.id.status)).setText(statusString);
                } catch (Exception ignored) {
                }

                while (true) {

                    if (!isStarted) {
                        break;
                    }

                    if (linkIsForFlipkart) {
                        try {
                            inStockVar = inStockFlip(url.getText().toString());
                        } catch (Exception ignored) {
                        }
                    } else {
                        try {
                            inStockVar = inStockAmz(url.getText().toString());
                        } catch (Exception ignored) {
                        }
                    }

                    if (inStockVar) {
                        Log.i("INFO", "In stock");
                        builder.setContentTitle("The item is back in stock");
                        builder.setContentText("Tap to open the product page");
                        notificationManager.notify(1, builder.build());
                        statusString = "Product is in stock.";
                        try {
                            ((TextView) findViewById(R.id.status)).setText(statusString);
                        } catch (Exception ignored) {
                        }
                        break;
                    } else {
                        statusString = "Product Out Of Stock. We will keep checking.";
                        try {
                            ((TextView) findViewById(R.id.status)).setText(statusString);
                        } catch (Exception ignored) {
                        }
                        try {
                            Thread.sleep(checkEveryMilliSec);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            } else {
                try {
                    if (url.getText().toString().charAt(12) != 'f' && url.getText().toString().charAt(12) != 'a') {
                        statusString = "Error. Check URL";
                        try {
                            ((TextView) findViewById(R.id.status)).setText(statusString);
                        } catch (Exception ignored) {
                        }
                        isStarted = false;
                        return;
                    }
                } catch (Exception e) {
                    statusString = "Error. Check URL";
                    try {
                        ((TextView) findViewById(R.id.status)).setText(statusString);
                    } catch (Exception ignored) {
                    }
                    isStarted = false;
                    return;
                }

                try {
                    linkIsForFlipkart = url.getText().toString().charAt(12) == 'f';
                } catch (Exception ignored) {
                }
                while (true) {
                    if (!isStarted) {
                        break;
                    }



                    statusString = "Started. Checking Price.";
                    try {
                        ((TextView) findViewById(R.id.status)).setText(statusString);
                    } catch (Exception ignored) {
                    }

                    if (linkIsForFlipkart) {
                        try {
                            inStockVar = inStockFlip(url.getText().toString());
                        } catch (Exception ignored) {
                        }
                    } else {
                        try {
                            inStockVar = inStockAmz(url.getText().toString());
                        } catch (Exception ignored) {
                        }
                    }

                    if (inStockVar) {
                        try {
                            priceFromApp = Double.parseDouble(price.getText().toString());
                        } catch (Exception e) {
                            statusString = "Error. Make sure the price is a number.";
                            try {
                                ((TextView) findViewById(R.id.status)).setText(statusString);
                            } catch (Exception ignored) {
                            }
                        }
                        if (linkIsForFlipkart) {
                            try {
                                priceFromWebsite = findPriceFlip(url.getText().toString());
                            }
                            catch (Exception ignored) {
                            }
                        } else {
                            try {
                                priceFromWebsite = findPriceAmz(url.getText().toString());
                            }
                            catch (Exception ignored) {
                            }
                        }
                        if (priceFromApp >= priceFromWebsite) {
                            Log.i("VALUES", "Price is low. New price = " + priceFromWebsite);
                            builder.setContentTitle("Price is low at " + priceFromWebsite);
                            builder.setContentText("Tap to open the product page");
                            notificationManager.notify(1, builder.build());
                            statusString = "Stopped";
                            try {
                                ((TextView) findViewById(R.id.status)).setText(statusString);
                            } catch (Exception ignored) {
                            }
                            break;
                        } else {
                            statusString = "Price is high("+ priceFromWebsite +"). We will keep checking.";
                            try {
                                ((TextView) findViewById(R.id.status)).setText(statusString);
                            } catch (Exception ignored) {
                            }
                            Log.i("VALUES", "Price is high. Price = " + priceFromWebsite);
                            Log.i("INFO", "Waiting....");
                            try {
                                Thread.sleep(checkEveryMilliSec);
                            }
                            catch (InterruptedException ignored) {
                            }
                        }
                    } else {
                        statusString = "Product out of stock. We will keep checking.";
                        try {
                            ((TextView) findViewById(R.id.status)).setText(statusString);
                        } catch (Exception ignored) {
                        }
                        try {
                            Thread.sleep(checkEveryMilliSec);
                        }
                        catch (InterruptedException ignored) {
                        }
                    }
                }


            }
        }

    }



    public void startFunc(View view) {

        isStarted = false;
        isStarted = true;

        Log.i("INFO", "start pressed");

        //isStarted = true;

        try {
            mt.interrupt();
        }
        catch (Exception ignored) {

        }

        //mp = new MainProcess();

        //mp.execute();

        mt = new MainThread();
        mt.start();




    }

    public void stopFunc(View view) {
        //isStarted = false;
        //mp.cancel(true);
        //mp = null;

        isStarted = false;

        try {
            mt.interrupt();
        }
        catch (Exception ignored) {
            Log.i("INFO", "interrupt failed.....");
        }

        statusString = "Stopped.";
        try {
            ((TextView) findViewById(R.id.status)).setText(statusString);
        } catch (Exception ignored) {

        }




        Log.i("INFO", "Stop pressed.");
    }


}


