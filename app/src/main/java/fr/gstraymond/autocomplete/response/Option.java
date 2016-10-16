package fr.gstraymond.autocomplete.response;

public class Option {
    private String text;

    private Payload payload;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return text + "-[" + payload + "]";
    }
}
