package fr.gstraymond.ui.view.impl;

import android.widget.TextView;

import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.models.response.Card;
import fr.gstraymond.models.response.Publication;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class CostView extends CommonDisplayableView<TextView> {

    private static double max$ = 1d;
    private static double max$$ = 10d;

    private Log log = new Log(this);

    @Override
    public boolean display(Card card) {
        return true;
    }

    @Override
    public int getId() {
        return R.id.array_adapter_card_price;
    }

    @Override
    public void setValue(Card card, int position) {
        int count$ = 0;
        int count$$ = 0;
        int count$$$ = 0;
        for (Publication publication : card.getPublications()) {
            if (publication.getPrice() > max$$) count$$$++;
            else if (publication.getPrice() > max$) count$$++;
            else if (publication.getPrice() > 0d) count$++;
        }

        String $$$ = "?";
        if (count$ > 0) $$$ = "$";
        else if (count$$ > 0) $$$ = "$$";
        else if (count$$$ > 0) $$$ = "$$$";

        log.d("$ %s $$ %s $$$ %s -> %s", count$, count$$, count$$$, $$$);

        if ($$$.equals("?")) {
            display(false);
        } else {
            display(true);
            getView().setText($$$);
        }
    }
}
