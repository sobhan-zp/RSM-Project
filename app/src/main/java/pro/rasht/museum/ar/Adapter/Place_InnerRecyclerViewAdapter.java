package pro.rasht.museum.ar.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pro.rasht.museum.ar.R;

/**
 * Created by Aroliant on 1/3/2018.
 */

public class Place_InnerRecyclerViewAdapter extends RecyclerView.Adapter<Place_InnerRecyclerViewAdapter.ViewHolder> {
    public ArrayList<String> nameList = new ArrayList<String>();

    public Place_InnerRecyclerViewAdapter(ArrayList<String> nameList) {

        this.nameList = nameList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemTextView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_expand_item_view, parent, false);


        Place_InnerRecyclerViewAdapter.ViewHolder vh = new Place_InnerRecyclerViewAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.name.setText(nameList.get(position));

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }


}
