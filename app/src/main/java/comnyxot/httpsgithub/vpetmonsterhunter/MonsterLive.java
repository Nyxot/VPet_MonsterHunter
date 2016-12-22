package comnyxot.httpsgithub.vpetmonsterhunter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;

import java.io.File;

public class MonsterLive extends AppCompatActivity {

    private ImageView monster;
    private int direccionX = 1, direccionY = 1, speed = 20;
    private Point screen = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_live);

        monster = (ImageView) findViewById(R.id.img_monster);
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        File imgFile = new File(sdCardDirectory.getAbsolutePath(), "myMonster.png");
        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        monster.setImageBitmap(bmp);

        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screen);

        /*MonsterLive.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        respirar();
                    }
                }, 1, 1000/fps);
            }
        });*/
        //respirar();

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                respirar();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    protected void respirar() {

        int x = (int) monster.getX();
        int y = (int) monster.getY();

        if ((x + (monster.getWidth()) >= screen.x) || x <= 0) {
            direccionX *= -1;
            monster.setRotationY(monster.getRotationY() + 180);
        }
        if ((y + (monster.getHeight()) >= screen.y) || (y <= 0)) {
            direccionY *= -1;
        }

        monster.setX(x - (speed * direccionX));
        //monster.setY(y - (speed * direccionY));
    }
}
