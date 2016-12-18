package fr.gstraymond.db.json;

import fr.gstraymond.models.response.Card;

public class CardWithOccurrence {

    private Card card;
    private int occurrence;
    private boolean isSideboard;

    public CardWithOccurrence() {
    }

    public CardWithOccurrence(Card card, int occurrence, boolean isSideboard) {
        this.card = card;
        this.occurrence = occurrence;
        this.isSideboard = isSideboard;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public boolean isSideboard() {
        return isSideboard;
    }

    public void setSideboard(boolean sideboard) {
        isSideboard = sideboard;
    }
}
