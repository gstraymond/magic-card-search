package fr.gstraymond.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import fr.gstraymond.R;
import fr.gstraymond.db.json.JsonList;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.ui.CastingCostAssetLoader;
import fr.gstraymond.ui.view.impl.FavoriteView;

public class CardArrayAdapter extends ArrayAdapter<Card> {

    private CardViews cardViews;

    public CardArrayAdapter(Context context, int resource,
                            int textViewResourceId, List<Card> objects,
                            CastingCostAssetLoader castingCostAssetLoader, JsonList wishlist) {
        super(context, resource, textViewResourceId, objects);
        cardViews = new CardViews(context, castingCostAssetLoader, wishlist, new FavoriteViewClickCallbacks());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.array_adapter_card, null);
        }

        cardViews.display(view, getItem(position), position);
        return view;
    }

    class FavoriteViewClickCallbacks implements FavoriteView.ClickCallbacks {

        @Override
        public void itemAdded(int position) {
            notifyDataSetChanged();
        }

        @Override
        public void itemRemoved(int position) {
            notifyDataSetChanged();
        }
    }
}
