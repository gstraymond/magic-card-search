package fr.gstraymond.tools;


import java.util.List;

import fr.gstraymond.R;

public class CardColorUtil {
    
    public static int getColorId(List<String> colors, String type) {
        int color = android.R.color.white;
        if (colors != null) {
            if (colors.contains("Gold")) color = R.color.gold;
            else if (colors.contains("White")) color = R.color.white;
            else if (colors.contains("Red")) color = R.color.red;
            else if (colors.contains("Green")) color = R.color.green;
            else if (colors.contains("Black")) color = R.color.black;
            else if (colors.contains("Blue")) color = R.color.blue;
            else if (colors.contains("Uncolored") && type.contains("Artifact"))
                color = R.color.uncolored;
        }
        return color;
    }
}
