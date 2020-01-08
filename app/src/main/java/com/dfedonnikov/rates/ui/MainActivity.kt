package com.dfedonnikov.rates.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dfedonnikov.rates.R
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MvpActivity<RatesView, RatesPresenter>(), RatesView {

    private lateinit var adapter: RatesAdapter

    override fun createPresenter(): RatesPresenter {
        return RatesPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRatesList()
        presenter.init()
    }

    private fun initRatesList() {
        adapter = RatesAdapter(
            clickListener = { presenter.onItemClicked(it) },
            inputChangeListener = { presenter.onInputChanged(it) })
        ratesList.adapter = adapter
        ratesList.layoutManager = LinearLayoutManager(this)
    }

    override fun renderRates(list: List<RateItem>) {
        adapter.submitList(list)
    }
}
