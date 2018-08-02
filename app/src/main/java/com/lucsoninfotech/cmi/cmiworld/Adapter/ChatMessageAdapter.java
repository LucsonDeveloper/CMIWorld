package com.lucsoninfotech.cmi.cmiworld.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.ChatMessageModel;
import com.lucsoninfotech.cmi.cmiworld.other.CustomGifView;
import com.lucsoninfotech.cmi.cmiworld.other.CustomGifViewAudio;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lucsonmacpc8 on 03/10/16.
 */
public class ChatMessageAdapter extends BaseAdapter {

    private final List<ChatMessageModel> chatMessages;
    private final Activity context;
    String time;
    String imageHistory;
    String videoHistory;
    String audioHistory;
    private ChatMessageModel chatMessage;
    private String urlUpload;
    private ImageLoader imageLoader;
    private DisplayImageOptions displayOptions;

    //String thumbnail_path;

    public ChatMessageAdapter(Activity context, List<ChatMessageModel> chatMessages, String[] imageUrls) {
        this.context = context;
        this.chatMessages = chatMessages;
        String[] imageUrls1 = imageUrls;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public int getViewTypeCount() {
//        System.out.println("Count:::::::::::::::::::::::"+getCount());
//        return getCount();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//
//        return position;
//    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //System.out.println("get view::::::"+position+"  "+convertView);
        View v = convertView;
        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
        displayOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.bg_timeline)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(500)).build();
        chatMessage = (ChatMessageModel) getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
//
//        String currentDate = DateFormat.getDateTimeInstance().format(new Date());
//        System.out.println("Current date..."+currentDate);
//
//        String utcTime = dateFormat.format(new Date());
//        System.out.println("Current UTC date..."+utcTime);
//
//        try {
//            Date current = dateFormat.parse(currentDate);
//            Date UTC = dateFormat.parse(utcTime);
//            System.out.println("Current date:::"+current);
//            System.out.println("Current UTC date:::"+UTC);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


        ViewHolder holder;
        if (v == null) {
            if (vi != null) {
                v = vi.inflate(R.layout.list_item_chat_message, null);
            }
            holder = createViewHolder(v);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        boolean myMsg = chatMessage.isMe();
        setAlignment(holder, myMsg, position);

        return v;
    }

    private void setMessageType(ViewHolder holder, int messageType) {

        // for text message -- messageType = 0
        switch (messageType) {
            case 0:
                holder.layoutMessage.setVisibility(View.VISIBLE);
                holder.txtInfo.setVisibility(View.VISIBLE);

                holder.layoutImage.setVisibility(View.GONE);
                holder.layoutAudio.setVisibility(View.GONE);
                holder.layoutVideo.setVisibility(View.GONE);
                break;
            // for image message -- messageType = 1
            case 1:
                holder.layoutImage.setVisibility(View.VISIBLE);

                holder.layoutMessage.setVisibility(View.GONE);
                holder.layoutAudio.setVisibility(View.GONE);
                holder.layoutVideo.setVisibility(View.GONE);
                holder.txtInfo.setVisibility(View.GONE);
                break;
            // for audio message -- messageType = 2
            case 2:
                holder.layoutAudio.setVisibility(View.VISIBLE);

                holder.layoutMessage.setVisibility(View.GONE);
                holder.layoutImage.setVisibility(View.GONE);
                holder.layoutVideo.setVisibility(View.GONE);
                holder.txtInfo.setVisibility(View.GONE);
                break;
            // for video message -- messageType = 3
            case 3:
                holder.layoutVideo.setVisibility(View.VISIBLE);

                holder.layoutMessage.setVisibility(View.GONE);
                holder.layoutImage.setVisibility(View.GONE);
                holder.layoutAudio.setVisibility(View.GONE);
                holder.txtInfo.setVisibility(View.GONE);
                break;
        }
    }

    private int getN() {
        return (int) (Math.random() * 255);
    }

