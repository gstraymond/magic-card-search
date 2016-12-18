package fr.gstraymond.ui.view.impl;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.db.json.JsonList;
import fr.gstraymond.models.search.response.Card;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class FavoriteView extends CommonDisplayableView<AppCompatButton> {

    private JsonList wishlist;
    private ClickCallbacks clickCallbacks;
    private ColorStateList colorEnabled;
    private ColorStateList colorDisabled;

    private Log log = new Log(this);

    public FavoriteView(JsonList wishlist, ClickCallbacks clickCallbacks, Context context) {
        this.wishlist = wishlist;
        this.clickCallbacks = clickCallbacks;
        this.colorEnabled = ResourcesCompat.getColorStateList(context.getResources(), R.color.colorAccent, null);
        this.colorDisabled = ResourcesCompat.getColorStateList(context.getResources(), R.color.colorPrimaryDark, null);
    }

    @Override
    public boolean display(Card card) {
        return true;
    }

    @Override
    public int getId() {
        return R.id.card_favorite;
    }

    @Override
    public void setValue(final Card card, final int position) {
        AppCompatButton button = getView();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wishlist.addOrRemove(card)) {
                    clickCallbacks.itemAdded(position);
                } else {
                    clickCallbacks.itemRemoved(position);
                }
            }
        });

        boolean contains = wishlist.contains(card);
        log.d("contains %s -> %s [%s]", card, contains, button.getClass());
        if (contains) {
            button.setSupportBackgroundTintList(colorEnabled);
        } else {
            button.setSupportBackgroundTintList(colorDisabled);
        }
    }

    public interface ClickCallbacks {
        void itemAdded(int position);

        void itemRemoved(int position);
    }
}
