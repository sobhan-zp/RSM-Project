package pro.rasht.museum.ar.Classes;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import pro.rasht.museum.ar.Fragment.FragmentGallery;
import pro.rasht.museum.ar.Model.Model_Gallery;
import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.network.AppController;

/**
 * Created by janisharali on 19/08/16.
 */
@Layout(R.layout.tinder_card_view)
public class TinderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

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

        int drawableId = mContext.getResources().getIdentifier(
                mModel_gallery.getDrawableName(),
                "drawable",
                mContext.getPackageName());

        Glide.with(mContext).load(drawableId).into(profileImageView);

        nameAgeTxt.setText(mModel_gallery.getName() + ", " + mModel_gallery.getAge());
        locationNameTxt.setText(mModel_gallery.getLocation());
    }

    @Click(R.id.profileImageView)
    private void onClick(){
        Log.d("EVENT", "profileImageView click");
        mSwipeView.addView(this);
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);

        save.save(AppController.COUNTER_GALLERY , save.load(AppController.COUNTER_GALLERY , 0)-1);

        FragmentGallery.tvLikeGallery.setText(  "تعداد پسندیده ها :" + save.load(AppController.COUNTER_GALLERY , 0));
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");


        save.save(AppController.COUNTER_GALLERY , save.load(AppController.COUNTER_GALLERY , 0)+1);

        FragmentGallery.tvLikeGallery.setText(  "تعداد پسندیده ها :" + save.load(AppController.COUNTER_GALLERY , 0));
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }
}
