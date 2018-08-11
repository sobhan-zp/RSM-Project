package pro.rasht.museum.ar.Classes;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import pro.rasht.museum.ar.network.AppController;


public class ImageUtil {

    public static final String TAG = ImageUtil.class.getSimpleName();

    public final static String name_ = "imgcatch"; //Folder name in device android/data/


    private static String path;
    private static ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ImageUtil(final Context context, String url, final String filename) {

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        imageLoader.get(url, new ImageLoader.ImageListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                saveToInternal(response.getBitmap(), context, filename);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AppController.message(context, "Error: " + error.getMessage());
            }
        }).getBitmap();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveToInternal(Bitmap bitmapImage, Context context, String name) {

        //ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir

        ContextWrapper cw = new ContextWrapper(context);


        //File directory = cw.getDir(name_, Context.MODE_PRIVATE);
        File directory = cw.getFilesDir();

        // Create imageDir
        File mypath = new File(directory, name);


        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        path = directory.getAbsolutePath();

        //Log.e("TAG-----save", path);
        Log.e(TAG, "saved: " + name);

        AppController.TARGET_PATH.add(path);
    }


    public static String getPath() {
        return path;
    }


    /**
     * Method to retrieve image from your device
     **/
    public static Bitmap loadImageFromStorage(String path, String name) {
        Bitmap b;
        try {
            File f = new File(path, name);

            b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
            //Log.e("TAG-----", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


}
