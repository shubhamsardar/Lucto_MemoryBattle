package dsardy.in.memorybattle.viewholders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import dsardy.in.memorybattle.R;

/**
 * Created by Shubham on 10/1/2017.
 */

public class InputbuttonViewHolder extends RecyclerView.ViewHolder {

    public FloatingActionButton floatingActionButton;

    public InputbuttonViewHolder(View itemView) {
        super(itemView);
        floatingActionButton = itemView.findViewById(R.id.floatingActionButton);
    }

}