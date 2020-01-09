package com.dfedonnikov.rates.ui

import com.dfedonnikov.rates.domain.RatesState
import com.dfedonnikov.rates.domain.RatesInteractor
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class RatesPresenter @Inject constructor(private val interactor: RatesInteractor) : MvpBasePresenter<RatesView>() {

    private val compositeDisposable = CompositeDisposable()

    fun init() {
        val disposable = interactor.getRates().renderRates()
        compositeDisposable.add(disposable)
    }


    private fun Observable<RatesState>.renderRates(): Disposable = map { build(it) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            { items -> ifViewAttached { it.renderRates(items) } },
            { ifViewAttached { it.showError("Could not load rates") } })

    private fun build(state: RatesState): List<RateItem> {
        return state.getRates().map {
            RateItem(
                title = it.currency.title,
                subtitle = it.currency.subtitle,
                amount = it.amount.toPlainString(),
                icon = it.currency.icon
            )
        }
    }


    override fun destroy() {
        super.destroy()
        interactor.destroy()
        compositeDisposable.clear()
    }

    fun onItemClicked(item: RateItem) = interactor.changeBase(item)


    fun onInputChanged(item: RateItem) = interactor.recalculatedRates(item.amount)

}