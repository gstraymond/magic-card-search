package fr.gstraymond.autocomplete.response;

import java.util.List;

public class Payload {

    private String stdEditionCode;
    private List<String> colors;
    private String type;

    public String getStdEditionCode() {
        return stdEditionCode;
    }

    public void setStdEditionCode(String stdEditionCode) {
        this.stdEditionCode = stdEditionCode;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
