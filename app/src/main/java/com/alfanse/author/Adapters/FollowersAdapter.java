package com.alfanse.author.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfanse.author.Models.Author;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Velocity-1601 on 4/19/2017.
 */

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowerViewHolder> {

    private final OnItemClickListener listener;
    private Context mContext;
    private ArrayList<Author> mListFollowers;

    public FollowersAdapter(Context context, ArrayList<Author> listFollowers, OnItemClickListener listener) {
        mContext = context;
        mListFollowers = listFollowers;
        this.listener = listener;
    }

    @Override
    public FollowerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follower, parent, false);

        return new FollowerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FollowerViewHolder holder, int position) {
        holder.bind(mListFollowers.get(position), listener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mListFollowers.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Author follower);
    }

    public class FollowerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.follower_image_item_follower)
        ImageView followerImage;
        @BindView(R.id.follower_name_item_follower)
        TextView textFollowerName;
        @BindView(R.id.follower_action_item_follower)
        TextView textAction;


        public FollowerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Author author, final OnItemClickListener listener) {

            RequestOptions authorImageOptions = new RequestOptions()
                    .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                    .fitCenter()
                    .circleCrop();

            Glide.with(mContext)
                    .load(author.getProfileImage())
                    .apply(authorImageOptions)
                    .into(followerImage);

            textFollowerName.setText(author.getName());

            if (author.getFollowing()) {
                textAction.setText("Unfollow");

            } else {
                textAction.setText("Follow");
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(author);
                }
            });
        }
    }
}
