package app.base.mvvm.vm

import androidx.fragment.app.FragmentStatePagerAdapter
import app.base.mvvm.repository.IRepository
import app.base.mvvm.view.IView

abstract class PagerVM<Rep : IRepository, V: IView>(repository:Rep,view:V,val pagerAdapter: FragmentStatePagerAdapter):BaseVM<Rep,V>(repository,view)