package fr.gstraymond.android.tools.amazon;

import android.content.Context;

import com.amazon.device.associates.AssociatesAPI;
import com.amazon.device.associates.LinkService;
import com.amazon.device.associates.NotInitializedException;
import com.amazon.device.associates.OpenSearchPageRequest;

import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.tools.Log;

public class AmazonUtils {

    private static final String SEARCH_KEYWORD = "mtg ";
    private static final String AMAZON_KEY = "77efbb6760054935b8969a20c12be781";

    public static void initAmazonApi(Context context) {
        AssociatesAPI.initialize(new AssociatesAPI.Config(AMAZON_KEY, context));
    }

    public static void openSearch(Context context, Card card) {
        OpenSearchPageRequest request = new OpenSearchPageRequest(formatBuyTitle(context, card));
        try {
            LinkService linkService = AssociatesAPI.getLinkService();
            linkService.openRetailPage(request);
        } catch (NotInitializedException e) {
            Log.error("Amazon error", e, AmazonUtils.class);
        }
    }

    public static String formatBuyTitle(Context context, Card card) {
        // FIXME : doesn't work Amazon is always in english
//		if (LanguageUtil.showFrench(context) && card.getFrenchTitle() != null) {
//			return SEARCH_KEYWORD + card.getFrenchTitle();
//		}

        return SEARCH_KEYWORD + card.getTitle();
    }
}
