package pro.rasht.museum.ar.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pro.rasht.museum.ar.Activity.EnterProfileActivity;
import pro.rasht.museum.ar.Activity.PlaceActivity;
import pro.rasht.museum.ar.Classes.CircularImageView;
import pro.rasht.museum.ar.R;

public class FragmentProfile extends Fragment {


    @BindView(R.id.btn_insert_profile)
    Button btnInsertProfile;
    Unbinder unbinder;
    @BindView(R.id.profile_image)
    CircularImageView profileImage;
    @BindView(R.id.btn_insert_location_profile)
    Button btnInsertLocationProfile;
    private FragmentActivity context;
    private View view;
    private LinearLayoutManager mLayoutManager;

    public static FragmentProfile newInstance() {

        Bundle args = new Bundle();
        FragmentProfile fragment = new FragmentProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        unbinder = ButterKnife.bind(this, view);
        //java code
        btnInsertProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EnterProfileActivity.class);
                startActivity(intent);
            }
        });



        btnInsertLocationProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaceActivity.class);
                startActivity(intent);
            }
        });


        ///java code

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
