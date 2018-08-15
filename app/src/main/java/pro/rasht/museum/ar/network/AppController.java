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

import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.Model.Target;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by Maziar on 3/23/2018.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();


    public final static String URL = "http://192.168.1.22/rashtpro/index.php/api/";

    public final static String URL_TARGET = URL + "target/";
    public final static String URL_TARGET_DETAIL = URL + "Target/detail/";



    public static String SAVE_PATH = "SAVE_LOGIN";


    public static ArrayList<String> TARGET_PATH = new ArrayList<>();
    public static ArrayList<Target> TARGET = new ArrayList<>();
    public static int TARGET_NUMBERS = 0;


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