package fr.gstraymond.android;

import android.os.Bundle;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.magic.card.search.commons.json.MapperUtil;

import java.io.InputStream;

import fr.gstraymond.R;
import fr.gstraymond.models.HelpText;
import fr.gstraymond.tools.HelpFormatter;

public class HelpActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        HelpFormatter formatter = new HelpFormatter();

        Spanned text = formatter.format(getHelpText(), this);

        TextView view = getTextView();
        view.setText(text);
        // rends les liens cliquables + scroll
        view.setMovementMethod(LinkMovementMethod.getInstance());

        actionBarSetDisplayHomeAsUpEnabled(true);
    }

    private HelpText getHelpText() {
        InputStream stream = getResources().openRawResource(R.raw.help);
        return MapperUtil.fromType(getObjectMapper(), HelpText.class).read(stream);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private TextView getTextView() {
        return (TextView) findViewById(R.id.help_text_view);
    }
}
