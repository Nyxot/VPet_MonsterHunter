package comnyxot.httpsgithub.vpetmonsterhunter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MonsterLive extends AppCompatActivity {

    private ImageView monster, meatX;
    private int direccionX = 1, direccionY = 1, speed = 20;
    private FrameLayout layMonster;
    private long time;
    private int hunger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_live);

        monster = (ImageView) findViewById(R.id.img_monster);
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        File imgFile = new File(sdCardDirectory.getAbsolutePath(), "myMonster.png");
        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        monster.setImageBitmap(bmp);
        layMonster = (FrameLayout)findViewById(R.id.layMonster);

        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        time = preferencias.getLong("time", 0);
        hunger = preferencias.getInt("hunger", 1);

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);

                        time++;
                        hunger++;
                        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefe.edit();
                        editor.putLong("time", time);
                        editor.putInt("hunger", hunger);
                        editor.commit();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                caminar();
                                if(hunger > 30)
                                {
                                    needEat();
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

    public void showInfo(View v)
    {
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        long currentTime = preferencias.getLong("time", 0);
        Toast.makeText(this, "Pasos: "+ currentTime, Toast.LENGTH_SHORT).show();
    }

    protected void needEat()
    {
        meatX = (ImageView) findViewById(R.id.meatx);
        meatX.setVisibility(View.VISIBLE);

        int x = (int)monster.getX();
        int y = (int)monster.getY();

        if ((x + (monster.getWidth()) >= layMonster.getWidth()) || x <= 0) {
            //monster.setRotationY(monster.getRotationY() + 180);
            meatX.animate().rotationY(monster.getRotationY() + 180);
        }

        /*meatX.setX(x - (speed * direccionX));
        meatX.animate().translationYBy(-2).start();
        meatX.animate().translationYBy(2).start();*/
    }

    public void feed(View v)
    {
        meatX = (ImageView) findViewById(R.id.meatx);
        meatX.setVisibility(View.INVISIBLE);
        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefe.edit();
        editor.putInt("hunger", 0);
        editor.commit();
        hunger = 0;
        Toast.makeText(this,"ñon ñon ñon",Toast.LENGTH_SHORT).show();
    }
}
