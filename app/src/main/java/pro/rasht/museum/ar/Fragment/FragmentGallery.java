package pro.rasht.museum.ar.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import pro.rasht.museum.ar.Adapter.GalleryRecyclerAdapter;
import pro.rasht.museum.ar.Data.Gallery_app;
import pro.rasht.museum.ar.Model.GalleryModel;
import pro.rasht.museum.ar.R;


public class FragmentGallery extends Fragment {




    private FragmentActivity context;
    private View view;
    private LinearLayoutManager mLayoutManager;


    private List<GalleryModel> itemList = new ArrayList<>();
    private RecyclerView rv_gallery;
    private GalleryRecyclerAdapter adapter;
    

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
        //java code




        rv_gallery = (RecyclerView)view.findViewById(R.id.rv_gallery);
        adapter = new GalleryRecyclerAdapter(context, itemList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        rv_gallery.setLayoutManager(mLayoutManager);
        rv_gallery.setItemAnimator(new DefaultItemAnimator());
        rv_gallery.setAdapter(adapter);




        itemList.addAll(new Gallery_app().getData());
        adapter.notifyDataSetChanged();







        ///java code
        return view;
    }





}
