package pro.rasht.museum.ar.Fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pro.rasht.museum.ar.Classes.Gallery_Utils;
import pro.rasht.museum.ar.Classes.SavePref;
import pro.rasht.museum.ar.Classes.TinderCard;
import pro.rasht.museum.ar.Model.Model_Gallery;
import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.network.AppController;


public class FragmentGallery extends Fragment {

    public static TextView tvLikeGallery;
    Unbinder unbinder;
    private FragmentActivity context;
    private View view;
    private LinearLayoutManager mLayoutManager;

    private SwipePlaceHolderView mSwipeView;

    SavePref save;

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

        save.save(AppController.COUNTER_GALLERY , 0);

        tvLikeGallery = (TextView)view.findViewById(R.id.tv_like_gallery);
        mSwipeView = (SwipePlaceHolderView) view.findViewById(R.id.swipeView);

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


        for (Model_Gallery model_gallery : Gallery_Utils.loadModel(getActivity().getApplicationContext())) {
            mSwipeView.addView(new TinderCard(context, model_gallery, mSwipeView));
        }

        view.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);

                //save.save(AppController.COUNTER_GALLERY , save.load(AppController.COUNTER_GALLERY , 0)-1);


                tvLikeGallery.setText(  "تعداد پسندیده ها :" + save.load(AppController.COUNTER_GALLERY , 0));
            }
        });

        view.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);

                //save.save(AppController.COUNTER_GALLERY , save.load(AppController.COUNTER_GALLERY , 0)+1);

                tvLikeGallery.setText(  "تعداد پسندیده ها :" + save.load(AppController.COUNTER_GALLERY , 0));
            }
        });

        ///java code
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
