package com.example.xyzreader2.presentation.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xyzreader2.R;
import com.example.xyzreader2.data.db.ArticleEntity;
import com.example.xyzreader2.databinding.ItemArticleBinding;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import static com.example.xyzreader2.utils.ArticleItemToArticleEntityConverter.OUTPUT_FORMAT;
import static com.example.xyzreader2.utils.ArticleItemToArticleEntityConverter.START_OF_EPOCH;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder> {

    // Use default locale format
    private List<ArticleEntity> mArticles;
    private ArticleListClickListener clickListener;
    private Context mContext;


    public ArticleListAdapter(ArticleListClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void updateList(List<ArticleEntity> articles) {
        mArticles = articles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ItemArticleBinding itemViewReviewBinding = DataBindingUtil.inflate(inflater, R.layout.item_article, parent, false);
        return new ArticleViewHolder(itemViewReviewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mArticles == null) return 0;
        else return mArticles.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemArticleBinding binding;
        private static final String EMPTY = "";

        ArticleViewHolder(@NonNull ItemArticleBinding articleBinding) {
            super(articleBinding.getRoot());
            this.binding = articleBinding;
            articleBinding.getRoot().setOnClickListener(this);
        }

        void bind(int position) {
            ArticleEntity articleItem = mArticles.get(position);
            if (articleItem != null && mContext != null) {
                if (!TextUtils.isEmpty(articleItem.getTitle())) {
                    binding.articleTitle.setText(articleItem.getTitle());
                } else {
                    binding.articleTitle.setText(EMPTY);
                }

                Date publishDate = new Date(articleItem.getPublishedDate());
                if (!publishDate.before(START_OF_EPOCH.getTime())) {
                    binding.articleSubtitle.setText(Html.fromHtml(
                            DateUtils.getRelativeTimeSpanString(
                                    publishDate.getTime(),
                                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                    DateUtils.FORMAT_ABBREV_ALL).toString()
                                    + "<br/>" + " by "
                                    + articleItem.getAuthor()));
                } else {
                    binding.articleSubtitle.setText(Html.fromHtml(
                            OUTPUT_FORMAT.format(publishDate)
                                    + "<br/>" + " by "
                                    + articleItem.getAuthor()));
                }

                if (!TextUtils.isEmpty(articleItem.getThumbUrl())) {
                    Picasso.get()
                            .load(articleItem.getThumbUrl())
                            .into(binding.thumbnail);
                } else {
                    binding.thumbnail.setImageDrawable(null);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (mArticles != null) {
                ArticleEntity articleItem = mArticles.get(getAdapterPosition());
                if (articleItem != null) {
                    clickListener.onArticleItemClick(articleItem.getId(), binding.thumbnail);
                }
            }
        }
    }

}


