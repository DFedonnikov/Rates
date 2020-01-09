package com.dfedonnikov.rates.ui

import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dfedonnikov.rates.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.rate_item.*
import java.util.regex.Pattern

class RatesAdapter(private val clickListener: (item: RateItem) -> Unit,
                   private val inputChangeListener: (item: RateItem) -> Unit) :
    ListAdapter<RateItem, RatesAdapter.RatesViewHolder>(RatesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rate_item, parent, false)
        return RatesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {}

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int, payloads: MutableList<Any>) {
        getItem(position)?.let { holder.bind(it, position, payloads) }
    }

    inner class RatesViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private var isBinding = false

        init {
            containerView.setOnClickListener { view ->
                (view.tag as? RateItem)?.let {
                    amount.requestFocus()
                    clickListener(it)
                }
            }
            amount.filters = arrayOf(DecimalDigitsFilter())
            amount.setOnTouchListener { _, _ ->
                (containerView.tag as? RateItem)?.let { clickListener(it) }
                false
            }
            amount.doAfterTextChanged { text ->
                if (isBinding) return@doAfterTextChanged
                (containerView.tag as? RateItem)?.let {
                    it.amount = text.toString()
                    inputChangeListener(it)
                }
            }
        }

        fun bind(item: RateItem, position: Int, payloads: MutableList<Any>) {
            isBinding = true
            containerView.tag = item
            rateTitle.text = item.title
            rateSubtitle.text = item.subtitle
            val isItemCompletelyChanged = payloads.firstOrNull() as? Boolean ?: true
            if (item.amount != "0" && (position != 0 || isItemCompletelyChanged)) {
                amount.setText(item.amount)
            }
            if (isItemCompletelyChanged) {
                rateIcon.setImageResource(item.icon)
            }
            isBinding = false
        }

    }
}

class RatesDiffUtil : DiffUtil.ItemCallback<RateItem>() {
    override fun areItemsTheSame(oldItem: RateItem, newItem: RateItem): Boolean = oldItem.title == newItem.title

    override fun areContentsTheSame(oldItem: RateItem, newItem: RateItem): Boolean = oldItem == newItem

    override fun getChangePayload(oldItem: RateItem, newItem: RateItem): Any? = oldItem.title != newItem.title
}

class DecimalDigitsFilter : InputFilter {


    override fun filter(source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int): CharSequence? {
        source ?: return null
        dest ?: return null

        val replacement = source.subSequence(start, end).toString()
        val newVal =
            dest.subSequence(0, dstart).toString() + replacement + dest.subSequence(dend, dest.length).toString();
        val matcher = pattern.matcher(newVal);
        if (matcher.matches()) return null

        return when {
            source.isEmpty() -> dest.subSequence(dstart, dend)
            else -> ""
        }
    }


    companion object {
        private val pattern = Pattern.compile("-?[0-9]{0,100}+((\\.[0-9]{0,2})?)||(\\.)?")
    }
}