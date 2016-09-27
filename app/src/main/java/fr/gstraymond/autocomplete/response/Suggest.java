package fr.gstraymond.autocomplete.response;

import java.util.List;

class Suggest {

    private List<Card> card;

    public List<Card> getCard() {
        return card;
    }

    public void setCard(List<Card> card) {
        this.card = card;
    }
}
