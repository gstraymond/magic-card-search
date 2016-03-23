package com.magic.card.search.browser.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.magic.card.search.browser.R;
import com.magic.card.search.commons.model.MTGCard;

import java.util.ArrayList;
import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private List<MTGCard> cards = new ArrayList<>();
    private CardClickListener cardClickListener;

    public CardsAdapter(final CardClickListener cardClickListener) {
        this.cardClickListener = cardClickListener;
    }

    public interface CardClickListener {
        void onCardClick(MTGCard mtgCard);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descTextView;
        public TextView costTextView;
        public TextView typeTextView;
        public TextView numberTextView;

        public ViewHolder(CardView v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.card_title_text_view);
            descTextView = (TextView) v.findViewById(R.id.card_desc_text_view);
            costTextView = (TextView) v.findViewById(R.id.card_cost_text_view);
            typeTextView = (TextView) v.findViewById(R.id.card_type_text_view);
            numberTextView = (TextView) v.findViewById(R.id.card_number_text_view);
        }

        public void bind(final MTGCard mtgCard, final CardClickListener cardClickListener) {
            titleTextView.setText(mtgCard.title);
            costTextView.setText(mtgCard.castingCost);
            numberTextView.setText(String.format("[%S]", mtgCard.collectorNumber));
            descTextView.setText(mtgCard.description);
            typeTextView.setText(mtgCard.type);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    cardClickListener.onCardClick(mtgCard);
                }
            });
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_text_view, parent, false);
        return new ViewHolder(cardView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(cards.get(position), cardClickListener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(List<MTGCard> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }
}
