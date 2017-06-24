package com.alfanse.author.Interfaces;

import com.alfanse.author.Models.Author;

/**
 * Created by Velocity-1601 on 6/24/2017.
 */

public interface onAuthorItemClickListener {
    void onItemClick(Author author);

    void onActionFollowClick(Author author);

    void onAuthorClick(Author author);
}
