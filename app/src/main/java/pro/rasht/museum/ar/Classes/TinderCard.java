package pro.rasht.museum.ar.Classes;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import pro.rasht.museum.ar.Fragment.FragmentGallery;
import pro.rasht.museum.ar.Model.Model_Gallery;
import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.network.AppController;
import pro.rasht.museum.ar.network.CustomRequest;

import static cn.easyar.engine.EasyAR.getApplicationContext;

/**
 * Created by janisharali on 19/08/16.
 */
@Layout(R.layout.tinder_card_view)
public class TinderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;


    @View(R.id.tv_like_gallery)
    private TextView tvLikeGallery;


    @View(R.id.tv_years_gallery)
    private TextView tvYearsGallery;


    @View(R.id.tv_title_gallery)
    private TextView tvTitleGallery;

    @View(R.id.btn_desc_gallery)
    private ImageView btnDescGallery;

    @View(R.id.btn_like_gallery)
    private LikeButton btnLikeGallery;


    private Model_Gallery mModel_gallery;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    SavePref save;

    public TinderCard(Context context, Model_Gallery model_gallery, SwipePlaceHolderView swipeView) {
        mContext = context;
        mModel_gallery = model_gallery;
        mSwipeView = swipeView;
        save=new SavePref(context);
    }

    @Resolve
    private void onResolved(){

        /*int drawableId = mContext.getResources().getIdentifier(
                mModel_gallery.getTitle_img(),"drawable",mContext.getPackageName());*/


        Glide.with(mContext).load(mModel_gallery.getImage_img()).into(profileImageView);
        tvTitleGallery.setText(mModel_gallery.getTitle_img());
        tvYearsGallery.setText(mModel_gallery.getYears_img());

        btnDescGallery.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {


                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_desc_gallery);
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                TextView text = (TextView) dialog.findViewById(R.id.text);
                text.setText(mModel_gallery.getDesc_img());
                // if button is clicked, close the custom dialog

                dialog.show();


            }
        });



        tvLikeGallery.setText(mModel_gallery.getLike_img());


        btnLikeGallery.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                tvLikeGallery.setText(   String.valueOf(Integer.parseInt(mModel_gallery.getLike_img()) + 1));
                like(
                        mModel_gallery.getId_img(),
                        save.load(AppController.SAVE_USER_ID,"10"),
                        mModel_gallery.getId_img()
                );
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(mContext, "پشیمون شدم!", Toast.LENGTH_SHORT).show();
                tvLikeGallery.setText(   String.valueOf(mModel_gallery.getLike_img()));
                dislike(
                        save.load(AppController.SAVE_USER_ID,"10"),
                        mModel_gallery.getId_img()
                );

            }
        });

    }

    @Click(R.id.profileImageView)
    private void onClick()

    {
        Log.d("EVENT", "profileImageView click");
        mSwipeView.addView(this);
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);


    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");


    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }



    private void like(String id, String uid, String gid) {

        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("uid", uid);
        params.put("gid", gid);


        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, AppController.URL_GALLERY_LIKE, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject resp = response;
                //Log.e("TAG--------OK", resp.toString());

                try {
                    if (resp.getString("status").equals("200")) {

                        Toast.makeText(mContext, "liked", Toast.LENGTH_SHORT).show();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG--------Error", "Error: " + error.getMessage());
                //AppController.message(mContext, "لطفا در زمان دیگری اقدام کنید");
            }
        });
        jsonObjReq.setShouldCache(false);
        //myRequestQueue.getCache().clear();
        AppController.getInstance().addToRequestQueue(jsonObjReq, "URL_GALLERY_LIKE");
    }




    private void dislike( String uid, String gid) {

        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("gid", gid);


        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, AppController.URL_GALLERY_DISLIKE, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject resp = response;
                //Log.e("TAG--------OK", resp.toString());

                try {
                    if (resp.getString("status").equals("200")) {

                        Toast.makeText(mContext, "dis likeed", Toast.LENGTH_SHORT).show();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG--------Error", "Error: " + error.getMessage());
                //AppController.message(mContext, "لطفا در زمان دیگری اقدام کنید");
            }
        });
        jsonObjReq.setShouldCache(false);
        //myRequestQueue.getCache().clear();
        AppController.getInstance().addToRequestQueue(jsonObjReq, "URL_GALLERY_LIKE");
    }


}
