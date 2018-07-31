package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

public class UserStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);

        ImageView img_development = findViewById(R.id.img_development);
        ArcProgress progress_experience = findViewById(R.id.progress_exprience);
        TextView txt_develope = findViewById(R.id.txt_develope);


        ProgressDialog pDialog = new ProgressDialog(UserStatusActivity.this);
        pDialog.setCancelable(false);

        System.out.println("user development   ::::-" + Constant.USER_DEVELOPMENT);

        switch (Constant.USER_DEVELOPMENT) {

            case 0:
                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.level1);
                img_development.setImageBitmap(icon);
                txt_develope.setText("Beginner");
                break;

            case 1:
                Bitmap icon2 = BitmapFactory.decodeResource(getResources(),
                        R.drawable.level2);
                img_development.setImageBitmap(icon2);
                txt_develope.setText("Intermediate");
                break;
            case 2:
                Bitmap icon3 = BitmapFactory.decodeResource(getResources(),
                        R.drawable.level3);
                img_development.setImageBitmap(icon3);
                txt_develope.setText("Professional");
                break;
            case 3:
                Bitmap icon4 = BitmapFactory.decodeResource(getResources(),
                        R.drawable.level4);
                img_development.setImageBitmap(icon4);
                txt_develope.setText("Elite");
                break;

        }


        progress_experience.setProgress(Constant.USER_EXPERIENCE * 2);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
