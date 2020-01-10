package com.dfedonnikov.rates.ui

import com.dfedonnikov.rates.domain.RatesState
import com.dfedonnikov.rates.domain.RatesInteractor
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import java.math.BigDecimal
import javax.inject.Inject

class RatesPresenter @Inject constructor(private val interactor: RatesInteractor) : MvpBasePresenter<RatesView>() {

    private val compositeDisposable = CompositeDisposable()
    private var updateDisposable = Disposables.disposed()

    fun init() {
        updateDisposable = interactor.getLatestRatesState().renderRates()
        compositeDisposable.add(updateDisposable)
    }


    private fun Observable<RatesState>.renderRates(): Disposable = map { build(it) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            { items -> ifViewAttached { it.renderRates(items) } },
            { ifViewAttached { it.showError("Could not load rates. Pull to refresh") } })

    private fun build(state: RatesState): List<RateItem> {
        return state.getRates().map { stateItem ->
            RateItem(
                title = stateItem.currency.title,
                subtitle = stateItem.currency.subtitle,
                amount = stateItem.amount.takeIf { it != BigDecimal.ZERO }?.toPlainString() ?: "",
                icon = stateItem.currency.icon
            )
        }
    }

    fun refresh() {
        if (!updateDisposable.isDisposed) {
            updateDisposable.dispose()
        }
        interactor.refresh()
        init()
    }

    fun onResume() = interactor.startRatesUpdating()

    fun onPause() = interactor.stopRatesUpdating()

    override fun destroy() {
        super.destroy()
        interactor.destroy()
        compositeDisposable.clear()
    }

    fun onItemClicked(item: RateItem) = interactor.changeBase(item)


    fun onInputChanged(item: RateItem) = interactor.recalculateRates(item.amount)

}