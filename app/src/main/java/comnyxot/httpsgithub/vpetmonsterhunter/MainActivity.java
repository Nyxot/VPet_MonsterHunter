package comnyxot.httpsgithub.vpetmonsterhunter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageButton monster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        if(preferencias.getBoolean("monster", false) != false)
        {
            Intent i = new Intent(this, MonsterLive.class);
            startActivity(i);
        }
    }

    public void selectMonster(View view)
    {
        monster = (ImageButton)findViewById(view.getId());
        BitmapDrawable drawable = (BitmapDrawable)monster.getDrawable();
        Bitmap bmp = drawable.getBitmap();

        File sdCardDirectory = Environment.getExternalStorageDirectory();
        File image = new File(sdCardDirectory,"myMonster.png");

        boolean sucess = false;
        FileOutputStream outStream;
        try
        {
            outStream = new FileOutputStream(image);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            sucess = true;
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        if(sucess)
        {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.gamestart);
            mp.start();
            Toast.makeText(this, "Monster tamed!", Toast.LENGTH_SHORT).show();
        }else
        {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.gamestart);
            mp.start();
            Toast.makeText(this, "Opps, monster bytes you", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefe.edit();
        editor.putBoolean("monster", true);
        editor.putString("monsterTamed", monster.getTag().toString());
        editor.commit();

        Intent i = new Intent(this, MonsterLive.class);
        startActivity(i);
    }
}
