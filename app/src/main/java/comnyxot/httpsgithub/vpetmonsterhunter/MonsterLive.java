package comnyxot.httpsgithub.vpetmonsterhunter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MonsterLive extends AppCompatActivity {

    private ImageView monster, meatX, crapX;
    private int direccionX = 1, direccionY = 1, speed = 20;
    private FrameLayout layMonster;
    private long time;
    private int hunger, crap, minute = 60, hour = 3600;
    private boolean live;

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
        hunger = preferencias.getInt("hunger", 1);
        crap = preferencias.getInt("crap", 1);

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);

                        time++;
                        hunger++;
                        crap++;
                        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefe.edit();
                        editor.putLong("time", time);
                        editor.putInt("hunger", hunger);
                        editor.putInt("crap", crap);
                        editor.commit();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (die() != true) {
                                    live = true;
                                    caminar();
                                }
                                //caminar();
                                if (hunger > (minute * 5)) {
                                    needEat();
                                }
                                if (crap > (hour)) {
                                    needWash();
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

        if ((x + (monster.getWidth()) >= layMonster.getWidth()) || x <= 0) {
            direccionX *= -1;
            //monster.setRotationY(monster.getRotationY() + 180);
            monster.animate().rotationY(monster.getRotationY() + 180);
        }
        if ((y + (monster.getHeight()) >= layMonster.getHeight()) || (y <= 200)) {
            direccionY *= -1;
        }

        monster.setX(x - (speed * direccionX));
        monster.animate().translationYBy(-3 * direccionY).start();
        monster.animate().translationYBy(+3 * direccionY).start();
        monster.setY(y);
    }

    public void showInfo(View v) {
        if (live == true) {
            SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
            long currentTime = preferencias.getLong("time", 0);
            MediaPlayer mp = MediaPlayer.create(this, R.raw.notification);
            mp.start();
            Toast.makeText(this, "Pasos: " + currentTime, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "your monster die", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    protected void needEat() {
        meatX = (ImageView) findViewById(R.id.meatx);
        meatX.setVisibility(View.VISIBLE);

        int x = (int) monster.getX();
        int y = (int) monster.getY();

        if ((x + (monster.getWidth()) >= layMonster.getWidth()) || x <= 0) {
            //monster.setRotationY(monster.getRotationY() + 180);
            meatX.animate().rotationY(monster.getRotationY() + 180);
        }
        if ((y + (monster.getHeight()) >= layMonster.getHeight()) || (y <= 200)) {
            direccionY *= -1;
        }

        meatX.setX(x - (speed * direccionX));
        meatX.animate().translationYBy(-3 * direccionY).start();
        meatX.animate().translationYBy(+3 * direccionY).start();
        meatX.setY(y);
    }

    public void feed(View v) {
        meatX = (ImageView) findViewById(R.id.meatx);
        meatX.setVisibility(View.INVISIBLE);
        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefe.edit();
        editor.putInt("hunger", 0);
        editor.commit();
        hunger = 0;
        MediaPlayer mp = MediaPlayer.create(this, R.raw.notification);
        mp.start();
        Toast.makeText(this, "ñon ñon ñon", Toast.LENGTH_SHORT).show();
    }

    protected void needWash() {
        crapX = (ImageView) findViewById(R.id.crap);
        crapX.setVisibility(View.VISIBLE);
    }

    public void wash(View v) {
        crapX = (ImageView) findViewById(R.id.crap);
        crapX.setVisibility(View.INVISIBLE);
        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefe.edit();
        editor.putInt("crap", 0);
        editor.commit();
        crap = 0;
        MediaPlayer mp = MediaPlayer.create(this, R.raw.notification);
        mp.start();
        Toast.makeText(this, "Clean!", Toast.LENGTH_SHORT).show();
    }

    protected boolean die() {
        if (hunger > (hour * 8)) {

            monster.animate().rotationX(180);
            SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefe.edit();
            editor.putLong("time", 0);
            editor.putInt("hunger", 0);
            editor.putInt("crap", 0);
            editor.commit();

            live = false;
            return true;
        } else {
            return false;
        }
    }
}
