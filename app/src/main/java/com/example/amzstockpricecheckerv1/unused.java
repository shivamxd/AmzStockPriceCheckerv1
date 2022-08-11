/*
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class unused {

    private class MainThread extends Thread {
        public void run() {

            try {
                ((TextView) findViewById(R.id.status)).setText(startedString);
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


            double priceDouble = 99999999;

            boolean isFlip;

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
                    isFlip = url.getText().toString().charAt(12) == 'f';
                } catch (Exception e) {
                    return;
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
                    if (isFlip) {
                        try {
                            stockVar = inStockFlip(url.getText().toString());
                        }
                        catch (Exception e) {
                            return;
                        }
                    } else {
                        try {
                            stockVar = inStockAmz(url.getText().toString());
                        }
                        catch (Exception e) {
                            return;
                        }
                    }
                    if (stockVar) {
                        Log.i("INFO", "In stock");
                        builder.setContentTitle("The item is back in stock");
                        builder.setContentText("Tap to open the product page");
                        notificationManager.notify(1, builder.build());
                        isStarted = false;
                        try {
                            ((TextView) findViewById(R.id.status)).setText(stoppedString);
                        } catch (Exception ignored) {

                        }
                        break;
                    } else {
                        statusString = "Product Out Of Stock. We will keep checking.";
                        try {
                            ((TextView) findViewById(R.id.status)).setText(statusString);
                        } catch (Exception ignored) {

                        }
                        Log.i("INFO", "Not in stock");
                        try {
                            Thread.sleep(120000);
                        }
                        catch (InterruptedException ignored) {

                        }

                    }
                }
                */
/*CheckStock cs = new CheckStock();
                cs.execute();*//*


            } else {
                try {
                    isFlip = url.getText().toString().charAt(12) == 'f';
                } catch (Exception e) {
                    return;
                }
                try {
                    priceDouble = Double.parseDouble(price.getText().toString());
                } catch (Exception e) {
                    return;
                }
                statusString = "Checking price.";
                try {
                    ((TextView) findViewById(R.id.status)).setText(statusString);
                } catch (Exception ignored) {

                }
                while (true) {
                    if (!isStarted) {
                        break;
                    }
                    if (isFlip) {
                        try {
                            stockVar = inStockFlip(url.getText().toString());
                        }
                        catch (Exception e) {
                            return;
                        }
                    } else {
                        try {
                            stockVar = inStockAmz(url.getText().toString());
                        }
                        catch (Exception e) {
                            return;
                        }
                    }
                    if (stockVar) {
                        try {
                            if (isFlip) {
                                try {
                                    finalPrice = findPriceFlip(url.getText().toString());
                                }
                                catch (Exception e) {
                                    return;
                                }
                            } else {
                                try {
                                    finalPrice = findPriceAmz(url.getText().toString());
                                }
                                catch (Exception e) {
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    } else {
                        finalPrice = -1;
                    }
                    if (stockVar) {
                        if (finalPrice >= 0) {
                            if (finalPrice <= priceDouble) {
                                Log.i("VALUES", "Price is low. New price = " + finalPrice);
                                builder.setContentTitle("Price is low at " + finalPrice);
                                builder.setContentText("Tap to open the product page");
                                notificationManager.notify(1, builder.build());
                                startedString = "Stopped";
                                try {
                                    ((TextView) findViewById(R.id.status)).setText(statusString);
                                } catch (Exception ignored) {

                                }
                                isStarted = false;
                                break;
                            } else {
                                statusString = "Price is high. We will keep checking.";
                                try {
                                    ((TextView) findViewById(R.id.status)).setText(statusString);
                                } catch (Exception ignored) {

                                }
                                Log.i("VALUES", "Price is high. Price = " + finalPrice);
                                Log.i("INFO", "Waiting....");
                                try {
                                    Thread.sleep(120000);
                                }
                                catch (InterruptedException ignored) {

                                }
                            }
                        } else {
                            Log.i("INFO", "Out of stock");
                            Log.i("INFO", "Waiting....");
                            try {
                                Thread.sleep(120000);
                            }
                            catch (InterruptedException ignored) {

                            }
                        }
                    }
                }
                */
/*PriceFind pf = new PriceFind();
                pf.execute();*//*


            }

            startedString = "Stopped";
            try {
                ((TextView) findViewById(R.id.status)).setText(statusString);
            } catch (Exception ignored) {

            }
            isStarted = false;

        }
    }
}
*/
