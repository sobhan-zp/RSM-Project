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
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pro.rasht.museum.ar.Activity.AddPlaceActivity;
import pro.rasht.museum.ar.Activity.MainActivity;
import pro.rasht.museum.ar.Activity.EnterProfileActivity;
import pro.rasht.museum.ar.Classes.CircularImageView;
import pro.rasht.museum.ar.Classes.SavePref;
import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.network.AppController;

public class FragmentProfile extends Fragment {


    @BindView(R.id.btn_insert_profile)
    Button btnInsertProfile;
    Unbinder unbinder;
    @BindView(R.id.profile_image)
    CircularImageView profileImage;
    @BindView(R.id.btn_insert_location_profile)
    Button btnInsertLocationProfile;
    @BindView(R.id.tv_name_profile)
    TextView tvNameProfile;
    @BindView(R.id.tv_family_profile)
    TextView tvFamilyProfile;
    @BindView(R.id.tv_state_profile)
    TextView tvStateProfile;
    @BindView(R.id.tv_city_profile)
    TextView tvCityProfile;
    @BindView(R.id.tv_email_profile)
    TextView tvEmailProfile;
    @BindView(R.id.tv_phone_profile)
    TextView tvPhoneProfile;
    @BindView(R.id.li_exit_profil)
    LinearLayout liExitProfil;
    private FragmentActivity context;
    private View view;
    private LinearLayoutManager mLayoutManager;


    SavePref save;

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
        save = new SavePref(context);



        tvNameProfile.append(save.load(AppController.SAVE_USER_Name, " "));
        tvFamilyProfile.append(save.load(AppController.SAVE_USER_Family, " "));
        tvStateProfile.append(save.load(AppController.SAVE_USER_STATE, " "));
        tvCityProfile.append(save.load(AppController.SAVE_USER_CITY, " "));
        tvEmailProfile.append(save.load(AppController.SAVE_USER_EMAIL, " "));
        tvPhoneProfile.append(save.load(AppController.SAVE_USER_MOBILE, " "));



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
                Intent intent = new Intent(context, AddPlaceActivity.class);
                startActivity(intent);
            }
        });

        liExitProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save.save(AppController.SAVE_LOGIN, "0");
                startActivity(new Intent(context, MainActivity.class));
                getActivity().finish();
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
