package app.base.mvvm.vm

import androidx.fragment.app.FragmentStatePagerAdapter
import app.base.mvvm.repository.IRepository
import app.base.mvvm.view.IView

abstract class PagerVM<Rep : IRepository, V: IView>():BaseVM<Rep,V>(){
    lateinit var pagerAdapter: FragmentStatePagerAdapter;
    fun initialize(repository: Rep, view: V, pagerAdapter: FragmentStatePagerAdapter) {
        super.initialize(repository, view)
        this.pagerAdapter = pagerAdapter
    }
}