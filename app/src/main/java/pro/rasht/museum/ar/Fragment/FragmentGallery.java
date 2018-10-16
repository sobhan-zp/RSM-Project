package pro.rasht.museum.ar.Fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pro.rasht.museum.ar.Activity.AddPlaceActivity;
import pro.rasht.museum.ar.Classes.Gallery_Utils;
import pro.rasht.museum.ar.Classes.SavePref;
import pro.rasht.museum.ar.Classes.TinderCard;
import pro.rasht.museum.ar.Model.Model_Gallery;
import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.network.AppController;
import pro.rasht.museum.ar.network.CustomRequest;


public class FragmentGallery extends Fragment {

    Unbinder unbinder;
    /*@BindView(R.id.tv_title_gallery)
    private TextView tvTitleGallery;
    @BindView(R.id.tv_years_gallery)
    private TextView tvYearsGallery;*/
    private FragmentActivity context;
    private View view;
    private LinearLayoutManager mLayoutManager;

    private SwipePlaceHolderView mSwipeView;

    SavePref save;
    private Model_Gallery mModel_gallery;

    public static FragmentGallery newInstance() {

        Bundle args = new Bundle();
        FragmentGallery fragment = new FragmentGallery();
        fragment.setArguments(args);
        return fragment;
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        context = getActivity();
        save = new SavePref(context);

        //java code

        AppController.GALLERYMODEL.clear();
        loadGallery();


        mSwipeView = (SwipePlaceHolderView) view.findViewById(R.id.swipeView);
        /*tvTitleGallery = (TextView) view.findViewById(R.id.tv_title_gallery);
        tvYearsGallery = (TextView) view.findViewById(R.id.tv_years_gallery);*/

        int bottomMargin = Gallery_Utils.dpToPx(160);
        Point windowSize = Gallery_Utils.getDisplaySize(getActivity().getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setHeightSwipeDistFactor(10)
                .setWidthSwipeDistFactor(5)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));


        //Log.e("-----------size()>" , String.valueOf(AppController.GALLERYMODEL.size()));





        ///java code
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    private void loadGallery() {
        JsonArrayRequest req = new JsonArrayRequest(AppController.URL_GALLERY,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("TAG---------OK", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = response.getJSONObject(i);

                                Model_Gallery model = new Model_Gallery();
                                model.setId_img(object.getString("id"));
                                model.setTitle_img(object.getString("caption"));
                                model.setYears_img(object.getString("year"));
                                model.setDesc_img(object.getString("description"));
                                model.setLike_img(object.getString("count"));
                                model.setImage_img(object.getString("img"));

                                AppController.GALLERYMODEL.add(model);


                            }
                            AppController.GALLERYMODEL_NUMBERS = response.length();

                            for (Model_Gallery model_gallery : AppController.GALLERYMODEL) {

                                mSwipeView.addView(new TinderCard(context, model_gallery, mSwipeView));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG------------Error", "Error: " + error.getMessage());
            }
        });
        req.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(req, "loadGallery");
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
