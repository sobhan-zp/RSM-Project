package pro.rasht.museum.ar.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import pro.rasht.museum.ar.Model.GalleryModel;
import pro.rasht.museum.ar.R;


public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.MyViewHolder> {

    private List<GalleryModel> itemList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_title_gallery;
        public CardView ll_bg_gallery;
        public ImageView img_avatar_gallery;

        public MyViewHolder(View view) {
            super(view);

            this.tv_title_gallery = (TextView) view.findViewById(R.id.tv_title_gallery);
            this.img_avatar_gallery = (ImageView) view.findViewById(R.id.img_avatar_gallery);
            this.ll_bg_gallery = (CardView) view.findViewById(R.id.ll_bg_gallery);

        }
    }


    public GalleryRecyclerAdapter(Context context, List<GalleryModel> itemList) {
        this.itemList = itemList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_gallery, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final GalleryModel Item = itemList.get(position);

        holder.tv_title_gallery.setText(Item.getTitle());

        /*Glide.with(holder.img_avatar_gallery.getContext())
                .load(Item.getImage())
                .fitCenter()
                .into(holder.img_avatar_gallery);*/


        holder.ll_bg_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_TITLE, Item.getTitle());
                intent.putExtra(DetailActivity.EXTRA_DESC, Item.getDescription());
                intent.putExtra(DetailActivity.EXTRA_IMAGE, Item.getImage());
                intent.putExtra(DetailActivity.EXTRA_TEXT1, Item.getText1());
                mContext.startActivity(intent);*/

            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }


}
