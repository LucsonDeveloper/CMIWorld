package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.AndroidMultiPartEntity;
import com.lucsoninfotech.cmi.cmiworld.helper.SQLiteHandler;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private static final Integer READ_EXST = 1;
    private final int PICK_IMAGE_REQUEST = 1;
    private ProgressDialog pDialog;
    private TextView txt_email;
    private EditText edt_name;
    private EditText edt_mobile;
    private ImageView user_dp;
    private String url;
    private FrameLayout progressBarHolder;
    private ProgressBar progressbar;
    private Uri fileUri;
    private String filePath;
    private File sourceFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        askForPermission();

        progressBarHolder = findViewById(R.id.progressBarHolder);
        progressbar = findViewById(R.id.progressbar);
        TextView txt_changepassword = findViewById(R.id.txt_changepassword);
        txt_email = findViewById(R.id.edt_mail);
        edt_name = findViewById(R.id.edt_name);
        edt_mobile = findViewById(R.id.edt_mobile);
        user_dp = findViewById(R.id.user_dp);
        Button btn_save = findViewById(R.id.btn_save);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        SQLiteHandler db = new SQLiteHandler(this);
        HashMap<String, String> user_detail = db.getUserDetails();
        Constant.USER_ID = user_detail.get("id");


        url = Constant.ViewProfileUrl + Constant.USER_ID;


        getProfileData();

        user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isOnline(getApplicationContext())) {
                    captureImage();
                } else {
                    Toast.makeText(ProfileActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        txt_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(i);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Constant.isOnline(getApplicationContext())) {
                    new UploadFileToServer().execute();
                } else {
                    Toast.makeText(ProfileActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void askForPermission() {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ProfileActivity.READ_EXST);
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ProfileActivity.READ_EXST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            recreate();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    private void captureImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void getProfileData() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {

                    Log.d("responce", response);
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        JSONObject data = jObj.getJSONObject("data");

                        String name = data.getString("name");
                        String email = data.getString("email");
                        String mobile = data.getString("mobile");
                        String profile_picture = data.getString("profile_picture");


                        txt_email.setText(email);
                        edt_name.setText(name);
                        edt_mobile.setText(mobile);


                        Picasso.get().load(profile_picture)
                                .placeholder(R.drawable.bg_timeline).into(user_dp, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(ProfileActivity.this, "Image couldn't loaded", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(getApplicationContext(),
                                errorMsg4, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

    private void showDialog() {

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
        progressbar.getIndeterminateDrawable().setColorFilter(0xFF, android.graphics.PorterDuff.Mode.MULTIPLY);

    }

    private void hideDialog() {
        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                user_dp.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            int columnIndex;
            if (cursor != null) {
                cursor.moveToFirst();
                columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                System.out.println(filePath);
                cursor.close();
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            showDialog();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constant.UpdateProfilePic);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                System.out.println("number" + num);
                            }
                        });

                if (filePath != null) {
                    sourceFile = new File(filePath);
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }


                System.out.println("source part" + sourceFile);
                // Adding file data to http body
                entity.addPart("user_id", new StringBody(Constant.USER_ID));

                if (filePath != null) {
                    entity.addPart("profile_picture", new FileBody(sourceFile));
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });
                }

                entity.addPart("name", new StringBody(edt_name.getText().toString().trim()));
                entity.addPart("email", new StringBody(txt_email.getText().toString()));
                entity.addPart("mobile", new StringBody(edt_mobile.getText().toString().trim()));
                entity.addPart("rm_image", new StringBody("0"));


                int totalSize = (int) entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                String resp = response.toString();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        private void showDialog() {
            if (!pDialog.isShowing())
                pDialog.show();
        }

        private void hideDialog() {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            hideDialog();
            Log.d("responce", result);
            try {
                JSONObject jObj = new JSONObject(result);
                int error = jObj.getInt("error_code");
                Log.d("responce", result);

                // Check for error node in json
                if (error == 0) {
//                    JSONObject data = jObj.getJSONObject("data");
                    Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ProfileActivity.this, SettingActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    String errorMsg4 = jObj.getString("error_string");
                    Toast.makeText(getApplicationContext(),
                            errorMsg4, Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}