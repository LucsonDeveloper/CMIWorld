package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.AndroidMultiPartEntity;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class ApplyforSEMActivity2 extends AppCompatActivity {

    static final Integer READ_EXST = 1;
    LinearLayout linear_images, linear_feature;
    EditText edt_project_name, edt_description, edt_tag, edt_estimated_donation;
    Button btn_done;
    TextView txt_select_feature_image, txt_select_image, txt_select_video, txt_per;
    Bitmap bitmap;
    String filePath, addprojecturl, resp, category_id;
    Spinner spn_category;
    Bundle b;
    CircleProgress circle_progress;
    long totalSize = 0;
    private int PICK_IMAGE_REQUEST = 1;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyfor_sem2);

        linear_images = findViewById(R.id.linear_images);
        linear_feature = findViewById(R.id.linear_feature);
        edt_project_name = findViewById(R.id.edt_project_name);
        edt_description = findViewById(R.id.edt_description);
        edt_estimated_donation = findViewById(R.id.edt_estimated_donation);
        edt_tag = findViewById(R.id.edt_tag);
        txt_select_feature_image = findViewById(R.id.edt_select_feature_image);
        txt_select_image = findViewById(R.id.edt_select_image);
        txt_select_video = findViewById(R.id.edt_select_video);
        circle_progress = findViewById(R.id.circle_progress);
        //progressBar=(ProgressBar)findViewById(R.id.progressBar);
        spn_category = findViewById(R.id.spinner_category);

        btn_done = findViewById(R.id.btn_done);
        pDialog = new ProgressDialog(ApplyforSEMActivity2.this);
        pDialog.setCancelable(false);


        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST);


        b = getIntent().getExtras();


        ArrayAdapter adar = new ArrayAdapter(ApplyforSEMActivity2.this, R.layout.spiner_item, Constant.array_category);
        spn_category.setAdapter(adar);


        spn_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                category_id = Constant.category_list.get(i).get("id");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                category_id = Constant.category_list.get(0).get("id");
            }
        });


        txt_select_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ApplyforSEMActivity2.this, VideoPickerActivity.class);
                startActivity(i);
            }
        });

        linear_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ApplyforSEMActivity2.this, ImageActivity.class);
                startActivity(i);
            }
        });


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addprojecturl = Constant.AddProject + Constant.USER_ID;
                Log.d("Done URL", addprojecturl);
                new UploadFileToServer().execute();
            }
        });

        linear_feature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ApplyforSEMActivity2.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ApplyforSEMActivity2.this, permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ApplyforSEMActivity2.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(ApplyforSEMActivity2.this, new String[]{permission}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), ApplyforSEMActivity.class);
            startActivity(intent);
        }
    }

    private void captureImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && null != data) {
            // Get the Image from data

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            int columnIndex = 0;
            if (cursor != null) {
                cursor.moveToFirst();
                columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);

                System.out.println(filePath);

                File f = new File("" + filePath);
                txt_select_feature_image.setText(f.getName());
                cursor.close();
            }

        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            showDialog();

            circle_progress.setProgress(0);

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            circle_progress.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // updating progress bar value
            circle_progress.setProgress(progress[0]);

        }


        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(addprojecturl);


            System.out.println("url in add project :::: " + addprojecturl);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {


                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                                System.out.println("progress" + (int) ((num / (float) totalSize) * 100));
                            }
                        });


                // Adding file data to http body


                System.out.println("userrrrrrr " + Constant.USER_ID);


                entity.addPart("project_name", new StringBody(edt_project_name.getText().toString()));
                entity.addPart("description", new StringBody(edt_description.getText().toString()));
                entity.addPart("category_id", new StringBody(category_id));
                entity.addPart("tag_ids", new StringBody(edt_tag.getText().toString().trim()));
                entity.addPart("estimated_donation", new StringBody(edt_estimated_donation.getText().toString().trim()));
                entity.addPart("ngo_existing", new StringBody("0"));

                entity.addPart("ngo_name", new StringBody(b.getString("ngo_name")));
                entity.addPart("ngo_reg_id", new StringBody(b.getString("reg_id")));
                entity.addPart("ngo_starting_date", new StringBody(b.getString("starting_date")));
                entity.addPart("ngo_working_area", new StringBody(b.getString("ngo_working_area")));
                entity.addPart("ngo_country_id", new StringBody(b.getString("country_id")));
                entity.addPart("ngo_location", new StringBody(b.getString("location")));

                entity.addPart("other_images_count", new StringBody(String.valueOf(Constant.array_images.size())));


                entity.addPart("other_videos_count", new StringBody(String.valueOf(Constant.selected_videos.size())));
                System.out.println("count ::" + String.valueOf(Constant.selected_videos.size()));


                //   if (Constant.array_images != null) {
                for (int i = 0; i < Constant.array_images.size(); i++) {
                    File sourceFile = new File(Constant.array_images.get(i).toString());
                    entity.addPart("image_" + (i + 1), new FileBody(sourceFile));
                }
                //  }

                /*    if (Constant.selected_videos != null) {*/

                for (int i = 0; i < Constant.selected_videos.size(); i++) {

                    System.out.println("fetch videos ::::" + Constant.selected_videos.get(i).toString());
                    File sourceFile = new File(Constant.selected_videos.get(i).toString());
                    entity.addPart("video_" + (i + 1), new FileBody(sourceFile));
                }

                // }

                if (filePath != null) {
                    File sourceFile = new File(filePath);
                    entity.addPart("featured_image", new FileBody(sourceFile));
                }

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                resp = response.toString();
                System.out.println("response from server:::: " + resp);
                if (statusCode == 200) {
                    // Server response
                    runOnUiThread(new Runnable() {
                        public void run() {

                            circle_progress.setVisibility(View.GONE);
                        }
                    });

                    responseString = EntityUtils.toString(r_entity);
                } else {

                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            hideDialog();
                            circle_progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(ApplyforSEMActivity2.this, "Please try again", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            circle_progress.setVisibility(View.GONE);
            System.out.println("post response : " + result);

            try {
                JSONObject jObj = new JSONObject(result);
                int error = jObj.getInt("error_code");
                String error_string = jObj.getString("error_string");

                // Check for error node in json
                if (error == 0) {
                    hideDialog();
                    Toast.makeText(ApplyforSEMActivity2.this, "Project Upload Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ApplyforSEMActivity2.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finishAffinity();

                } else {

                    Toast.makeText(ApplyforSEMActivity2.this, error_string, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
