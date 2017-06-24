package com.alfanse.author.Adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfanse.author.Interfaces.onAuthorItemClickListener;
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

public class AuthorsAdapter extends RecyclerView.Adapter<AuthorsAdapter.AuthorViewHolder> {

    private final onAuthorItemClickListener listener;
    private Context mContext;
    private ArrayList<Author> mListAuthors;

    public AuthorsAdapter(Context context, ArrayList<Author> listAuthors, onAuthorItemClickListener listener) {
        mContext = context;
        mListAuthors = listAuthors;
        this.listener = listener;
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_author, parent, false);

        return new AuthorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {
        holder.bind(mListAuthors.get(position), listener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mListAuthors.size();
    }


    public class AuthorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_author_image_item_author)
        ImageView authorImage;
        @BindView(R.id.text_author_name_item_author)
        TextView textAuthorName;
        @BindView(R.id.text_author_action_item_author)
        TextView textAction;


        public AuthorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Author author, final onAuthorItemClickListener listener) {

            RequestOptions authorImageOptions = new RequestOptions()
                    .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                    .fitCenter()
                    .circleCrop();

            Glide.with(mContext)
                    .load(author.getProfileImage())
                    .apply(authorImageOptions)
                    .into(authorImage);

            textAuthorName.setText(author.getName());

            if (author.isFollowingAuthor()) {
                textAction.setText(mContext.getString(R.string.action_unfollow));

            } else {
                textAction.setText(mContext.getString(R.string.action_follow));
            }

            textAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onActionFollowClick(author);
                    textAction.setClickable(false);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textAction.setClickable(true);
                            if (author.isFollowingAuthor()) {
                                textAction.setText(mContext.getString(R.string.action_follow));
                                author.setFollowingAuthor(true);
                            } else {
                                textAction.setText(mContext.getString(R.string.action_unfollow));
                                author.setFollowingAuthor(false);
                            }
                        }
                    }, 2000);
                }
            });

            authorImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAuthorClick(author);
                }
            });

            textAuthorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAuthorClick(author);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(author);
                }
            });
        }
    }
}
