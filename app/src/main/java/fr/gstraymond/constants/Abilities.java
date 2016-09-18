package fr.gstraymond.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Abilities {

    public static List<String> ALL = new ArrayList<>();

    // http://mtgsalvation.gamepedia.com/Ability_word
    static {
        Collections.addAll(ALL,
                "Battalion",
                "Bloodrush",
                "Channel",
                "Chroma",
                "Cohort",
                "Constellation",
                "Converge",
                "Delirium",
                "Domain",
                "Fateful hour",
                "Ferocious",
                "Formidable",
                "Grandeur",
                "Hellbent",
                "Heroic",
                "Imprint",
                "Inspired",
                "Join forces",
                "Kinship",
                "Landfall",
                "Lieutenant",
                "Metalcraft",
                "Morbid",
                "Parley",
                "Radiance",
                "Raid",
                "Rally",
                "Spell mastery",
                "Strive",
                "Sweep",
                "Tempting offer",
                "Threshold",
                "Will of the council"
        );
    }
}
