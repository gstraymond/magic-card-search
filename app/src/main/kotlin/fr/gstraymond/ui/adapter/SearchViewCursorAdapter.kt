package fr.gstraymond.ui.adapter

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.widget.CursorAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.biz.SetImageGetter
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.tools.CardColorUtil
import fr.gstraymond.utils.inflate
import java.util.*

class SearchViewCursorAdapter private constructor(context: Context,
                                                  cursor: Cursor,
                                                  flags: Int) : CursorAdapter(context, cursor, flags) {

    private val setImageGetter = SetImageGetter(context)

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        when (getType(cursor)) {
            FIELD_TYPE_EDITION -> {
                view = context.inflate(R.layout.searchview_adapter_edition, parent)
                holder = EditionViewHolder(
                        view.findViewById(R.id.searchview_edition_text) as TextView,
                        view.findViewById(R.id.searchview_edition_image) as ImageView)
            }
            FIELD_TYPE_CARD -> {
                view = context.inflate(R.layout.searchview_adapter_card, parent)
                holder = CardViewHolder(
                        view.findViewById(R.id.searchview_card_title) as TextView,
                        view.findViewById(R.id.searchview_card_type) as TextView)
            }
            else -> {
                view = context.inflate(R.layout.searchview_adapter_token, parent)
                holder = TokenViewHolder(view as TextView)
            }
        }

        return view.apply { tag = holder }
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val holder = view.tag as ViewHolder
        val text = cursor.getString(cursor.getColumnIndex(FIELD_TEXT))

        when (holder) {
            is EditionViewHolder -> {
                holder.textView.text = text
                val drawable = setImageGetter.getDrawable(getEditionCode(cursor))
                holder.imageView.setImageDrawable(drawable)
            }

            is CardViewHolder -> {
                val colorId = ResourcesCompat.getColor(context.resources, getCardColorId(cursor), null)
                holder.textViewTitle.setTextColor(colorId)
                holder.textViewTitle.text = text
                holder.textViewType.text = getCardType(cursor)
            }

            is TokenViewHolder -> {
                holder.textView.text = text
            }
        }
    }

    interface ViewHolder

    class TokenViewHolder(var textView: TextView) : ViewHolder

    class EditionViewHolder(var textView: TextView,
                            var imageView: ImageView) : ViewHolder

    class CardViewHolder(var textViewTitle: TextView,
                         var textViewType: TextView) : ViewHolder

    override fun getItemViewType(position: Int): Int {
        val cursor = getItem(position) as Cursor
        return getType(cursor)
    }

    override fun getViewTypeCount() = 3

    fun changeCursor(data: List<Option>) {
        super.changeCursor(convert(data))
    }

    private fun getEditionCode(cursor: Cursor) =
            cursor.getString(cursor.getColumnIndex(FIELD_EDITION_CODE))

    private fun getType(cursor: Cursor) =
            cursor.getInt(cursor.getColumnIndex(FIELD_TYPE))

    private fun getCardColorId(cursor: Cursor) =
            cursor.getInt(cursor.getColumnIndex(FIELD_CARD_COLOR_ID))

    private fun getCardType(cursor: Cursor) =
            cursor.getString(cursor.getColumnIndex(FIELD_CARD_TYPE))

    companion object {

        private val FIELD_TYPE = "type"
        private val FIELD_TEXT = "text"
        private val FIELD_EDITION_CODE = "editionCode"
        private val FIELD_CARD_COLOR_ID = "cardColorId"
        private val FIELD_CARD_TYPE = "cardType"

        private val FIELD_TYPE_TOKEN = 0
        private val FIELD_TYPE_EDITION = 1
        private val FIELD_TYPE_CARD = 2

        fun empty(context: Context): SearchViewCursorAdapter {
            return SearchViewCursorAdapter(context, convert(ArrayList<Option>()), 0)
        }

        private fun convert(data: List<Option>): Cursor {
            val cursor = MatrixCursor(
                    arrayOf("_id",
                            FIELD_TYPE,
                            FIELD_TEXT,
                            FIELD_EDITION_CODE,
                            FIELD_CARD_COLOR_ID,
                            FIELD_CARD_TYPE
                    )
            )

            data.forEachIndexed { i, option ->
                var type = FIELD_TYPE_TOKEN
                var editionCode: String? = null
                var cardColorId: Int? = null
                var cardType: String? = null

                option.payload?.apply {
                    if (stdEditionCode != null) {
                        type = FIELD_TYPE_EDITION
                        editionCode = stdEditionCode
                    } else {
                        type = FIELD_TYPE_CARD
                        cardColorId = CardColorUtil.getColorId(colors, this.type)
                        cardType = this.type
                    }
                }
                cursor.addRow(
                        arrayOf(i,
                                type,
                                option.text,
                                editionCode,
                                cardColorId,
                                cardType
                        )
                )
            }
            return cursor
        }
    }
}
