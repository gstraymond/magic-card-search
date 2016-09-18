package fr.gstraymond.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeywordActions {

    public static List<String> ALL = new ArrayList<>();

    // http://mtgsalvation.gamepedia.com/Keyword_action
    static {
        Collections.addAll(ALL,
                "Activate",
                "Attach",
                "Cast",
                "Counter",
                "Destroy",
                "Discard",
                "Exchange",
                "Exile",
                "Fight",
                "Play",
                "Regenerate",
                "Reveal",
                "Sacrifice",
                "Search",
                "Shuffle",
                "Untap",
                "Tap",
                "Scry",
                "Fateseal",
                "Clash",
                "Planeswalk",
                "Set in Motion",
                "Abandon",
                "Proliferate",
                "Transform",
                "Detain",
                "Populate",
                "Monstrosity",
                "Vote",
                "Bolster",
                "Manifest",
                "Support",
                "Investigate",
                "Meld",
                "Goad");
    }
}
