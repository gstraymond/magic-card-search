package fr.gstraymond.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Zones {

    public static List<String> ALL = new ArrayList<>();

    // http://mtgsalvation.gamepedia.com/Zone
    static {
        Collections.addAll(ALL,
                "library",
                "hand",
                "battlefield",
                "graveyard",
                "stack",
                "exile",
                "command"
        );
    }
}
