package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.Adapter.Project_detail_Adapter;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectDetailActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private TextView project_title;
    private TextView project_story;
    private TextView sem_name;
    private String projectdetailurl;
    private String posturl;
    private String project_id, sem_id;
    private String updateurl;
    private String likeurl;
    private String string_like;
    private String like;
    private ImageView featured_image;
    private ImageView profile_picture;
    private ImageView img_volunter;
    private ImageView img_advise;
    private ImageView user_dp;
    private LinearLayout layout;
    private LinearLayout layout2;
    private ArrayList arr_image;
    private ArrayList arr_video;
    private ArrayList arr_thumb;
    private Dialog dialog;
    private Dialog dialogComment;
    private int i;
    private ImageView imageView;
    private RecyclerView rv_update;
    private Project_detail_Adapter projectdetailAdapter;
    private EditText edt_user_post;
    private Button btn_post;
    private List<HashMap<String, String>> category_list;
    private ToggleButton toggle_like;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        rv_update = findViewById(R.id.rv_updates);
        category_list = new ArrayList<>();
        Button see_update = findViewById(R.id.see_update);
        FloatingActionButton float_donate = findViewById(R.id.donate);
        toggle_like = findViewById(R.id.toggle_like);
        b = getIntent().getExtras();


        toggle_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                likeurl = Constant.ProjectLike + Constant.USER_ID + "&project_id=" + b.getString("project_id");
                Like();

            }
        });


        ProgressBar progress_donation = findViewById(R.id.progress_donation);
        featured_image = findViewById(R.id.featured_image);
        profile_picture = findViewById(R.id.profile_picture);
        project_story = findViewById(R.id.project_story);
        project_title = findViewById(R.id.project_title);
        TextView txt_update = findViewById(R.id.txt_update);
        sem_name = findViewById(R.id.sem_name);
        progress_donation.getProgressDrawable().setColorFilter(Color.parseColor("#68b04d"), PorterDuff.Mode.SRC_ATOP);

        arr_image = new ArrayList();
        arr_video = new ArrayList();
        arr_thumb = new ArrayList();
        projectdetailurl = Constant.ProjectDetailsUrl + Constant.USER_ID + "&project_id=" + b.getString("project_id");
        pDialog = new ProgressDialog(ProjectDetailActivity.this);
        pDialog.setCancelable(false);
        HorizontalScrollView sView = findViewById(R.id.horizontal_Scroll);
        // Hide the Scollbar
        sView.setVerticalScrollBarEnabled(false);
        sView.setHorizontalScrollBarEnabled(false);

        HorizontalScrollView vView = findViewById(R.id.horizontal_Scroll_video);
        // Hide the Scollbar
        vView.setVerticalScrollBarEnabled(false);
        vView.setHorizontalScrollBarEnabled(false);
        layout2 = findViewById(R.id.linear2);
        layout = findViewById(R.id.linear);

        if (Constant.isOnline(getApplicationContext())) {
            getProjectDetail();
        } else {
            Toast.makeText(ProjectDetailActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
        }


        float_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ProjectDetailActivity.this, CMIDonate.class);
                i.putExtra("project_id", b.getString("project_id"));
                startActivity(i);


            }
        });


        see_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (category_list.size() >= 1) {
                    category_list.clear();
                }


                updateurl = Constant.SeeUpdatesDetailsUrl + project_id + "&page=1";
                getUpdate();

            }
        });


    }

    private void createDialog() {
        dialog = new Dialog(ProjectDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_choose_action);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }


    private void initDialog() {
        img_volunter = dialog.findViewById(R.id.img_volunteer);
        img_advise = dialog.findViewById(R.id.img_advise);

    }


    private void createDialogComment() {
        dialogComment = new Dialog(ProjectDetailActivity.this);
        dialogComment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Include dialog.xml file
        dialogComment.setContentView(R.layout.layout_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialogComment.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialogComment.show();
    }


    private void initDialogComment() {
        edt_user_post = dialogComment.findViewById(R.id.edt_user_post);
        btn_post = dialogComment.findViewById(R.id.btn_post);
        user_dp = dialogComment.findViewById(R.id.user_dp);
    }

    private void Like() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, likeurl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);


                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {
                        JSONArray data = jObj.getJSONArray("data");

                        string_like = data.getString(0);
                        if (string_like.equals("Like")) {
                            toggle_like.setChecked(true);
                        } else {
                            toggle_like.setChecked(false);
                        }
                    } else {

                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(ProjectDetailActivity.this,
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                return new HashMap<>();
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    private void getProjectDetail() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Getting Projects ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, projectdetailurl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {
                        JSONObject data = jObj.getJSONObject("data");

                        project_id = data.getString("id");

                        sem_id = data.getString("sem_id");

                        String project_name = data.getString("project_name");
                        String long_description = data.getString("long_description");
                        String featured_image_url = data.getString("featured_image");
                        String estimated_donation = data.getString("estimated_donation");
                        String profile_picture_url = data.getString("profile_picture");
                        Constant.SEM_DP = profile_picture_url;
                        String string_sem_name = data.getString("sem_name");
                        Constant.SEM_NAME = string_sem_name;
                        String location = data.getString("location");
                        String posted_date = data.getString("posted_date");
                        String country_name = data.getString("country_name");
                        like = data.getString("like");


                        JSONArray images = data.getJSONArray("other_images");
                        JSONArray videos = data.getJSONArray("other_videos");

                        for (int i = 0; i < images.length(); i++) {
                            String c = images.getString(i);
                            arr_image.add(c);
                        }
                        for (int i = 0; i < videos.length(); i++) {

                            JSONObject d = videos.getJSONObject(i);

                            String video = d.getString("video");
                            String thumb = d.getString("thumb");

                            arr_video.add(video);
                            arr_thumb.add(thumb);
                        }
                        //IMAGES IN SLIDER
                        for (i = 0; i < arr_image.size(); i++) {
                            imageView = new ImageView(ProjectDetailActivity.this);
                            int width = 200;
                            int height = 150;
                            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
                            imageView.setLayoutParams(parms);
                            imageView.requestLayout();
                            imageView.setId(i);
                            imageView.setPadding(4, 4, 4, 4);
                            Picasso.get().load(arr_image.get(i).toString())
                                    .placeholder(R.drawable.bg_timeline).into(imageView);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            layout.addView(imageView);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int s = view.getId();
                                    Intent j = new Intent(ProjectDetailActivity.this, MediaViewActivty.class);
                                    j.putExtra("type", "1");
                                    j.putExtra("image", arr_image.get(s).toString());
                                    startActivity(j);
                                }
                            });
                        }
                        //VIDEOS IN SLIDER
                        for (int i = 0; i < arr_video.size(); i++) {
                            ImageView imageView = new ImageView(ProjectDetailActivity.this);
                            int width = 200;
                            int height = 150;
                            RelativeLayout relt = new RelativeLayout(ProjectDetailActivity.this);
                            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                            RelativeLayout.LayoutParams parms_play = new RelativeLayout.LayoutParams(100, 70);
                            imageView.requestLayout();
                            imageView.setId(i);
                            imageView.setPadding(4, 4, 4, 4);
                            ImageView img_play = new ImageView(ProjectDetailActivity.this);
                            img_play.setImageDrawable(getResources().getDrawable(R.mipmap.play));

                            parms_play.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            parms_play.addRule(RelativeLayout.CENTER_VERTICAL);
                            imageView.setLayoutParams(parms);
                            img_play.setLayoutParams(parms_play);
                            Picasso.get().load(arr_thumb.get(i).toString())
                                    .placeholder(R.drawable.bg_timeline).into(imageView);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            relt.addView(imageView);
                            relt.addView(img_play);
                            layout2.addView(relt);

                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int s = view.getId();
                                    Intent j = new Intent(ProjectDetailActivity.this, MediaViewActivty.class);
                                    j.putExtra("type", "2");
                                    j.putExtra("video", arr_video.get(s).toString());
                                    startActivity(j);

                                }
                            });
                        }

                        if (like.equals("0")) {
                            toggle_like.setChecked(false);
                        } else if (like.equals("1")) {
                            toggle_like.setChecked(true);
                        }

                        sem_name.setText(string_sem_name);
                        project_title.setText(project_name);
                        if (long_description.equals("null")) {
                            project_story.setText("No Description Added for this project.\n For more Details Contact to CMI WORLD ");

                        } else
                            project_story.setText(long_description);
                        Picasso.get().load(Constant.Image_url + featured_image_url)
                                .placeholder(R.drawable.bg_timeline).into(featured_image);
                        Picasso.get().load(profile_picture_url)
                                .placeholder(R.drawable.bg_timeline).into(profile_picture);


                    } else {

                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(ProjectDetailActivity.this,
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                return new HashMap<>();
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void PostComment() {
        // Tag used to cancel the request
        final String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait");
        pDialog.setTitle("Please Wait");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, posturl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        edt_user_post.setText("");
                        Toast.makeText(ProjectDetailActivity.this, "Progress Updated on project", Toast.LENGTH_SHORT).show();
                        dialogComment.cancel();

                    } else {

                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(ProjectDetailActivity.this,
                                errorMsg4, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(ProjectDetailActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ProjectDetailActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("comments", edt_user_post.getText().toString().trim());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getUpdate() {

        // Tag used to cancel the request
        final String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait");
        pDialog.setTitle("Please Wait");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, updateurl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        JSONArray data = jObj.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {

                            JSONObject c = data.getJSONObject(i);


                            String id = c.getString("id");
                            String project_id = c.getString("project_id");
                            String comment = c.getString("comments");
                            String date = c.getString("added_datetime");


                            HashMap<String, String> map_project = new HashMap<>();
                            map_project.put("id", id);
                            map_project.put("project_id", project_id);
                            map_project.put("sem_comment", comment);
                            map_project.put("name", Constant.SEM_NAME);
                            map_project.put("time", date);
                            map_project.put("profile_picture", Constant.SEM_DP);
                            category_list.add(map_project);
                        }

                        projectdetailAdapter = new Project_detail_Adapter(ProjectDetailActivity.this, category_list);
                        LinearLayoutManager llm = new LinearLayoutManager(ProjectDetailActivity.this);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        rv_update.setLayoutManager(llm);
                        rv_update.setAdapter(projectdetailAdapter);

                    } else {

                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(ProjectDetailActivity.this,
                                errorMsg4, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(ProjectDetailActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ProjectDetailActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                return new HashMap<>();
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chat:
                CommentAction();
                return true;
            case R.id.action_testimonials:
                Intent intent = new Intent(getApplicationContext(), TestimonialsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("project_id", project_id);
                bundle.putString("sem_id", sem_id);
                intent.putExtras(bundle);
                startActivity(intent);
            case R.id.action_message:
                MessageActions();
                return true;
            case R.id.action_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "For More Details Download https://play.google.com/store/apps/details?id=com.lucsoninfotech.cmi.cmiworld ");
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void MessageActions() {
        createDialog();
        initDialog();

        img_advise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProjectDetailActivity.this, ChatActivity.class);
                i.putExtra("flag", "2");
                startActivity(i);


            }
        });

        img_volunter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProjectDetailActivity.this, ChatActivity.class);
                i.putExtra("flag", "1");
                startActivity(i);

            }
        });
    }

    private void CommentAction() {

        createDialogComment();
        initDialogComment();

        Picasso.get().load(Constant.SEM_DP).into(user_dp);


        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posturl = Constant.SeeProcessDetailsUrl + project_id;
                if (Constant.isOnline(getApplicationContext())) {
                    PostComment();

                } else {
                    Toast.makeText(ProjectDetailActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
