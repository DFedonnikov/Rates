package com.dfedonnikov.rates.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
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
            clickListener = {
                presenter.onItemClicked(it)
                showKeyboard()
            },
            inputChangeListener = { presenter.onInputChanged(it) })
        ratesList.adapter = adapter
        ratesList.layoutManager = LinearLayoutManager(this)
        ratesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState != SCROLL_STATE_IDLE) {
                    (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.let {
                        val view = currentFocus ?: View(this@MainActivity)
                        it.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
                if (!recyclerView.canScrollVertically(-1)) {
                    showKeyboard()
                }
            }
        })
        refresh.setOnRefreshListener {
            presenter.refresh()
            refresh.isRefreshing = false
        }
    }

    private fun showKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { manager ->
            ratesList.getChildAt(0)?.let {
                manager.showSoftInput(currentFocus ?: View(this@MainActivity), InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun renderRates(list: List<RateItem>) {
        adapter.submitList(list)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}