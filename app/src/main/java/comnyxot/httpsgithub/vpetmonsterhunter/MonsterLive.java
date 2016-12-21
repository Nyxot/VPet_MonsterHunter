package comnyxot.httpsgithub.vpetmonsterhunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class MonsterLive extends AppCompatActivity {

    private ImageView monster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_live);

        monster = (ImageView)findViewById(R.id.img_monster);
        File imgFile = new File("/storage/emulated/0/myMonster.png");
        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        monster.setImageBitmap(bmp);
        //respirar();
    }

    protected void respirar()
    {
        int i=0;
        do{
            try {
                Thread.sleep(1000);
                //monster.invalidate();
                monster.drawableHotspotChanged(monster.getX(), monster.getY()+10);
                Thread.sleep(1000);
                //monster.invalidate();
                monster.drawableHotspotChanged(monster.getX(), monster.getY()-10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }while(i<100);
    }
}
