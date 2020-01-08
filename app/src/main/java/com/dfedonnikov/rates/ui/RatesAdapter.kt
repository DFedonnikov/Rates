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

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
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

        fun bind(item: RateItem) {
            isBinding = true
            containerView.tag = item
            rateTitle.text = item.title
            rateSubtitle.text = item.subtitle
            amount.setText(item.amount)
            rateIcon.setImageResource(item.icon)
            isBinding = false
        }

    }
}

class RatesDiffUtil : DiffUtil.ItemCallback<RateItem>() {
    override fun areItemsTheSame(oldItem: RateItem, newItem: RateItem): Boolean = oldItem.title == newItem.title

    override fun areContentsTheSame(oldItem: RateItem, newItem: RateItem): Boolean = oldItem == newItem
}

class DecimalDigitsFilter : InputFilter {


    override fun filter(source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int): CharSequence? {
        return dest?.let {
            return when {
                !pattern.matcher(dest).matches() -> ""
                else -> null
            }
        } ?: ""
    }

    companion object {
        private val pattern = Pattern.compile("[0-9]*((\\.[0-9]?)?)||(\\.)?")
    }
}