    private void setAlignment(final ViewHolder holder, boolean isMe, int position) {

        int messageType = chatMessage.getMessageType();
        setMessageType(holder, messageType);

        if (!isMe) {
            holder.txtUser.setVisibility(View.VISIBLE);
            switch (messageType) {
                case 0:
                    holder.txtUser.setVisibility(View.VISIBLE);
                    holder.txtMessage.setText(chatMessage.getMessage());
                    holder.txtInfo.setText(chatMessage.getDateTime());
                    holder.txtUser.setText(chatMessage.getUserName());

                    break;
                case 1:
                    holder.txtUser.setVisibility(View.VISIBLE);
                    if (chatMessage.isHistory()) {
                        holder.ivImageUpload.setVisibility(View.GONE);
                        holder.ivImageDownload.setVisibility(View.GONE);
                        //holder.tvImageSize.setVisibility(View.VISIBLE);
                        holder.tvImageTime.setText(chatMessage.getDateTime());
                        //holder.ivImageChat.setImageBitmap(BitmapFactory.decodeFile(chatMessage.getMessage_thumb()));
                        System.out.println("chatMessage.getMessage_image()::" + chatMessage.getMessage_image());
                    /*Picasso.with(context).load(chatMessage.getMessage_image())
                            .placeholder(R.drawable.bg_timeline).into(holder.ivImageChat, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });*/
                        try {
                            imageLoader.displayImage(chatMessage.getMessage_image(), holder.ivImageChat, displayOptions,
                                    new SimpleImageLoadingListener() {

                                        @Override
                                        public void onLoadingStarted(String imageUri,
                                                                     View view) {
                                        }

                                        @Override
                                        public void onLoadingFailed(String imageUri,
                                                                    View view, FailReason failReason) {
                                        }

                                        @Override
                                        public void onLoadingComplete(String imageUri,
                                                                      View view, Bitmap loadedImage) {

                                        }
                                    }, new ImageLoadingProgressListener() {
                                        @Override
                                        public void onProgressUpdate(String imageUri,
                                                                     View view, int current, int total) {
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        holder.ivImageChat.setTag(position);
                        holder.ivImageChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int position = (Integer) v.getTag();
                           /* Intent intent = new Intent(context, PhotoViewerActivity.class);
                            intent.putExtra("image", chatMessages.get(position).getMessage_image());
                            context.startActivity(intent);*/
                            }
                        });
                    } else if (chatMessage.isReceive()) {
                        holder.tvImageTime.setText(chatMessage.getDateTime());
                    /*Picasso.with(context).load(chatMessage.getMessage_thumb())
                            .placeholder(R.drawable.bg_timeline).into(holder.ivImageChat, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });*/
                        try {
                            imageLoader.displayImage(chatMessage.getMessage_thumb(), holder.ivImageChat, displayOptions,
                                    new SimpleImageLoadingListener() {

                                        @Override
                                        public void onLoadingStarted(String imageUri,
                                                                     View view) {
                                        }

                                        @Override
                                        public void onLoadingFailed(String imageUri,
                                                                    View view, FailReason failReason) {
                                        }

                                        @Override
                                        public void onLoadingComplete(String imageUri,
                                                                      View view, Bitmap loadedImage) {

                                        }
                                    }, new ImageLoadingProgressListener() {
                                        @Override
                                        public void onProgressUpdate(String imageUri,
                                                                     View view, int current, int total) {
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        holder.ivImageDownload.setVisibility(View.VISIBLE);
                        holder.ivImageDownload.setTag(position);
                        holder.ivImageDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivImageDownload.setVisibility(View.GONE);
                                holder.gvCancel.setVisibility(View.VISIBLE);
                                final int position = (Integer) v.getTag();
                                Picasso.get().load(chatMessage.getMessage_image()).placeholder(holder.ivImageChat.getDrawable())
                                        .into(holder.ivImageChat);
                                Picasso.get().load(chatMessage.getMessage_image())
                                        .into(new Target() {
                                                  @Override
                                                  public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                      try {
                                                          String root = Environment.getExternalStorageDirectory().getPath();
                                                          File myDir = new File(root + "/Quranology/QuranologyImages");
                                                          if (!myDir.exists()) {
                                                              myDir.mkdirs();
                                                          }
                                                          String name = "image" + System.currentTimeMillis() + ".jpg";
                                                          myDir = new File(myDir, name);
                                                          FileOutputStream out = new FileOutputStream(myDir);
                                                          bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                          out.flush();
                                                          out.close();
                                                      } catch (Exception e) {
                                                          // some action
                                                      }
                                                  }

                                                  @Override
                                                  public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                  }


                                                  @Override
                                                  public void onPrepareLoad(Drawable placeHolderDrawable) {
                                                  }
                                              }
                                        );
                            }
                        });
                        holder.ivImageChat.setTag(position);
                        holder.ivImageChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int position = (Integer) v.getTag();
                          /*  Intent intent = new Intent(context, PhotoViewerActivity.class);
                            intent.putExtra("image", chatMessages.get(position).getMessage_image());
                            context.startActivity(intent);*/
                            }
                        });
                    } else if (chatMessage.isSend()) {
                        holder.tvImageTime.setText(chatMessage.getDateTime());
                        holder.ivImageChat.setImageBitmap(BitmapFactory.decodeFile(chatMessage.getMessage_image()));
                        holder.ivImageUpload.setVisibility(View.VISIBLE);
                        holder.ivImageUpload.setTag(position);
                        holder.ivImageUpload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivImageUpload.setVisibility(View.GONE);
                                // urlUpload = "http://lucsoninfotech.in/quranology/WS/private-chat.php?sender_id=" + Constant.USER_ID + "&receiver_id=" + Constant.RECEIVER_ID.get(Constant.POS_RECEIVER) + "&message_type=2&message=&timezone=" + Constant.TIMEZONE;
                                System.out.println("urlUpload::" + urlUpload);
                                int position = (Integer) v.getTag();
                                // new UploadImageFile(holder, position).execute();
                            }
                        });
                        holder.ivImageChat.setTag(position);
                        holder.ivImageChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int position = (Integer) v.getTag();
                           /* Intent intent = new Intent(context, PhotoViewerActivity.class);
                            intent.putExtra("image", chatMessages.get(position).getMessage_image());
                            context.startActivity(intent);*/
                            }
                        });
                    }
                    break;
                case 3:
                    holder.txtUser.setVisibility(View.VISIBLE);
                    if (chatMessage.isHistory()) {
                        final String[] thumbnail_path = new String[1];
                        holder.tvVideoTime.setText(chatMessage.getDateTime());
                        holder.ivVideoUpload.setVisibility(View.GONE);
                        holder.ivVideoDownload.setVisibility(View.GONE);

                        Picasso.get().load(chatMessages.get(position).getMessage_video_thumb())
                                .into(new Target() {
                                          @Override
                                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                              try {
                                                  String root = Environment.getExternalStorageDirectory().getPath();
                                                  File myDir = new File(root + "/Quranology/QuranologyVideos/.Thumbnails");
                                                  if (!myDir.exists()) {
                                                      myDir.mkdirs();
                                                  }
                                                  String name = "thumbnail" + System.currentTimeMillis() + ".jpg";
                                                  myDir = new File(myDir, name);
                                                  thumbnail_path[0] = myDir + "";
                                                  FileOutputStream out = new FileOutputStream(myDir);
                                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                  out.flush();
                                                  out.close();
                                              } catch (Exception e) {
                                                  // some action
                                              }
                                          }

                                          @Override
                                          public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                          }


                                          @Override
                                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                                          }
                                      }
                                );
                        Bitmap bitmapImage = BitmapFactory.decodeFile(thumbnail_path[0]);
                        Drawable drawableImage = new BitmapDrawable(bitmapImage);
                        holder.ivVideoChat.setBackgroundDrawable(drawableImage);

                        holder.ivVideoChat.setTag(position);
                        holder.ivVideoChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int position = (Integer) v.getTag();
                            /*Intent intent = new Intent(context, VideoViewerActivity.class);
                            intent.putExtra("video", chatMessages.get(position).getMessage_video());
                            context.startActivity(intent);*/
                            }
                        });

                    } else if (chatMessage.isReceive()) {
                        final String[] thumbnail_path = new String[1];
                        holder.tvVideoTime.setText(chatMessage.getDateTime());
                        Picasso.get().load(chatMessages.get(position).getMessage_video_thumb())
                                .into(new Target() {
                                          @Override
                                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                              try {
                                                  String root = Environment.getExternalStorageDirectory().getPath();
                                                  File myDir = new File(root + "/Quranology/QuranologyVideos/.Thumbnails");
                                                  if (!myDir.exists()) {
                                                      myDir.mkdirs();
                                                  }
                                                  String name = "thumbnail" + System.currentTimeMillis() + ".jpg";
                                                  myDir = new File(myDir, name);
                                                  thumbnail_path[0] = myDir + "";
                                                  FileOutputStream out = new FileOutputStream(myDir);
                                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                  out.flush();
                                                  out.close();
                                              } catch (Exception e) {
                                                  // some action
                                              }
                                          }

                                          @Override
                                          public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                          }

                                          @Override
                                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                                          }
                                      }
                                );
                        Bitmap bitmapImage = BitmapFactory.decodeFile(thumbnail_path[0]);
                        Drawable drawableImage = new BitmapDrawable(bitmapImage);
                        holder.ivVideoChat.setBackgroundDrawable(drawableImage);
                        holder.ivVideoDownload.setVisibility(View.VISIBLE);
                        holder.ivVideoDownload.setTag(position);
                        holder.ivVideoDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivVideoDownload.setVisibility(View.GONE);
                                holder.gvVideoCancel.setVisibility(View.VISIBLE);
                                final int position = (Integer) v.getTag();
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    downloadFile(chatMessage.getMessage_video(), position);
//                                }
//                            });
                                // new DownloadVideo(holder, chatMessage.getMessage_video(), position).execute();


                            }
                        });

                    } else if (chatMessage.isSend()) {
                        holder.tvVideoTime.setText(chatMessage.getDateTime());
                        Bitmap bitmapImage = BitmapFactory.decodeFile(chatMessage.getMessage_image());
                        Drawable drawableImage = new BitmapDrawable(bitmapImage);
                        holder.ivVideoChat.setBackgroundDrawable(drawableImage);
                        holder.ivVideoUpload.setVisibility(View.VISIBLE);
                        holder.ivVideoUpload.setTag(position);
                        holder.ivVideoUpload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivVideoUpload.setVisibility(View.GONE);
                                //  urlUpload = "http://lucsoninfotech.in/quranology/WS/private-chat.php?sender_id=" + Constant.USER_ID + "&receiver_id=" + Constant.RECEIVER_ID.get(Constant.POS_RECEIVER) + "&message_type=3&message=&timezone=" + Constant.TIMEZONE;
                                //urlUpload = "http://lucsoninfotech.in/quranology/WS/video-upload.php";
                                System.out.println("urlUpload::" + urlUpload);
                                int position = (Integer) v.getTag();
                                //  new UploadVideoFile(holder, position).execute();
                            }
                        });

                    }

                    break;
                case 2:
                    holder.txtUser.setVisibility(View.VISIBLE);
                    if (chatMessage.isHistory()) {
                        holder.tvAudioTime.setText(chatMessage.getDateTime());
                        holder.ivAudioPlay.setVisibility(View.VISIBLE);
                        holder.ivAudioPlay.setTag(position);
                        final MediaPlayer[] mediaplayer = new MediaPlayer[1];
                        holder.ivAudioPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivAudioPlay.setVisibility(View.GONE);
                                holder.ivAudioPause.setVisibility(View.VISIBLE);
                                mediaplayer[0] = new MediaPlayer();
                                mediaplayer[0].setAudioStreamType(AudioManager.STREAM_MUSIC);
                                int position = (Integer) v.getTag();
                                try {

                                    mediaplayer[0].setDataSource(chatMessages.get(position).getMessage_audio());
                                    mediaplayer[0].prepareAsync();

                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaplayer[0].setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mediaplayer[0].start();
                                    }
                                });
                                mediaplayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        holder.ivAudioPause.setVisibility(View.GONE);
                                        holder.ivAudioPlay.setVisibility(View.VISIBLE);
                                        mp.release();
                                    }
                                });
                            }
                        });
                        holder.ivAudioPause.setTag(position);
                        holder.ivAudioPause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivAudioPlay.setVisibility(View.VISIBLE);
                                holder.ivAudioPause.setVisibility(View.GONE);
                                int position = (Integer) v.getTag();
                                mediaplayer[0].pause();
                            }
                        });

                    } else if (chatMessage.isReceive()) {
                        holder.tvAudioTime.setText(chatMessage.getDateTime());
                        holder.ivAudioPlay.setVisibility(View.GONE);
                        holder.ivAudioPause.setVisibility(View.GONE);
                        holder.ivAudioDownload.setVisibility(View.VISIBLE);
                        holder.ivAudioDownload.setTag(position);
                        holder.ivAudioDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivAudioDownload.setVisibility(View.GONE);
                                holder.gvAudioCancel.setVisibility(View.VISIBLE);
                                final int position = (Integer) v.getTag();
                                new DownloadAudio(holder, chatMessage.getMessage_audio(), position).execute();
                            }
                        });
                        final MediaPlayer[] mediaplayer = new MediaPlayer[1];
                        holder.ivAudioPlay.setTag(position);
                        holder.ivAudioPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mediaplayer[0] = new MediaPlayer();
                                mediaplayer[0].setAudioStreamType(AudioManager.STREAM_MUSIC);
                                holder.ivAudioPlay.setVisibility(View.GONE);
                                holder.ivAudioPause.setVisibility(View.VISIBLE);
                                int position = (Integer) v.getTag();
                                try {

                                    mediaplayer[0].setDataSource(chatMessages.get(position).getMessage_audio());
                                    mediaplayer[0].prepareAsync();

                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaplayer[0].setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mediaplayer[0].start();
                                    }
                                });
                                mediaplayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        holder.ivAudioPause.setVisibility(View.GONE);
                                        holder.ivAudioPlay.setVisibility(View.VISIBLE);
                                        mp.release();
                                    }
                                });
                            }
                        });
                        holder.ivAudioPause.setTag(position);
                        holder.ivAudioPause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivAudioPlay.setVisibility(View.VISIBLE);
                                holder.ivAudioPause.setVisibility(View.GONE);
                                int position = (Integer) v.getTag();
                                mediaplayer[0].pause();
                            }
                        });

                    } else if (chatMessage.isSend()) {
                        holder.tvAudioTime.setText(chatMessage.getDateTime());
                        holder.ivAudioPlay.setVisibility(View.GONE);
                        holder.ivAudioPause.setVisibility(View.GONE);
                        holder.ivAudioUpload.setVisibility(View.VISIBLE);
                        holder.ivAudioUpload.setTag(position);
                        holder.ivAudioUpload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivAudioUpload.setVisibility(View.GONE);
                                // urlUpload = "http://lucsoninfotech.in/quranology/WS/private-chat.php?sender_id=" + Constant.USER_ID + "&receiver_id=" + Constant.RECEIVER_ID.get(Constant.POS_RECEIVER) + "&message_type=4&message=&timezone=" + Constant.TIMEZONE;
                                System.out.println("urlUpload::" + urlUpload);
                                int position = (Integer) v.getTag();
                                // new UploadAudioFile(holder, position).execute();
                            }
                        });
                    }
                    break;
            }

            holder.contentWithBG.setBackgroundResource(R.drawable.chat_gray_bg);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtUser.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtUser.setLayoutParams(layoutParams);
        } else {
            //holder.avatar.setVisibility(View.GONE);
            holder.txtUser.setVisibility(View.GONE);
            switch (messageType) {
                case 0:

                    holder.txtMessage.setText(chatMessage.getMessage());
                    holder.txtInfo.setText(chatMessage.getDateTime());
                    holder.txtUser.setText(chatMessage.getUserName());

                    break;
                case 1:

                    if (chatMessage.isHistory()) {
                        holder.ivImageDownload.setVisibility(View.GONE);
                        //holder.tvImageSize.setVisibility(View.VISIBLE);
                        holder.ivImageUpload.setVisibility(View.GONE);
                        holder.tvImageTime.setText(chatMessage.getDateTime());
                        //holder.ivImageChat.setImageBitmap(BitmapFactory.decodeFile(chatMessage.getMessage_thumb()));
                    /*Picasso.with(context).load(chatMessage.getMessage_image())
                            .placeholder(R.drawable.bg_timeline).into(holder.ivImageChat, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });*/
                        try {
                            imageLoader.displayImage(chatMessage.getMessage_image(), holder.ivImageChat, displayOptions,
                                    new SimpleImageLoadingListener() {

                                        @Override
                                        public void onLoadingStarted(String imageUri,
                                                                     View view) {
                                        }

                                        @Override
                                        public void onLoadingFailed(String imageUri,
                                                                    View view, FailReason failReason) {
                                        }

                                        @Override
                                        public void onLoadingComplete(String imageUri,
                                                                      View view, Bitmap loadedImage) {

                                        }
                                    }, new ImageLoadingProgressListener() {
                                        @Override
                                        public void onProgressUpdate(String imageUri,
                                                                     View view, int current, int total) {
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        holder.ivImageChat.setTag(position);
                        holder.ivImageChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int position = (Integer) v.getTag();
                          /*  Intent intent = new Intent(context, PhotoViewerActivity.class);
                            intent.putExtra("image", chatMessages.get(position).getMessage_image());
                            context.startActivity(intent);*/
                            }
                        });
                    } else if (chatMessage.isReceive()) {
                        holder.tvImageTime.setText(chatMessage.getDateTime());
                    /*Picasso.with(context).load(chatMessage.getMessage_thumb())
                            .placeholder(R.drawable.bg_timeline).into(holder.ivImageChat, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });*/
                        try {
                            imageLoader.displayImage(chatMessage.getMessage_thumb(), holder.ivImageChat, displayOptions,
                                    new SimpleImageLoadingListener() {

                                        @Override
                                        public void onLoadingStarted(String imageUri,
                                                                     View view) {
                                        }

                                        @Override
                                        public void onLoadingFailed(String imageUri,
                                                                    View view, FailReason failReason) {
                                        }

                                        @Override
                                        public void onLoadingComplete(String imageUri,
                                                                      View view, Bitmap loadedImage) {

                                        }
                                    }, new ImageLoadingProgressListener() {
                                        @Override
                                        public void onProgressUpdate(String imageUri,
                                                                     View view, int current, int total) {
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        holder.ivImageDownload.setVisibility(View.VISIBLE);
                        holder.ivImageDownload.setTag(position);
                        holder.ivImageDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivImageDownload.setVisibility(View.GONE);
                                holder.gvCancel.setVisibility(View.VISIBLE);
                                final int position = (Integer) v.getTag();
                                Picasso.get().load(chatMessage.getMessage_image()).placeholder(holder.ivImageChat.getDrawable())
                                        .into(holder.ivImageChat, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.gvCancel.setVisibility(View.GONE);
                                                chatMessages.get(position).setHistory(true);
                                                chatMessages.get(position).setReceive(false);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                            }
                                        });
                                Picasso.get().load(chatMessage.getMessage_image())
                                        .into(new Target() {
                                                  @Override
                                                  public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                      try {
                                                          String root = Environment.getExternalStorageDirectory().getPath();
                                                          File myDir = new File(root + "/Quranology/QuranologyImages/");
                                                          if (!myDir.exists()) {
                                                              myDir.mkdirs();
                                                          }
                                                          String name = "image" + System.currentTimeMillis() + ".jpg";
                                                          myDir = new File(myDir, name);
                                                          FileOutputStream out = new FileOutputStream(myDir);
                                                          bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                          out.flush();
                                                          out.close();
                                                      } catch (Exception e) {
                                                          // some action
                                                      }
                                                  }

                                                  @Override
                                                  public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                  }

                                                  @Override
                                                  public void onPrepareLoad(Drawable placeHolderDrawable) {
                                                  }
                                              }
                                        );
                            }
                        });
                        holder.ivImageChat.setTag(position);
                        holder.ivImageChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int position = (Integer) v.getTag();
                            /*Intent intent = new Intent(context, PhotoViewerActivity.class);
                            intent.putExtra("image", chatMessages.get(position).getMessage_image());
                            context.startActivity(intent);*/
                            }
                        });
                    } else if (chatMessage.isSend()) {

                        holder.tvImageTime.setText(chatMessage.getDateTime());
                        try {
                            imageLoader.displayImage(chatMessage.getMessage_image(), holder.ivImageChat, displayOptions,
                                    new SimpleImageLoadingListener() {

                                        @Override
                                        public void onLoadingStarted(String imageUri,
                                                                     View view) {
                                        }

                                        @Override
                                        public void onLoadingFailed(String imageUri,
                                                                    View view, FailReason failReason) {
                                        }

                                        @Override
                                        public void onLoadingComplete(String imageUri,
                                                                      View view, Bitmap loadedImage) {

                                        }
                                    }, new ImageLoadingProgressListener() {
                                        @Override
                                        public void onProgressUpdate(String imageUri,
                                                                     View view, int current, int total) {
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        holder.ivImageUpload.setVisibility(View.VISIBLE);
                        holder.ivImageUpload.setTag(position);
                        holder.ivImageUpload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivImageUpload.setVisibility(View.GONE);
                                //  urlUpload = "http://lucsoninfotech.in/quranology/WS/private-chat.php?sender_id=" + Constant.USER_ID + "&receiver_id=" + Constant.RECEIVER_ID.get(Constant.POS_RECEIVER) + "&message_type=2&message=&timezone=" + Constant.TIMEZONE;
                                System.out.println("urlUpload::" + urlUpload);
                                int position = (Integer) v.getTag();
                                // new UploadImageFile(holder, position).execute();
                            }
                        });
                        holder.ivImageChat.setTag(position);
                        holder.ivImageChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int position = (Integer) v.getTag();
                            /*Intent intent = new Intent(context, PhotoViewerActivity.class);
                            intent.putExtra("image", chatMessages.get(position).getMessage_image());
                            context.startActivity(intent);*/
                            }
                        });
                    }
                    break;
                case 3:
                    if (chatMessage.isHistory()) {
                        final String[] thumbnail_path = new String[1];
                        holder.tvVideoTime.setText(chatMessage.getDateTime());
                        holder.ivVideoUpload.setVisibility(View.GONE);
                        holder.ivVideoDownload.setVisibility(View.GONE);

                        Picasso.get().load(chatMessages.get(position).getMessage_video_thumb())
                                .into(new Target() {
                                          @Override
                                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                              try {
                                                  String root = Environment.getExternalStorageDirectory().getPath();
                                                  File myDir = new File(root + "/Quranology/QuranologyVideos/.Thumbnails");
                                                  if (!myDir.exists()) {
                                                      myDir.mkdirs();
                                                  }
                                                  String name = "thumbnail" + System.currentTimeMillis() + ".jpg";
                                                  myDir = new File(myDir, name);
                                                  thumbnail_path[0] = myDir + "";
                                                  FileOutputStream out = new FileOutputStream(myDir);
                                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                  out.flush();
                                                  out.close();
                                              } catch (Exception e) {
                                                  // some action
                                              }
                                          }

                                          @Override
                                          public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                          }


                                          @Override
                                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                                          }
                                      }
                                );
                        Bitmap bitmapImage = BitmapFactory.decodeFile(thumbnail_path[0]);
                        Drawable drawableImage = new BitmapDrawable(bitmapImage);
                        holder.ivVideoChat.setBackgroundDrawable(drawableImage);
                        holder.ivVideoChat.setTag(position);
                        holder.ivVideoChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int position = (Integer) v.getTag();
                           /* Intent intent = new Intent(context, VideoViewerActivity.class);
                            intent.putExtra("video", chatMessages.get(position).getMessage_video());
                            context.startActivity(intent);*/
                            }
                        });

                    } else if (chatMessage.isReceive()) {
                        final String[] thumbnail_path = new String[1];
                        holder.tvVideoTime.setText(chatMessage.getDateTime());
                        Picasso.get().load(chatMessages.get(position).getMessage_video_thumb())
                                .into(new Target() {
                                          @Override
                                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                              try {
                                                  String root = Environment.getExternalStorageDirectory().getPath();
                                                  File myDir = new File(root + "/Quranology/QuranologyVideos/.Thumbnails");
                                                  if (!myDir.exists()) {
                                                      myDir.mkdirs();
                                                  }
                                                  String name = "thumbnail" + System.currentTimeMillis() + ".jpg";
                                                  myDir = new File(myDir, name);
                                                  thumbnail_path[0] = myDir + "";
                                                  FileOutputStream out = new FileOutputStream(myDir);
                                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                  out.flush();
                                                  out.close();
                                              } catch (Exception e) {
                                                  // some action
                                              }
                                          }

                                          @Override
                                          public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                          }


                                          @Override
                                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                                          }
                                      }
                                );
                        Bitmap bitmapImage = BitmapFactory.decodeFile(thumbnail_path[0]);
                        Drawable drawableImage = new BitmapDrawable(bitmapImage);
                        holder.ivVideoChat.setBackgroundDrawable(drawableImage);
                        holder.ivVideoDownload.setVisibility(View.VISIBLE);
                        holder.ivVideoDownload.setTag(position);
                        holder.ivVideoDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivVideoDownload.setVisibility(View.GONE);
                                holder.gvVideoCancel.setVisibility(View.VISIBLE);
                                final int position = (Integer) v.getTag();
                            /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    downloadFile(chatMessage.getMessage_video(), position);
                                }
                            });*/
                                // new DownloadVideo(holder, chatMessage.getMessage_video(), position).execute();
                            }
                        });

                    } else if (chatMessage.isSend()) {
                        holder.tvVideoTime.setText(chatMessage.getDateTime());
                        Bitmap bitmapImage = BitmapFactory.decodeFile(chatMessage.getMessage_image());
                        Drawable drawableImage = new BitmapDrawable(bitmapImage);
                        holder.ivVideoChat.setBackgroundDrawable(drawableImage);
                        holder.ivVideoUpload.setVisibility(View.VISIBLE);
                        holder.ivVideoUpload.setTag(position);
                        holder.ivVideoUpload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivVideoUpload.setVisibility(View.GONE);
                                //urlUpload = "http://lucsoninfotech.in/quranology/WS/private-chat.php?sender_id=" + Constant.USER_ID + "&receiver_id=" + Constant.RECEIVER_ID.get(Constant.POS_RECEIVER) + "&message_type=3&message=&timezone=" + Constant.TIMEZONE;
                                //urlUpload = "http://lucsoninfotech.in/quranology/WS/video-upload.php";
                                System.out.println("urlUpload::" + urlUpload);
                                int position = (Integer) v.getTag();
                                //new UploadVideoFile(holder, position).execute();
                            }
                        });
                    }
                    break;
                case 2:
                    if (chatMessage.isHistory()) {
                        holder.tvAudioTime.setText(chatMessage.getDateTime());
                        holder.ivAudioPlay.setVisibility(View.VISIBLE);
                        holder.ivAudioPlay.setTag(position);
                        final MediaPlayer[] mediaplayer = new MediaPlayer[1];
                        holder.ivAudioPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mediaplayer[0] = new MediaPlayer();
                                mediaplayer[0].setAudioStreamType(AudioManager.STREAM_MUSIC);
                                holder.ivAudioPlay.setVisibility(View.GONE);
                                holder.ivAudioPause.setVisibility(View.VISIBLE);
                                int position = (Integer) v.getTag();
                                try {

                                    mediaplayer[0].setDataSource(chatMessages.get(position).getMessage_audio());
                                    mediaplayer[0].prepareAsync();

                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaplayer[0].setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mediaplayer[0].start();
                                    }
                                });
                                mediaplayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        holder.ivAudioPause.setVisibility(View.GONE);
                                        holder.ivAudioPlay.setVisibility(View.VISIBLE);
                                        mp.release();
                                    }
                                });
                            }
                        });
                        holder.ivAudioPause.setTag(position);
                        holder.ivAudioPause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivAudioPlay.setVisibility(View.VISIBLE);
                                holder.ivAudioPause.setVisibility(View.GONE);
                                int position = (Integer) v.getTag();
                                mediaplayer[0].pause();
                            }
                        });

                    } else if (chatMessage.isReceive()) {
                        holder.tvAudioTime.setText(chatMessage.getDateTime());
                        holder.ivAudioPlay.setVisibility(View.GONE);
                        holder.ivAudioPause.setVisibility(View.GONE);
                        holder.ivAudioDownload.setVisibility(View.VISIBLE);
                        holder.ivAudioDownload.setTag(position);
                        holder.ivAudioDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivAudioDownload.setVisibility(View.GONE);
                                //holder.gvAudioCancel.setVisibility(View.VISIBLE);
                                final int position = (Integer) v.getTag();
                                new DownloadAudio(holder, chatMessage.getMessage_audio(), position).execute();
                            }
                        });
                        final MediaPlayer[] mediaplayer = new MediaPlayer[1];
                        holder.ivAudioPlay.setTag(position);
                        holder.ivAudioPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mediaplayer[0] = new MediaPlayer();
                                mediaplayer[0].setAudioStreamType(AudioManager.STREAM_MUSIC);
                                holder.ivAudioPlay.setVisibility(View.GONE);
                                holder.ivAudioPause.setVisibility(View.VISIBLE);
                                int position = (Integer) v.getTag();
                                try {

                                    mediaplayer[0].setDataSource(chatMessages.get(position).getMessage_audio());
                                    mediaplayer[0].prepareAsync();

                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaplayer[0].setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mediaplayer[0].start();
                                    }
                                });
                                mediaplayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        holder.ivAudioPause.setVisibility(View.GONE);
                                        holder.ivAudioPlay.setVisibility(View.VISIBLE);
                                        mp.release();
                                    }
                                });
                            }
                        });
                        holder.ivAudioPause.setTag(position);
                        holder.ivAudioPause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivAudioPlay.setVisibility(View.VISIBLE);
                                holder.ivAudioPause.setVisibility(View.GONE);
                                int position = (Integer) v.getTag();
                                mediaplayer[0].pause();
                            }
                        });

                    } else if (chatMessage.isSend()) {
                        holder.tvAudioTime.setText(chatMessage.getDateTime());
                        holder.ivAudioPlay.setVisibility(View.GONE);
                        holder.ivAudioPause.setVisibility(View.GONE);
                        holder.ivAudioUpload.setVisibility(View.VISIBLE);
                        holder.ivAudioUpload.setTag(position);
                        holder.ivAudioUpload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ivAudioUpload.setVisibility(View.GONE);
                                //  urlUpload = "http://lucsoninfotech.in/quranology/WS/private-chat.php?sender_id=" + Constant.USER_ID + "&receiver_id=" + Constant.RECEIVER_ID.get(Constant.POS_RECEIVER) + "&message_type=4&message=&timezone=" + Constant.TIMEZONE;
                                System.out.println("urlUpload::" + urlUpload);
                                int position = (Integer) v.getTag();
                                //  new UploadAudioFile(holder, position).execute();
                            }
                        });
                    }
                    break;
            }

            holder.contentWithBG.setBackgroundResource(R.drawable.chat_yellow_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
        }
    }

    /*  private class UploadAudioFile extends AsyncTask<Void, Void, String> {

          String sResponse;
          ViewHolder holder;
          int error_code;
          int position;

          public UploadAudioFile(ViewHolder holder, int position) {
              this.holder = holder;
              this.position = position;
          }

          @Override
          protected void onPreExecute() {
              super.onPreExecute();
              // Showing progress dialog
              holder.gvAudioCancel.setVisibility(View.VISIBLE);
          }

          @Override
          protected String doInBackground(Void... arg0) {
              return uploadFile();
          }

          @SuppressWarnings("deprecation")
          private String uploadFile() {
              String responseString;

              HttpClient httpclient = new DefaultHttpClient();
              HttpPost httppost = new HttpPost(urlUpload);

              try {
                  AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                          new AndroidMultiPartEntity.ProgressListener() {

                              @Override
                              public void transferred(long num) {

                              }
                          });

                  File sourceFile = new File(Constant.FILEPATH);

                  // Adding file data to http body
                  entity.addPart("audio", new FileBody(sourceFile));

                  System.out.println("entity::" + entity);

                  //totalSize = entity.getContentLength();
                  httppost.setEntity(entity);

                  // Making server call
                  HttpResponse response = httpclient.execute(httppost);
                  HttpEntity r_entity = response.getEntity();

                  int statusCode = response.getStatusLine().getStatusCode();
                  if (statusCode == 200) {
                      // Server response
                      responseString = EntityUtils.toString(r_entity);
                      System.out.println("responseString" + responseString);
                      try {
                          JSONObject jsonObj = new JSONObject(responseString);
                          error_code = jsonObj.getInt("error_code");
                          if (error_code == 0) {
                              JSONArray data = jsonObj.getJSONArray("data");
                              System.out.println("jsonArray:::: in upload" + data);

                              // looping through All Contacts
                              for (int i = 0; i < data.length(); i++) {
                                  JSONObject c = data.getJSONObject(i);
                                  audioHistory = c.getString("message_audio");
                                  chatMessages.get(position).setMessage_audio(audioHistory);
                              }
                          }

                      } catch (JSONException e) {
                          e.printStackTrace();
                      }

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

          @Override
          protected void onPostExecute(String result) {
              super.onPostExecute(result);
              // Dismiss the progress dialog
              if (error_code == 0) {
                  holder.gvAudioCancel.setVisibility(View.GONE);
                  holder.ivAudioPlay.setVisibility(View.VISIBLE);
                  chatMessages.get(position).setSend(false);
                  chatMessages.get(position).setHistory(true);
                  notifyDataSetChanged();

              } else {
                  Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
                  holder.gvAudioCancel.setVisibility(View.GONE);
                  holder.ivAudioUpload.setVisibility(View.VISIBLE);
              }
              //adapter.notifyDataSetChanged();
          }
      }

      private class DownloadVideo extends AsyncTask<Void, Void, Void> {

          String urlDownload;
          ViewHolder holder;
          int error_code;
          int position;
          boolean downloaded = false;

          public DownloadVideo(ViewHolder holder, String urlDownload, int position) {
              this.holder = holder;
              this.urlDownload = urlDownload;
              this.position = position;
          }

          @Override
          protected void onPreExecute() {
              super.onPreExecute();
              // Showing progress dialog
              holder.gvVideoCancel.setVisibility(View.VISIBLE);
          }

          @Override
          protected Void doInBackground(Void... arg0) {
              try {
                  String root = Environment.getExternalStorageDirectory().getPath();
                  File myDir = new File(root + "/Quranology/QuranologyVideos");
                  if (!myDir.exists()) {
                      myDir.mkdirs();
                  }
                  String name = "video"+System.currentTimeMillis() + ".mp4";
                  myDir = new File(myDir, name);
              *//*String rootDir = Environment.getExternalStorageDirectory()
                    + File.separator + "Video";
            File rootFile = new File(rootDir);
            rootFile.mkdir();*//*
                URL url = new URL(urlDownload);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.connect();
                FileOutputStream f = new FileOutputStream(myDir);
                InputStream in = c.getInputStream();
                BufferedInputStream inStream = new BufferedInputStream(in, 1024 * 5);

                byte[] buffer = new byte[5 * 1024];
                int len1 = 0;
                while ((len1 = inStream.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.flush();
                f.close();
                inStream.close();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Video downloaded", Toast.LENGTH_SHORT).show();
                    }
                });
                chatMessages.get(position).setHistory(true);
                chatMessages.get(position).setReceive(false);
                downloaded = true;
            } catch (IOException e) {
                downloaded = false;
                Log.d("Error....", e.toString());
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (downloaded) {
                holder.gvVideoCancel.setVisibility(View.GONE);
            } else {
                holder.gvVideoCancel.setVisibility(View.GONE);
                holder.ivVideoDownload.setVisibility(View.VISIBLE);
            }
                *//*chatMessages.get(position).setSend(false);
                chatMessages.get(position).setHistory(true);*//*

            //adapter.notifyDataSetChanged();
        }
    }

    private class UploadImageFile extends AsyncTask<Void, Void, String> {

        String sResponse;
        ViewHolder holder;
        int error_code;
        int position;

        public UploadImageFile(ViewHolder holder, int position) {
            this.holder = holder;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            holder.gvCancel.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlUpload);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {

                            }
                        });

                File sourceFile = new File(Constant.FILEPATH);

                // Adding file data to http body
                entity.addPart("image_1", new FileBody(sourceFile));

                System.out.println("entity::" + entity);

                //totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    System.out.println("responseString" + responseString);
                    try {
                        JSONObject jsonObj = new JSONObject(responseString);
                        error_code = jsonObj.getInt("error_code");
                        if (error_code == 0) {
                            JSONArray data = jsonObj.getJSONArray("data");
                            System.out.println("jsonArray:::: in upload" + data);

                            // looping through All Contacts
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject c = data.getJSONObject(i);
                                imageHistory = c.getString("message_image");
                                chatMessage.setMessage_image(imageHistory);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (error_code == 0) {
                holder.gvCancel.setVisibility(View.GONE);
                chatMessages.get(position).setSend(false);
                chatMessages.get(position).setHistory(true);
            } else {
                Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
                holder.gvCancel.setVisibility(View.GONE);
                holder.ivImageUpload.setVisibility(View.VISIBLE);
            }
            //adapter.notifyDataSetChanged();
        }
    }

    private class UploadVideoFile extends AsyncTask<Void, Void, String> {

        String sResponse;
        ViewHolder holder;
        int error_code;
        int position;

        public UploadVideoFile(ViewHolder holder, int position) {
            this.holder = holder;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            holder.gvVideoCancel.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlUpload);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {

                            }
                        });

                File sourceFile = new File(Constant.FILEPATH);
                File thumbnail = new File(Constant.VIDEO_THUMBNAIL_PATH);

                // Adding file data to http body
                entity.addPart("video", new FileBody(sourceFile));
                entity.addPart("thumb_image", new FileBody(thumbnail));

                System.out.println("sourceFile::" + sourceFile);

                //totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    System.out.println("responseString" + responseString);
                    try {
                        JSONObject jsonObj = new JSONObject(responseString);
                        error_code = jsonObj.getInt("error_code");
                        if (error_code == 0) {
                            JSONArray data = jsonObj.getJSONArray("data");
                            System.out.println("jsonArray:::: in upload" + data);

                            // looping through All Contacts
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject c = data.getJSONObject(i);
                                videoHistory = c.getString("message_video_thumb");
                                chatMessages.get(position).setMessage_video_thumb(videoHistory);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (error_code == 0) {
                holder.gvVideoCancel.setVisibility(View.GONE);
                chatMessages.get(position).setSend(false);
                chatMessages.get(position).setHistory(true);
            } else {
                Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
                holder.gvVideoCancel.setVisibility(View.GONE);
                holder.ivVideoUpload.setVisibility(View.VISIBLE);
            }
            //adapter.notifyDataSetChanged();
        }
    }
*/
    private ViewHolder createViewHolder(View convertView) {

        ViewHolder holder = new ViewHolder();

        holder.content = convertView.findViewById(R.id.content);
        holder.contentWithBG = convertView.findViewById(R.id.contentWithBackground);

        // text layout
        holder.txtMessage = convertView.findViewById(R.id.txtMessage);
        holder.txtInfo = convertView.findViewById(R.id.txtInfo);
        //holder.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
        holder.txtUser = convertView.findViewById(R.id.txtUser);
        holder.layoutMessage = convertView.findViewById(R.id.message);

        // image layout
        holder.layoutImage = convertView.findViewById(R.id.image);
        holder.tvImageSize = convertView.findViewById(R.id.tvImageSize);
        holder.tvImageTime = convertView.findViewById(R.id.tvImageTime);
        holder.ivImageDownload = convertView.findViewById(R.id.ivImageDownload);
        holder.ivImageUpload = convertView.findViewById(R.id.ivImageUpload);
        holder.ivImageChat = convertView.findViewById(R.id.ivImageChat);
        holder.gvCancel = convertView.findViewById(R.id.gvCancel);

        // video layout
        holder.layoutVideo = convertView.findViewById(R.id.video);
        holder.tvVideoDuration = convertView.findViewById(R.id.tvVideoDuration);
        holder.tvVideoSize = convertView.findViewById(R.id.tvVideoSize);
        holder.tvVideoTime = convertView.findViewById(R.id.tvVideoTime);
        holder.ivVideoDownload = convertView.findViewById(R.id.ivVideoDownload);
        holder.ivVideoUpload = convertView.findViewById(R.id.ivVideoUpload);
        holder.ivVideoChat = convertView.findViewById(R.id.ivVideoChat);
        holder.gvVideoCancel = convertView.findViewById(R.id.gvCancelVideo);

        // audio layout
        holder.layoutAudio = convertView.findViewById(R.id.audio);
        holder.tvAudioDuration = convertView.findViewById(R.id.tvAudioDuration);
        holder.tvAudioSize = convertView.findViewById(R.id.tvAudioSize);
        holder.tvAudioTime = convertView.findViewById(R.id.tvAudioTime);
        holder.ivAudioDownload = convertView.findViewById(R.id.ivAudioDownload);
        holder.ivAudioUpload = convertView.findViewById(R.id.ivAudioUpload);
        holder.ivAudioPlay = convertView.findViewById(R.id.ivAudioPlay);
        holder.ivAudioPause = convertView.findViewById(R.id.ivAudioPause);
        holder.gvAudioCancel = convertView.findViewById(R.id.gvCancelAudio);
        holder.progressBarAudio = convertView.findViewById(R.id.audioProgressBar);

        return holder;
    }

    public void add(ChatMessageModel message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessageModel> messages) {
        chatMessages.addAll(messages);
    }

    public String getDate() {
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(calander.getTime());
        //System.out.println("Current date"+date);
        return date;
    }

    static class ViewHolder {
        TextView txtMessage;
        TextView txtInfo;
        LinearLayout content;
        LinearLayout layoutMessage;
        LinearLayout contentWithBG;
        ImageView ivImageDownload;
        ImageView ivImageChat;
        ImageView ivImageUpload;
        TextView tvImageSize;
        TextView tvImageTime;
        CustomGifView gvCancel;
        CustomGifView gvVideoCancel;
        CustomGifViewAudio gvAudioCancel;
        TextView tvVideoTime;
        TextView tvVideoSize;
        TextView tvVideoDuration;
        ImageView ivVideoChat;
        ImageView ivVideoDownload;
        ImageView ivVideoUpload;
        TextView tvAudioSize;
        TextView tvAudioDuration;
        TextView tvAudioTime;
        ImageView ivAudioUpload;
        ImageView ivAudioDownload;
        ImageView ivAudioPlay;
        ImageView ivAudioPause;
        ProgressBar progressBarAudio;
        // public CircleImageView avatar;
        TextView txtUser;
        RelativeLayout layoutImage, layoutVideo, layoutAudio;
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadAudio extends AsyncTask<Void, Void, Void> {

        final String urlDownload;
        final ViewHolder holder;
        final int position;
        int error_code;
        boolean downloaded = false;

        DownloadAudio(ViewHolder holder, String urlDownload, int position) {
            this.holder = holder;
            this.urlDownload = urlDownload;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            holder.gvAudioCancel.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String root = Environment.getExternalStorageDirectory().getPath();
                File myDir = new File(root + "/Quranology/QuranologyAudio");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                String name = "audio" + System.currentTimeMillis() + ".m4a";
                myDir = new File(myDir, name);
            /*String rootDir = Environment.getExternalStorageDirectory()
                    + File.separator + "Video";
            File rootFile = new File(rootDir);
            rootFile.mkdir();*/
                System.out.println("myDir" + myDir.toString());
                System.out.println("urlDownload:::" + urlDownload);
                URL url = new URL(urlDownload);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();
                FileOutputStream f = new FileOutputStream(myDir);
                InputStream in = c.getInputStream();
                BufferedInputStream inStream = new BufferedInputStream(in, 1024);

                byte[] buffer = new byte[1024];
                int len1;
                while ((len1 = inStream.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.flush();
                f.close();
                inStream.close();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Audio downloaded", Toast.LENGTH_SHORT).show();
                    }
                });
                downloaded = true;
            } catch (IOException e) {
                downloaded = false;
                Log.d("Error....", e.toString());
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (downloaded) {
                holder.gvAudioCancel.setVisibility(View.GONE);
                chatMessages.get(position).setHistory(true);
                chatMessages.get(position).setReceive(false);
                notifyDataSetChanged();
                holder.ivAudioPlay.setVisibility(View.VISIBLE);
            } else {
                holder.gvAudioCancel.setVisibility(View.GONE);
                holder.ivAudioDownload.setVisibility(View.VISIBLE);
            }
        }
    }
}