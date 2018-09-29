package pro.rasht.museum.ar.network;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import pro.rasht.museum.ar.Model.HoloModel;
import pro.rasht.museum.ar.Model.PointModel;
import pro.rasht.museum.ar.Model.VrModel;
import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.Model.Target;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by Maziar on 3/23/2018.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();


    public final static String URL = "http://archism.direct/rsm/index.php/api/";

    public final static String URL_POINT = URL + "point/";
    public final static String URL_POINT_DETAIL = URL + "point/detail/";

    public final static String URL_AR = URL + "ar/";
    public final static String URL_AR_DETAIL = URL + "ar/detail/";

    public final static String URL_VR = URL + "vr/";
    public final static String URL_VR_DETAIL = URL + "vr/detail/";

    public final static String URL_HOLO = URL + "holo/";
    public final static String URL_HOLO_DETAIL = URL + "holo/detail/";



    public final static String URL_SIGNUP = URL + "user/signup";


    public final static String SAVE_LOGIN = "SAVE_LOGIN";
    public final static String SAVE_USER_ID = "SAVE_USER_ID";

    public final static String SAVE_USER_Name = "SAVE_USER_NAME";
    public final static String SAVE_USER_Family = "SAVE_USER_Family";
    public final static String SAVE_USER_STATE = "SAVE_USER_STATE";
    public final static String SAVE_USER_CITY = "SAVE_USER_CITY";
    public final static String SAVE_USER_EMAIL = "SAVE_USER_EMAIL";
    public final static String SAVE_USER_MOBILE = "SAVE_USER_MOBILE";

    public final static String SAVE_USER_ACTIVE = "SAVE_USER_ACTIVE";

    public final static String SAVE_COMPLETE_PROFILE = "SAVE_COMPLETE_PROFILE";

    public static String SAVE_PATH = "SAVE_LOGIN";


    public static ArrayList<String> TARGET_PATH = new ArrayList<>();
    public static ArrayList<Target> TARGET = new ArrayList<>();
    public static ArrayList<VrModel> VRMODEL = new ArrayList<>();
    public static ArrayList<HoloModel> HOLOMODEL = new ArrayList<>();
    public static ArrayList<PointModel> POINTMODEL = new ArrayList<>();
    public static int TARGET_NUMBERS = 0;
    public static int VRMODEL_NUMBERS = 0;
    public static int HOLOMODEL_NUMBERS = 0;
    public static int POINTMODEL_NUMBERS = 0;


    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/irsans_Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    (ImageLoader.ImageCache) new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public static void message(Context context ,String txt) {
        Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
    }

    public static void message(Context context ,String txt, int time) {
        Toast.makeText(context, txt, time).show();
    }

}