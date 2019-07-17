package com.example.xyzreader2.presentation.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xyzreader2.R;
import com.example.xyzreader2.databinding.ItemTextBodyPartBinding;

import java.util.List;

public class ArticleTextBodyAdapter extends RecyclerView.Adapter<ArticleTextBodyAdapter.ArticleBodyViewHolder> {
    private List<String> mBodyParts;
    private Context mContext;

    public void updateList(List<String> parts) {
        mBodyParts = parts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleBodyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ItemTextBodyPartBinding itemViewReviewBinding = DataBindingUtil.inflate(inflater, R.layout.item_text_body_part, parent, false);
        return new ArticleBodyViewHolder(itemViewReviewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleBodyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mBodyParts == null) return 0;
        else return mBodyParts.size();
    }

    class ArticleBodyViewHolder extends RecyclerView.ViewHolder {
        private final ItemTextBodyPartBinding binding;
        private static final String EMPTY = "";

        ArticleBodyViewHolder(@NonNull ItemTextBodyPartBinding articleBinding) {
            super(articleBinding.getRoot());
            this.binding = articleBinding;
        }

        void bind(int position) {
            String bodyPart = mBodyParts.get(position);
            if (!TextUtils.isEmpty(bodyPart) && mContext != null) {
                binding.bodyPart.setText(bodyPart);
            } else {
                binding.bodyPart.setText(EMPTY);
            }
        }

    }
}


