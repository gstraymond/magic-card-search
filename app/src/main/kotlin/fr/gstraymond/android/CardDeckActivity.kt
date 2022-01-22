package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.magic.card.search.commons.json.MapperUtil
import fr.gstraymond.R
import fr.gstraymond.android.adapter.CardDeckAdapter
import fr.gstraymond.models.Deck
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.utils.*

class CardDeckActivity : CardCommonActivity(R.layout.activity_card_deck) {

    companion object {
        fun getIntent(context: Context, card: Card) =
                Intent(context, CardDeckActivity::class.java).apply {
                    putExtra(CARD_EXTRA, card)
                }
    }

    private val emptyView by lazy { find<View>(R.id.card_deck_empty_text) }
    private val nonEmptyView by lazy { find<View>(R.id.card_deck_not_empty) }
    private val countTextView by lazy { find<TextView>(R.id.card_deck_count) }

    private val cardDeckAdapter by lazy {
        CardDeckAdapter(this, buildTuples()).apply {
            onClickListener = { deckId ->
                View.OnClickListener {
                    startActivity {
                        DeckDetailActivity.getIntent(this@CardDeckActivity, deckId)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(findViewById(R.id.toolbar))
        true.actionBarSetDisplayHomeAsUpEnabled()

        val title = find<TextView>(R.id.toolbar_title)
        title.text = card.getLocalizedTitle(this, Card::title) { c, ft -> "$ft (${c.title})" }


        find<RecyclerView>(R.id.card_deck_recyclerview).let {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = cardDeckAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        if (cardDeckAdapter.itemCount == 0) {
            emptyView.visible()
            nonEmptyView.gone()
        } else {
            countTextView.text = String.format(getString(R.string.card_deck_count), cardDeckAdapter.itemCount)
            emptyView.gone()
            nonEmptyView.visible()
        }
    }

    private fun buildTuples(): List<Pair<Deck, DeckCard.Counts>> {
        val deckCardMapper = MapperUtil.fromType(app().objectMapper, DeckCard::class.java)
        val stringMapper = MapperUtil.fromType(app().objectMapper, String::class.java)

        val deckFiles = fileList().filter { it.startsWith("deckcard_") }
        val cardTitle = """"title":""" + stringMapper.asJsonString(card.title)
        return deckFiles.map {
            val deck = app().deckList.getByUid(it.replace("deckcard_", ""))
            deck!! to openFileInput(it).bufferedReader().useLines { it1 ->
                it1.filter { it2 -> it2.contains(cardTitle) }.map { it2 ->
                    val deckCard = deckCardMapper.read(it2)
                    deckCard.counts
                }.toList()
            }
        }.filter { it.second.isNotEmpty() }.map { it.first to it.second.first() }
    }
}