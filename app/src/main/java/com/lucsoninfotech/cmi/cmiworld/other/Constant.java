package com.lucsoninfotech.cmi.cmiworld.other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constant {

    public static final ArrayList array_images = new ArrayList();
    public static final ArrayList array_category = new ArrayList();
    public static final List<HashMap<String, String>> category_list = new ArrayList<>();
    public static final String Image_url = "http://18.220.189.209/admin/uploads/images/";
    public static final String PAYPAL_CLIENT_ID = "AZBf-hH7t4cVbraWupQfWqX2POatxxjQ1chZE623irgPVZCpQ3O9k7Ewl9uJKJ7ChWL9c7w1lsN9qFv6";
    public static final String PAYPAL_CLIENT_SECRET = "EFU2oXqX42pEMvBaZzlKFqrDqX7W--U5T-jRSCDDUWOU0jDtckmqDdRyJ3oFEncvs6CYo8XTYDecEIgR";
    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "USD";
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;
    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    private static final String Root_Url = "http://18.220.189.209/admin/ws/";
    public static final String LOGIN_URL = Root_Url + "login.php?";
    public static final String SIGNUP_URL = Root_Url + "register.php";
    public static final String VerifyOTP_Url = Root_Url + "account-verify.php?email=";
    public static final String ViewProfileUrl = Root_Url + "profile-view.php?user_id=";
    public static final String UpdateProfilePic = Root_Url + "profile-update-multi.php";
    public static final String Question_Url = Root_Url + "user-rank.php";
    public static final String Forgot_Password_Step1 = Root_Url + "password-forgot.php?step=1&user_name=";
    public static final String Forgot_Password_Step2 = Root_Url + "password-forgot.php?step=2&user_name=";
    public static final String ProjectLike = Root_Url + "project-like.php?user_id=";
    public static final String ProjectDetailsUrl = Root_Url + "project-view.php?user_id=";
    public static final String SeeUpdatesDetailsUrl = Root_Url + "project-progress-view.php?project_id=";
    public static final String ProjectUrl = Root_Url + "projects-list.php?user_id=";
    public static final String SEMProjectList = Root_Url + "sem-project-list.php?sem_id=";
    public static final String SeeProcessDetailsUrl = Root_Url + "project-progress-add-update.php?progress_id=0&project_id=";
    public static final String PrivateChatUrl = Root_Url + "private-chat.php?sender_id=";
    public static final String ChatRoomUrl = Root_Url + "chat-room.php?user_id=";
    public static final String HistoryChatUrl = Root_Url + "history.php?sender_id=";
    public static final String MessgaeChatUrl = Root_Url + "messages.php?sender_id=";
    public static final String FilterUrl = Root_Url + "data-list.php";
    public static final String Change_Password = Root_Url + "password-change.php?";
    public static final String AddProject = Root_Url + "project-add.php?user_id=";
    public static final String BackedProject = Root_Url + "projects-backed-list.php?user_id=";
    public static final String VerifyPayment_URL = Root_Url + "give-donation.php?";
    public static final String AddUserCard = Root_Url + "user-card-add.php?";
    public static final String UserCardList = Root_Url + "user-cards-list.php?user_id=";
    public static final String UpdateUserCard = Root_Url + "user-card-update.php?card_id=";
    public static final String SearchProjectUrl = Root_Url + "projects-search.php?user_id=";
    public static final String FaqUrl = Root_Url + "faq.php";
    public static final String Testimonials_URL = Root_Url + "testimonial-add.php?testimonial_id=0&sem_id=";
    public static final String TestimonialsVIew_URL = Root_Url + "testimonial-view.php?type=";
    public static final String SEMDonerList = Root_Url + "donor-list-project.php?project_id=";
    private static final String BASE_URL = "https://lucsoninfotech.com/gcm-chat/v1";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
    public static String USER_EMAIL;
    public static String USER_ID;
    public static String USER_TYPE;
    public static String USER_NAME;

    //chatting URL and Data
    public static String USER_IMAGE;
    public static int USER_DEVELOPMENT;
    public static int USER_EXPERIENCE;
    public static Boolean Search_Fragment_Flag = false;
    public static Boolean Payment_flag = false;
    public static String Country_ID = "";
    public static ArrayList selected_videos = new ArrayList<>();
    public static String NGO_ID = "";
    public static String Category_ID = "";
    public static String Tags_ID = "";
    public static String TIMEZONE;
    public static String RECEIVER_ID;
    public static String SEM_DP;
    public static String SEM_NAME;
    // flag to identify whether to show single line
    // or multi line test push notification tray
    public static boolean appendNotificationMessages = true;

    public static boolean isOnline(Context applicationContext) {

        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }

        return netInfo != null && netInfo.isConnectedOrConnecting();

    }


}
