package com.dfedonnikov.rates.ui

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dfedonnikov.rates.App
import com.dfedonnikov.rates.R
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : MvpActivity<RatesView, RatesPresenter>(), RatesView {

    @Inject
    lateinit var ratesPresenter: RatesPresenter

    private lateinit var adapter: RatesAdapter

    override fun createPresenter(): RatesPresenter = ratesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).component.inject(this)
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

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
