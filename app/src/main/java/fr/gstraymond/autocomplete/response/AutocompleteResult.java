package fr.gstraymond.autocomplete.response;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteResult {

    private Suggest suggest;

    public AutocompleteResult() {
        this.suggest = new Suggest();
        this.suggest.setCard(new ArrayList<Card>());
    }

    public void setSuggest(Suggest suggest) {
        this.suggest = suggest;
    }

    public List<String> getResults() {
        List<String> results = new ArrayList<>();
        for (Card card : suggest.getCard()) {
            for (Option option : card.getOptions()) {
                results.add(option.getText());
            }
        }
        return results;
    }
}
