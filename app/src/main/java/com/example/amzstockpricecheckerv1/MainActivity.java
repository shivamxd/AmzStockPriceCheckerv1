package com.example.amzstockpricecheckerv1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.loader.content.AsyncTaskLoader;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public boolean stockVar;
    public double finalPrice;
    public boolean isStarted;
    MainProcess mp;


    //this function checks if the character passed to it is a part of a number
    public static boolean isNumber(char c) {
        if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0' || c == '.' || c == ',') {
            return true;
        }
        return false;
    }


    //this function returns true or false based on if the product is in stock
    public static boolean inStockAmz(String url){
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
        //System.out.println(avail);


        //when the product is out of stock, the availability id contains the following text. I can probably improve this code.
        /*if (avail.equals("<div id=\"availability\" class=\"a-section a-spacing-base }\"> <span class=\"a-size-medium a-color-price\"> Currently unavailable. </span> \n" +
                " <br>We don't know when or if this item will be back in stock. \n" +
                "</div>")) {
            return false;
        }*/

        String[] availArray = avail.split(" ");

        boolean foundCurrently = false;
        //boolean foundIn = false;
        //boolean foundstock = false;


        for (String s : availArray) {
            /*if (foundIn && foundstock) {
                return true;
            }*/
            if (foundCurrently) {
                return false;
            }
            if (s.equals("Currently")) {
                foundCurrently = true;
            }
            /*if (s.equals("In")) {
                foundIn = true;
            }
            if (s.equals("stock")) {
                foundstock = true;
            }*/
        }
        return true;
    }

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

    public static double findPriceAmz(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String price = doc.getElementsByClass("a-price-whole").outerHtml();
        Log.i("INFO", "price a price whole = " + price);
        //System.out.println("price = " + price);
        //System.out.println(title);
        if (price.isEmpty()) {
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
    }


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

    private class MainProcess extends AsyncTask<Void, Void, Void> {
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

        /*CheckStock cs = new CheckStock();
        cs.execute();

        if (stockVar) {
            Log.i("INFO", "In stock");
        } else {
            Log.i("INFO", "Not in stock");
        }*/


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

            /*Intent resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setData(Uri.parse(url.getText().toString()));

            PendingIntent pending = PendingIntent.getActivity(MainActivity.this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pending);*/



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
                /*CheckStock cs = new CheckStock();
                cs.execute();*/

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
                /*PriceFind pf = new PriceFind();
                pf.execute();*/

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    public void startFunc(View view) {

        /*EditText url = (EditText) findViewById(R.id.URL);
        EditText price = (EditText) findViewById(R.id.price);

        double priceInt = Double.parseDouble(price.getText().toString());

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch sw = (Switch) findViewById(R.id.switch1);

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








        if (sw.isChecked()) {
            CheckStock cs = new CheckStock();
            cs.execute();
            if (stockVar) {
                Log.i("INFO", "In stock");
                builder.setContentTitle("The item is back in stock");
                builder.setContentText("Tap to open the product page.");
                notificationManager.notify(1, builder.build());
            } else {
                Log.i("INFO", "Not in stock");
            }
        } else {
            PriceFind pf = new PriceFind();
            pf.execute();
            if (stockVar) {
                if (finalPrice >= 0) {
                    if (finalPrice <= priceInt) {
                        Log.i("VALUES", "Price is low. New price = " + finalPrice);
                        builder.setContentTitle("Price is low.");
                        builder.setContentText("Current price = " + finalPrice);
                        notificationManager.notify(1, builder.build());
                    } else {
                        Log.i("VALUES", "Price is high. Price = " + finalPrice);
                    }
                } else {
                    Log.i("INFO", "Out of stock");
                }
            }
        }*/

        Log.i("INFO", "start pressed");

        isStarted = true;

        mp = new MainProcess();

        mp.execute();





        //Log.i("Values", url.getText().toString());
        //Log.i("Values", price.getText().toString());

        

    }

    public void stopFunc(View view) {
        isStarted = false;
        mp.cancel(true);
        mp = null;

        Log.i("INFO", "Stop pressed.");
    }


}


