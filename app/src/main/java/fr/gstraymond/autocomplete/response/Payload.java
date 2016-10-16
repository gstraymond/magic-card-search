package fr.gstraymond.autocomplete.response;

public class Payload {

    private String stdEditionCode;

    public String getStdEditionCode() {
        return stdEditionCode;
    }

    public void setStdEditionCode(String stdEditionCode) {
        this.stdEditionCode = stdEditionCode;
    }

    @Override
    public String toString() {
        return stdEditionCode;
    }
}
