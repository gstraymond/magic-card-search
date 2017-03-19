package fr.gstraymond.biz;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.ExpandableListView;

import com.magic.card.search.commons.json.MapperUtil;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;

import fr.gstraymond.R;
import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.models.search.response.Card;
import fr.gstraymond.models.search.response.Hit;
import fr.gstraymond.models.search.response.SearchResult;
import fr.gstraymond.ui.FacetOnChildClickListener;
import fr.gstraymond.ui.adapter.FacetListAdapter;
import fr.gstraymond.utils.AndroidUtilsKt;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

public class UIUpdater extends AsyncTask<Void, Void, SearchResult> {

    private CardListActivity activity;
    private String resultAsString;
    private MapperUtil<SearchResult> mapperUtil;

    public UIUpdater(CardListActivity activity, String resultAsString, Moshi objectMapper) {
        this(activity);
        this.resultAsString = resultAsString;
        this.mapperUtil = MapperUtil.fromType(objectMapper, SearchResult.class);
    }

    UIUpdater(CardListActivity activity) {
        this.activity = activity;
    }

    @Override
    protected SearchResult doInBackground(Void... params) {
        return mapperUtil.read(resultAsString);
    }

    @Override
    protected void onPostExecute(SearchResult result) {
        if (result == null) {
            showText(activity.getString(R.string.failed_search));
            return;
        }

        ArrayList<Card> cards = new ArrayList<>();
        int totalCardCount = result.getHits().getTotal();
        for (Hit hit : result.getHits().getHits()) {
            cards.add(hit.get_source());
        }

        int textId = R.string.progress_cards_found;
        if (totalCardCount <= 1) {
            textId = R.string.progress_card_found;
        }

        updateUIList(totalCardCount, cards);
        updateUIFacets(result);

        String text = String.format("%s/%s %s", activity.adapter.getItemCount(), totalCardCount, activity.getString(textId));
        showText(text);
    }

    private void showText(String message) {
        if (activity.getLoadingSnackbar() != null) {
            activity.getLoadingSnackbar().dismiss();
        }
        Snackbar snackbar = Snackbar.make(AndroidUtilsKt.rootView(activity), message, LENGTH_LONG);
        activity.setLoadingSnackbar(snackbar);
        snackbar.show();
    }

    private void updateUIList(int totalCardCount, ArrayList<Card> cards) {
        if (!activity.isFinishing()) {
            if (getOptions().getAppend()) {
                activity.getAdapter().appendCards(cards);
            } else {
                activity.getAdapter().setCards(cards);

            }
            activity.setTotalCardCount(totalCardCount);
        }
    }


    private void updateUIFacets(SearchResult result) {
        if (!getOptions().getAppend()) {
            FacetListAdapter adapter = new FacetListAdapter(result.getFacets(), getOptions(), activity);
            getFacetListView().setAdapter(adapter);

            FacetOnChildClickListener listener = new FacetOnChildClickListener(adapter, getOptions(), activity);
            getFacetListView().setOnChildClickListener(listener);
        }
    }

    private SearchOptions getOptions() {
        return activity.getCurrentSearch();
    }

    private ExpandableListView getFacetListView() {
        return (ExpandableListView) activity.findViewById(R.id.left_drawer);
    }
}
