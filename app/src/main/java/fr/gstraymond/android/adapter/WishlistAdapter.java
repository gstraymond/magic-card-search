package fr.gstraymond.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.gstraymond.R;
import fr.gstraymond.db.json.JsonList;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.ui.adapter.CardViews;
import fr.gstraymond.ui.view.impl.FavoriteView;

public class WishlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private CardViews cardViews;
    private JsonList wishlist;
    private ClickCallbacks clickCallbacks;

    public WishlistAdapter(Context context, JsonList wishlist, ClickCallbacks clickCallbacks) {
        this.cardViews = new CardViews(context, wishlist, new FavoriteViewClickCallbacks());
        this.wishlist = wishlist;
        this.clickCallbacks = clickCallbacks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.array_adapter_card, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Card card = wishlist.getCards().get(position);
        cardViews.display(holder.itemView, card, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCallbacks.cardClicked(card);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlist.getCards().size();
    }

    private class FavoriteViewClickCallbacks implements FavoriteView.ClickCallbacks {

        @Override
        public void itemAdded(int position) {
        }

        @Override
        public void itemRemoved(int position) {
            notifyItemRemoved(position);
            int total = wishlist.getCards().size();
            if (position < total) {
                notifyItemRangeChanged(position, total - position);
            }
            if (total == 0) {
                clickCallbacks.onEmptyList();
            }
        }
    }

    public interface ClickCallbacks {
        void onEmptyList();
        void cardClicked(Card card);
    }
}
