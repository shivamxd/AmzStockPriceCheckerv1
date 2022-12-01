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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public boolean inStockVar = false;
    public double priceFromWebsite = 999999999;
    public boolean isStarted;
    public double priceFromApp = 999999999;
    public boolean linkIsForFlipkart = false;
    public int checkEveryMilliSec = 120000;
    //MainProcess mp;
    MainThread mt;
    //String statusString = "Stopped";
    //String stoppedString = "Stopped";
    //String startedString = "Started";
    public String statusString = "...";




    //this function checks if the character passed to it is a part of a number
    public static boolean isNumber(char c) {
        if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0' || c == '.' || c == ',') {
            return true;
        }
        return false;
    }


    public static boolean inStockAmzOld(String url) {
        Document doc = null;
        Connection conn = Jsoup.connect(url);
        String avail = "";
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        try {
            Element availability = doc.getElementById("availability");
            avail = availability.text();
        } catch (Exception ignored) {

        }
        return avail.equals("In stock.");
    }

    public static boolean inStockAmz(String url) {
        Document doc = null;
        Connection conn = Jsoup.connect(url);
        String avail = "";
        //conn.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Safari/537.36 Edg/104.0.1293.54");
        conn.userAgent("Mozilla/5.0 (Linux; Android 13; SM-A528B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Mobile Safari/537.36 EdgA/107.0.1418.43");
        try {
            doc = conn.get();
        } catch (Exception ignored) {

        }
        try {
            Elements availability = doc.getElementsByClass("a-size-medium a-color-price");
            avail = availability.text();
        } catch (Exception ignored) {

        }
        //System.out.println(avail);
        //return avail.equals("In stock.");
        boolean inStock = false;
        try {
            inStock = !(avail.charAt(0) == 'C');
        } catch (Exception e) {
            return true;
        }
        return inStock;
    }


    //this function returns true or false based on if the product is in stock
    /*public static boolean inStockAmz2(String url){
        Document doc = null;  //create a document variable

        //connect to the url. this can produce an IO exception, so we need to deal with it
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        String avail = "";   //this string will contain the text in the availability id

        //get the text in the availability class. This can throw a null pointer exception.
        try {
            avail = doc.getElementById("availability").outerHtml();
        }
        catch (NullPointerException ignored)
        {

        }

        if (avail.isEmpty()) {
            try {
                avail = doc.getElementsByClass("a-section a-spacing-base }").outerHtml();
            } catch (Exception ignored) {

            }
        }
        //System.out.println("Avail = " + avail);
        //System.out.println(avail);


        //when the product is out of stock, the availability id contains the following text. I can probably improve this code.
        *//*if (avail.equals("<div id=\"availability\" class=\"a-section a-spacing-base }\"> <span class=\"a-size-medium a-color-price\"> Currently unavailable. </span> \n" +
                " <br>We don't know when or if this item will be back in stock. \n" +
                "</div>")) {
            return false;
        }*//*

        String[] availArray = avail.split(" ");

        boolean foundCurrently = false;
        //boolean foundIn = false;
        //boolean foundstock = false;


        for (String s : availArray) {
            *//*if (foundIn && foundstock) {
                return true;
            }*//*
            if (foundCurrently) {
                return false;
            }
            if (s.equals("Currently")) {
                foundCurrently = true;
            }
            *//*if (s.equals("In")) {
                foundIn = true;
            }
            if (s.equals("stock")) {
                foundstock = true;
            }*//*
        }
        return true;
    }*/

    public static boolean inStockFlip(String url){
        Document doc = null;  //create a document variable

        //connect to the url. this can produce an IO exception, so we need to deal with it
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        String avail = "";   //this string will contain the text in the availability id

        //get the text in the availability class. This can throw a null pointer exception.
        try {
            avail = doc.getElementsByClass("_2KpZ6l _2uS5ZX _2Dfasx").outerHtml();
        }
        catch (NullPointerException ignored)
        {

        }
        //System.out.println(avail);


        //when the product is out of stock, the availability id contains the following text. I can probably improve this code.
        if (avail.equals("<button class=\"_2KpZ6l _2uS5ZX _2Dfasx\">NOTIFY ME</button>")) {
            return false;
        }
        return true;
    }

    /*public static double findPrice(String url){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String price = "";
        try {
            price = doc.getElementsByClass("a-price-whole").outerHtml();
        } catch (NullPointerException ignored) {
            Log.i("INFO", "caught null exception 1");
        }

        if (price.isEmpty()) {
            try {
                price = doc.getElementsByClass("a-offscreen").outerHtml();
            } catch (NullPointerException ignored) {
                Log.i("INFO", "caught null exception 2");
                price = "99999999999";
            }
        }

        Log.i("INFO", "XXXXXXXXXXX price = " + price);

        //System.out.println(title);
        StringBuilder newPrice = new StringBuilder();

        boolean numFound = false;

        for (int i = 0; i < price.length(); i++) {

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
        Log.i("INFO", "ZZZZZZZZZZZZ price = " + newPrice);
        return Double.parseDouble(String.valueOf(newPrice));
    }*/

    /*public static double findPrice2(String url){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String price = "";
        try {
            price = doc.getElementsByClass("a-price-whole").outerHtml();
        } catch (Exception e) {

        }

        //System.out.println("price = " + price);
        //System.out.println(title);
        StringBuilder newPrice = new StringBuilder();

        boolean numFound = false;

        for (int i = 0; i < price.length(); i++) {

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
        return Integer.parseInt(String.valueOf(newPrice));
    }*/

    public static double findPriceFlip(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String price = doc.getElementsByClass("_30jeq3 _16Jk6d").outerHtml();
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
        conn.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Safari/537.36 Edg/104.0.1293.54");
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

        if (usedElementsByClass) {
            StringBuilder priceStringBuilder = new StringBuilder();
            for (int i = 0; i < avail.length() - 1; i++) {
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
    /*public static double findPriceAmz2(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String price = doc.getElementsByClass("a-price-whole").outerHtml();

        Log.i("INFO", "price a price whole = " + price);
        //System.out.println("price = " + price);
        //System.out.println(title);
        if (price.isEmpty()) {
            Log.i("INFO", "PRICE IS EMPTYYYYYYYY");
            price = doc.getElementsByClass("a-offscreen").outerHtml();
            Log.i("INFO", "off screen price = " + price);
        }
        StringBuilder newPrice = new StringBuilder();

        boolean numFound = false;

        for (int i = 0; i < price.length(); i++) {

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
        Log.i("INFO", "33333333333 price = " + String.valueOf(newPrice));
        if (String.valueOf(newPrice).isEmpty()) {
            return 99999999;
        }
        return Double.parseDouble(String.valueOf(newPrice));
    }*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("INFO", "This app was created by Shivam. Twitter - @shivam_ggwp");






        //EditText outputFinal = (EditText) findViewById(R.id.output);



    }

    /*private class CheckStock extends AsyncTask<Void, Void, Void> {
        boolean stock;
        @Override
        protected void onPreExecute() {
           // super.OnPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            EditText url = (EditText) findViewById(R.id.URL);
            stock = inStock(url.getText().toString());

           // stockVar = stock;


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            stockVar = stock;
        }
    }


    private class PriceFind extends AsyncTask<Void, Void, Void>{
        double price;
        @Override
        protected void onPreExecute() {
            // super.OnPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            EditText url = (EditText) findViewById(R.id.URL);
            boolean stock = inStock(url.getText().toString());
            //double price;

            if (stock) {
                price = findPrice(url.getText().toString());
            } else {
                price = -1;
            }

            //finalPrice = price;


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finalPrice = price;
        }
    }*/

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

    /*private class MainProcess extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // super.OnPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids) {



            EditText url = (EditText) findViewById(R.id.URL);
            EditText price = (EditText) findViewById(R.id.price);

            double priceDouble = Double.parseDouble(price.getText().toString());

            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch sw = (Switch) findViewById(R.id.switch1);

            boolean isFlip = false;

            if (url.getText().toString().charAt(12) == 'f') {
                isFlip = true;
            }




        *//*CheckStock cs = new CheckStock();
        cs.execute();

        if (stockVar) {
            Log.i("INFO", "In stock");
        } else {
            Log.i("INFO", "Not in stock");
        }*//*


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("PriceStock", "PriceStock", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }


            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "PriceStock");
            builder.setSmallIcon(R.drawable.logo);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);



            //test code

            *//*Intent resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setData(Uri.parse(url.getText().toString()));

            PendingIntent pending = PendingIntent.getActivity(MainActivity.this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pending);*//*



            //test code end




            if (sw.isChecked()) {
                while (true) {
                    if (!isStarted) {
                        break;
                    }
                    if (isFlip) {
                        stockVar = inStockFlip(url.getText().toString());
                    } else {
                        stockVar = inStockAmz(url.getText().toString());
                    }
                    if (stockVar) {
                        Log.i("INFO", "In stock");
                        builder.setContentTitle("The item is back in stock");
                        builder.setContentText("Tap to open the product page.");
                        notificationManager.notify(1, builder.build());
                        isStarted = false;
                        break;
                    } else {
                        Log.i("INFO", "Not in stock");
                        try {
                            TimeUnit.SECONDS.sleep(120);
                        }
                        catch (InterruptedException ignored) {

                        }

                    }
                }
                *//*CheckStock cs = new CheckStock();
                cs.execute();*//*

            } else {
                while (true) {
                    if (!isStarted) {
                        break;
                    }
                    if (isFlip) {
                        stockVar = inStockFlip(url.getText().toString());
                    } else {
                        stockVar = inStockAmz(url.getText().toString());
                    }
                    if (stockVar) {
                        try {
                            if (isFlip) {
                                finalPrice = findPriceFlip(url.getText().toString());
                            } else {
                                finalPrice = findPriceAmz(url.getText().toString());
                            }
                        } catch (IOException e) {
                            //e.printStackTrace();
                        }
                    } else {
                        finalPrice = -1;
                    }
                    if (stockVar) {
                        if (finalPrice >= 0) {
                            if (finalPrice <= priceDouble) {
                                Log.i("VALUES", "Price is low. New price = " + finalPrice);
                                builder.setContentTitle("Price is low.");
                                builder.setContentText("Current price = " + finalPrice);
                                notificationManager.notify(1, builder.build());
                                isStarted = false;
                                break;
                            } else {
                                Log.i("VALUES", "Price is high. Price = " + finalPrice);
                                Log.i("INFO", "Waiting....");
                                try {
                                    TimeUnit.SECONDS.sleep(120);
                                }
                                catch (InterruptedException ignored) {

                                }
                            }
                        } else {
                            Log.i("INFO", "Out of stock");
                            Log.i("INFO", "Waiting....");
                            try {
                                TimeUnit.SECONDS.sleep(120);
                            }
                            catch (InterruptedException ignored) {

                            }
                        }
                    }
                }
                *//*PriceFind pf = new PriceFind();
                pf.execute();*//*

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }*/

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


