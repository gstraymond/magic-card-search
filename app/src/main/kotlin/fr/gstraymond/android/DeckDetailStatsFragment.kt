package fr.gstraymond.android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find

class DeckDetailStatsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_deck_detail_stats, container, false)

    override fun onResume() {
        super.onResume()
        updateStats()
    }

    fun updateStats() {
        val deckId = activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA)
        val cardList = activity.app().cardListBuilder.build(deckId.toInt())
        val deckStats = DeckStats(cardList.all())
        activity.find<TextView>(R.id.deck_colors).text = "colors: ${deckStats.colors.joinToString()}"
        activity.find<TextView>(R.id.deck_formats).text = "formats: ${deckStats.format}"
        activity.find<TextView>(R.id.deck_cards).text = "cards: ${deckStats.deckSize}"
        activity.find<TextView>(R.id.deck_sideboard).text = "sidebard: ${deckStats.sideboardSize}"
        activity.find<TextView>(R.id.deck_price).text = "price: ${deckStats.totalPrice}"
        activity.find<TextView>(R.id.mana_curve).text = "mana curve: ${deckStats.manaCurve}"
    }
}