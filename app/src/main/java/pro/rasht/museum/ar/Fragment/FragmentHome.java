package pro.rasht.museum.ar.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import pro.rasht.museum.ar.MapsActivity;
import pro.rasht.museum.ar.PoiBrowserActivity;
import pro.rasht.museum.ar.R;


public class FragmentHome extends Fragment {


    @BindView(R.id.cv_ar_home)
    CardView cvArHome;
    @BindView(R.id.cv_vr_home)
    CardView cvVrHome;
    Unbinder unbinder;




    private FragmentActivity context;
    private View view;
    private LinearLayoutManager mLayoutManager;

    private ImageView gyroscopeObserver;

    public static FragmentHome newInstance() {

        Bundle args = new Bundle();
        FragmentHome fragment = new FragmentHome();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        unbinder = ButterKnife.bind(this, view);



        /*new ImageUtil(context,
                "http://globalmedicalco.com/photos/globalmedicalco/1/3586.jpg",
                "test1");*/


       /* loadTarget();*/





        cvArHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "مسیریابی تصویری", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(context, NavActivity.class);
                Intent intent = new Intent(context, MapsActivity.class);
                startActivity(intent);
                //startActivity(new Intent(context, ArModulActivity.class));

                /*Intent intent = new Intent(context, FMapActivity.class);
                startActivity(intent);*/
            }
        });


        cvVrHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "مکان یابی تصویری", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, PoiBrowserActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }

    /*private void loadTarget() {
        JsonArrayRequest req = new JsonArrayRequest(AppController.URL_TARGET,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("TAG---------OK", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = response.getJSONObject(i);
                                String url = object.getString("url");

                                Target target = new Target(
                                        String.valueOf(i),
                                        url.substring( url.lastIndexOf('/')+1, url.length() ),
                                        object.getString("url"),
                                        object.getString("value")
                                );

                                new ImageUtil(context, target.getUrl(), target.getName());

                                AppController.TARGET.add(target);
                            }
                            AppController.TARGET_NUMBERS = response.length();

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
        AppController.getInstance().addToRequestQueue(req, "loadTarget");
    }*/


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
