package com.dfedonnikov.rates.domain

import com.dfedonnikov.rates.data.RatesRepository
import com.dfedonnikov.rates.ui.RateItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface RatesInteractor {

    fun startRatesUpdating()
    fun stopRatesUpdating()
    fun refresh()
    fun getLatestRatesState(): Observable<RatesState>
    fun changeBase(item: RateItem)
    fun recalculateRates(amount: String)
    fun destroy()
}

class RatesInteractorImpl @Inject constructor(private val repository: RatesRepository) : RatesInteractor {

    private val compositeDisposable = CompositeDisposable()
    private var updateDisposable = Disposables.disposed()


    override fun startRatesUpdating() {
        updateDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .startWith(1)
            .flatMapCompletable {
                val base = when (val state = repository.getState()) {
                    null -> Currency.EUR.title
                    else -> state.currentCurrency.title
                }
                updateRates(base)
            }
            .subscribeOn(Schedulers.io())
            .subscribe({}, {})
        compositeDisposable.add(updateDisposable)
    }

    override fun stopRatesUpdating() = updateDisposable.dispose()

    override fun refresh() {
        if (!updateDisposable.isDisposed) {
            updateDisposable.dispose()
        }
        repository.refresh()
        startRatesUpdating()
    }

    private fun updateRates(base: String): Completable {
        val state = repository.getState()
        return when {
            state == null || state.currentAmount != BigDecimal.ZERO -> repository.updateRates(base)
                .flatMapCompletable {
                    when (state) {
                        null -> initState(it)
                        else -> updateState(it, state)
                    }
                    Completable.complete()
                }
            else -> Completable.never()
        }
    }

    private fun initState(data: RatesData) {
        val list = data.currencies.mapTo(LinkedList()) { RateStateItem(it.key, BigDecimal.ZERO, it.value) }
        list.addFirst(RateStateItem(data.base, BigDecimal.ZERO, BigDecimal.ONE))
        val ratesState = RatesState(data.base, BigDecimal.ZERO, list)
        repository.updateState(ratesState)
    }

    private fun updateState(data: RatesData, state: RatesState) {
        val list = state.getRates().mapIndexedTo(LinkedList()) { index, it ->
            when (index) {
                0 -> it.copy(ratioToBase = BigDecimal.ONE)
                else -> {
                    val factor = data.currencies[it.currency] ?: return
                    val amount =
                        when (val amount = state.currentAmount) {
                            BigDecimal.ZERO -> amount
                            else -> amount.multiply(factor).setScale(2, RoundingMode.HALF_UP)
                        }
                    RateStateItem(it.currency, amount, factor)
                }
            }
        }
        val ratesState = RatesState(data.base, state.currentAmount, list)
        repository.updateState(ratesState)
    }

    override fun getLatestRatesState(): Observable<RatesState> = repository.getLastRatesState()

    override fun changeBase(item: RateItem) {
        val disposable = repository.updateRates(item.title)
            .subscribeOn(Schedulers.io())
            .subscribe({ data ->
                val state = repository.getState() ?: return@subscribe
                val list = state.getRates()
                val currentCurrency = Currency.valueOf(item.title)
                val element = list.find { it.currency == currentCurrency }
                list.remove(element)
                list.addFirst(element)
                val currentAmount = try {
                    item.amount.toBigDecimal()
                } catch (e: Exception) {
                    BigDecimal.ZERO
                }
                val ratesState = state.copy(
                    currentCurrency = currentCurrency,
                    currentAmount = currentAmount,
                    rates = list
                )
                updateState(data, ratesState)
            }, {})
        compositeDisposable.add(disposable)
    }

    override fun recalculateRates(amount: String) {
        val currentAmount = try {
            BigDecimal(amount)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
        val state = repository.getState() ?: return
        val list = state.getRates()
            .mapTo(LinkedList()) {
                val newAmount = when (currentAmount) {
                    BigDecimal.ZERO -> currentAmount
                    else -> currentAmount.multiply(it.ratioToBase).setScale(2, RoundingMode.HALF_UP)
                }
                RateStateItem(it.currency, newAmount, it.ratioToBase)
            }
        val ratesState = state.copy(currentAmount = currentAmount, rates = list)
        repository.updateState(ratesState)
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}