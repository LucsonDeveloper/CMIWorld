package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {
    private final int PICK_IMAGE_MULTIPLE = 1;
    private LinearLayout lnrImages;
    private ArrayList<String> imagesPathList;
    private Bitmap yourbitmap;
    private Bitmap resized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        lnrImages = findViewById(R.id.lnrImages);
        Button btnAddPhots = findViewById(R.id.btnAddPhots);
        Button btnSaveImages = findViewById(R.id.btnSaveImages);
        btnAddPhots.setOnClickListener(this);
        btnSaveImages.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddPhots:
                Intent intent = new Intent(ImageActivity.this, custome_gallery.class);
                startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
                break;
            case R.id.btnSaveImages:
                if (imagesPathList != null) {
                    onBackPressed();
                } else {
                    Toast.makeText(ImageActivity.this, " no images are selected", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_MULTIPLE) {
                imagesPathList = new ArrayList<>();
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                try {
                    lnrImages.removeAllViews();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                for (String anImagesPath : imagesPath) {
                    imagesPathList.add(anImagesPath);
                    Constant.array_images.add(anImagesPath);
                    System.out.println("image path" + anImagesPath);


                    ImageView imageView = new ImageView(this);
                    try {
                        imageView.setImageBitmap(decodeUri(Uri.parse("file://" + anImagesPath)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    imageView.setAdjustViewBounds(true);
                    lnrImages.addView(imageView);
                }
                System.out.println("array of selected images" + Constant.array_images);
            }
        }

    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_WEDTH = 500;
        final int REQUIRED_HEIGHT = 300;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_WEDTH || height_tmp / 2 < REQUIRED_HEIGHT) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o2);
    }

}
