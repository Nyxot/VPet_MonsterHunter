package comnyxot.httpsgithub.vpetmonsterhunter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;

public class MonsterLive extends AppCompatActivity {

    private ImageView monster, meatX, crapX, sleepX, sleepicon;
    private int direccionX = 1, direccionY = 1, speed = 20, hunger, crap, sleep, minute = 60, hour = 3600;
    private FrameLayout layMonster;
    private long time;
    private boolean live = true, move, sleeping;
    private String nameMonster, nameHunter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_live);

        monster = (ImageView) findViewById(R.id.img_monster);
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        File imgFile = new File(sdCardDirectory.getAbsolutePath(), "myMonster.png");
        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        monster.setImageBitmap(bmp);
        layMonster = (FrameLayout) findViewById(R.id.layMonster);

        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        time = preferencias.getLong("time", 0);
        hunger = preferencias.getInt("hunger", 0);
        crap = preferencias.getInt("crap", 0);
        sleep = preferencias.getInt("sleep", 0);
        nameMonster = preferencias.getString("monsterTamed", "");
        nameHunter = preferencias.getString("nameHunter", "");

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);

                        time++;
                        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefe.edit();
                        editor.putLong("time", time);
                        editor.putInt("hunger", hunger);
                        editor.putInt("crap", crap);
                        editor.putInt("sleep", sleep);
                        editor.apply();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (die() != true) {
                                    live = true;

                                    if (areSleep() != true) {
                                        caminar();
                                        if (sleep > (hour * 8)) {
                                            needSleep();
                                        } else {
                                            sleep++;
                                        }
                                        if (hunger > hour) {
                                            needEat();
                                        } else {
                                            hunger++;
                                        }
                                        if (crap > (hour * 3)) {
                                            needWash();
                                        } else {
                                            crap++;
                                        }
                                    }
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    protected void caminar() {

        int x = (int) monster.getX();
        int y = (int) monster.getY();

        if (sleeping == true) {
        } else {
            if (move == false) {
                if ((x + (monster.getWidth()) >= layMonster.getWidth()) || x <= 0) {
                    direccionX *= -1;
                    //monster.setRotationY(monster.getRotationY() + 180);
                    monster.animate().rotationY(monster.getRotationY() + 180);
                }
                if ((y + (monster.getHeight()) >= layMonster.getHeight()) || (y <= 300)) {
                    direccionY *= -1;
                }

                monster.setX(x - (speed * direccionX));
                monster.animate().translationYBy(-3 * direccionY).start();
                monster.animate().translationYBy(+3 * direccionY).start();
                monster.setY(y);
            } else {
                move = false;
            }
        }

    }

    public void showInfo(View v) {
        if (live == true) {
            SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
            long currentTime = preferencias.getLong("time", 0);
            int currentHunger = preferencias.getInt("hunger", 0);
            int currentCrap = preferencias.getInt("crap", 0);
            int currentSleep = preferencias.getInt("sleep", 0);
            double porcentajeHunger = 0.02777777777777777777777777777778;
            double porcentajeCrap = 0.00925925925925925925925925925926;
            double porcentajeSleep = 0.00347222222222222222222222222222;
            MediaPlayer mp = MediaPlayer.create(this, R.raw.notification);
            mp.start();
            DecimalFormat var = new DecimalFormat("#.###");
            Toast.makeText(this, "Pasos: " + currentTime + "\nHambre: " + var.format(currentHunger * porcentajeHunger) +
                            "%\nCrap: " + var.format(currentCrap * porcentajeCrap) +
                            "%\nSleep: " + var.format(currentSleep * porcentajeSleep) + "%"
                    , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "your monster die", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    protected void needEat() {
        meatX = (ImageView) findViewById(R.id.meatx);
        meatX.setVisibility(View.VISIBLE);

        int x = (int) monster.getX();
        int y = (int) monster.getY();

        meatX.setX(x - (speed * direccionX));
        meatX.animate().translationYBy(-3 * direccionY).start();
        meatX.animate().translationYBy(+3 * direccionY).start();
        meatX.setY(y);

        sendNotification();
    }

    public void feed(View v) {
        meatX = (ImageView) findViewById(R.id.meatx);
        meatX.setVisibility(View.INVISIBLE);
        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefe.edit();
        editor.putInt("hunger", 0);
        editor.apply();
        hunger = 0;
        MediaPlayer mp = MediaPlayer.create(this, R.raw.notification);
        mp.start();
        Toast.makeText(this, "ñom ñom ñom", Toast.LENGTH_SHORT).show();
    }

    protected void needWash() {
        crapX = (ImageView) findViewById(R.id.crap);
        crapX.setVisibility(View.VISIBLE);

        sendNotification();
    }

    public void wash(View v) {
        crapX = (ImageView) findViewById(R.id.crap);
        crapX.setVisibility(View.INVISIBLE);
        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefe.edit();
        editor.putInt("crap", 0);
        editor.apply();
        crap = 0;
        MediaPlayer mp = MediaPlayer.create(this, R.raw.notification);
        mp.start();
        Toast.makeText(this, "Clean!", Toast.LENGTH_SHORT).show();
    }

    protected void needSleep() {
        sleepX = (ImageView) findViewById(R.id.sleepX);
        sleepX.setVisibility(View.VISIBLE);

        sendNotification();
    }

    public void sleep(View v) {
        if (sleeping == false) {
            sleeping = true;
            sleepX = (ImageView) findViewById(R.id.sleepX);
            sleepX.setVisibility(View.INVISIBLE);
            areSleep();
        } else {
            sleepX = (ImageView) findViewById(R.id.sleepX);
            sleepX.setVisibility(View.INVISIBLE);
            sleepicon = (ImageView) findViewById(R.id.sleepicon);
            sleepicon.setVisibility(View.INVISIBLE);
            sleeping = false;
        }
    }

    public boolean areSleep() {

        if (sleeping == true) {
            int x = (int) monster.getX();
            int y = (int) monster.getY();


            sleepicon = (ImageView) findViewById(R.id.sleepicon);
            sleepicon.setVisibility(View.VISIBLE);
            sleepicon.setX(x - (speed * direccionX));
            sleepicon.setY(y);
            sleepicon.animate().translationYBy(-3).start();
            sleepicon.animate().translationYBy(+3).start();

            sleep--;
            if (sleep == 0) {
                sleeping = false;
                sleepicon.setVisibility(View.INVISIBLE);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    protected boolean die() {
        if (hunger > (hour * 12)) {

            monster.animate().rotationX(180);
            SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefe.edit();
            editor.putLong("time", 0);
            editor.putInt("hunger", 0);
            editor.putInt("crap", 0);
            editor.putInt("sleep", 0);
            editor.apply();

            live = false;

            sendNotification();
            return true;
        } else {
            return false;
        }
    }

    public void touchMonster(View v) {
        move = true;
        int x = (int) monster.getX();
        int y = (int) monster.getY();

        /*monster.animate().cancel();
        monster.animate().rotation(monster.getRotation() + 360);
        monster.animate().translationYBy(-100);
        monster.animate().translationY(0);*/
        //monster.setTranslationY(100 * -1);
        monster.animate()
                .rotation(monster.getRotation() + 360)
                .translationYBy(-100)
                .translationY(0)
                .setDuration(500)
                .start();
    }

    @Override
    public void onBackPressed() {
        //finish();
    }

    public void sendNotification() {

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.mipmap.ic_launcher);

        // This intent is fired when notification is clicked
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle("Your Monster Call You");

        // Content text, which appears in smaller text below the title
        builder.setContentText("GGGGRRRRRR!!!");

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText("");
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setDefaults(Notification.FLAG_AUTO_CANCEL);
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(0, builder.build());
    }
}
