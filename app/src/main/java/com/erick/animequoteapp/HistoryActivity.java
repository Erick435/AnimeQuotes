package com.erick.animequoteapp;

import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private List<Quote> quoteHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Get quote history from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AnimeQuoteApp", Context.MODE_PRIVATE);

        Set<String> historySet = sharedPreferences.getStringSet("quoteHistory", new HashSet<>());

        // Convert Set to List
        List<String> historyStringList = new ArrayList<>(historySet);
        quoteHistory = new ArrayList<>();
        for (String quoteString : historyStringList) {
            quoteHistory.add(new Quote(quoteString));
        }

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));  // Add this line

        // Set the LayoutManager here after historyRecyclerView has been initialized
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the list and set the adapter to the RecyclerView
        historyAdapter = new HistoryAdapter(quoteHistory);
        historyRecyclerView.setAdapter(historyAdapter);
        historyRecyclerView.setHasFixedSize(true);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // Delete button
    public void deleteQuote(int position) {
        // Remove the quote from your local list
        Quote quoteToDelete = quoteHistory.remove(position);

        // Update your SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AnimeQuoteApp", Context.MODE_PRIVATE);
        Set<String> historySet = sharedPreferences.getStringSet("quoteHistory", new HashSet<>());

        // Convert Quote object to string and remove it from the set
        historySet.remove(quoteToDelete.toString());

        // Update the SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("quoteHistory", historySet);
        editor.apply();

        // Notify the adapter about the removed item
        historyAdapter.notifyItemRemoved(position);
    }

    // This is the HistoryAdapter class
    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

        private List<Quote> quoteList;

        public HistoryAdapter(List<Quote> quoteList) {
            this.quoteList = quoteList;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_item, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            Quote quote = quoteList.get(position);
            holder.quoteTextView.setText(quote.getQuote());

            if (quote.isFavorite()) {
                holder.favoriteImageView.setImageResource(R.drawable.star_outline);
                holder.favoriteImageView.setVisibility(View.VISIBLE);
            } else {
                holder.favoriteImageView.setVisibility(View.GONE);
            }

            holder.menuImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(holder.menuImageView.getContext(), holder.menuImageView);
                    popup.inflate(R.menu.quote_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.action_delete) {
                                // Call deleteQuote method from your activity
                                deleteQuote(holder.getAdapterPosition());
                                return true;
                            } else if (id == R.id.action_favorite) {
                                // Handle favoriting the item
                                return true;
                            }
                            return false;
                        }

                    });
                    popup.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return quoteList.size();
        }

        public class HistoryViewHolder extends RecyclerView.ViewHolder {
            TextView quoteTextView;
            ImageView favoriteImageView;
            ImageView menuImageView;

            public HistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                quoteTextView = itemView.findViewById(R.id.quoteTextView);
                favoriteImageView = itemView.findViewById(R.id.favoriteImageView);
                menuImageView = itemView.findViewById(R.id.menuImageView);

            }
        }
    }
}
