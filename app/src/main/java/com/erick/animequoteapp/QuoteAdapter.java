package com.erick.animequoteapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {
    private List<Quote> quoteList;

    public QuoteAdapter(List<Quote> quoteList) {
        this.quoteList = quoteList;
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_item, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quoteList.get(position);
        holder.quoteTextView.setText(quote.getQuote());

        // Update the UI based on whether the quote is a favorite
        if (quote.isFavorite()) {
            holder.favoriteImageView.setImageResource(R.drawable.star_outline);
        } else {
            holder.favoriteImageView.setImageResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }

    public class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView quoteTextView;
        ImageView menuImageView;
        ImageView favoriteImageView;  // new ImageView for favorite status

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            quoteTextView = itemView.findViewById(R.id.quoteTextView);
            menuImageView = itemView.findViewById(R.id.menuImageView);
            favoriteImageView = itemView.findViewById(R.id.favoriteImageView);

            menuImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v);
                }
            });
        }

        // Show the popup menu
        private void showPopupMenu(View view) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.quote_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_favorite) {
                        // Mark the quote as a favorite
                        Quote quote = quoteList.get(getAdapterPosition());
                        quote.setFavorite(!quote.isFavorite());  // toggle favorite status
                        // Notify the adapter that the item was updated so the UI can be refreshed
                        notifyItemChanged(getAdapterPosition());
                        return true;
                    } else if (itemId == R.id.action_delete) {
                        // Delete the quote
                        quoteList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            popup.show();
        }
    }
}

