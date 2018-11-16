package app.base.mvvm.vm

import androidx.fragment.app.FragmentStatePagerAdapter
import app.base.mvvm.repository.IRepository
import app.base.mvvm.view.IView

open class PagerVM<Rep : IRepository, V: IView>() :BaseVM<Rep,V>(){

    lateinit var pagerAdapter:  FragmentStatePagerAdapter
    constructor(repository:Rep,view:V, pagerAdapter: FragmentStatePagerAdapter) : this() {
        this.repository = repository
        this.view = view
        this.pagerAdapter = pagerAdapter
    }
}