package com.jskaleel.sangaelakkiyangal.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jskaleel.sangaelakkiyangal.R
import com.jskaleel.sangaelakkiyangal.listeners.CategoryItemClickListener
import com.jskaleel.sangaelakkiyangal.model.ResponseModel
import com.jskaleel.sangaelakkiyangal.model.SEAppUtil
import com.jskaleel.sangaelakkiyangal.ui.activities.CategoryDetailsActivity
import com.jskaleel.sangaelakkiyangal.ui.base.CategoryListAdapter
import com.jskaleel.sangaelakkiyangal.utils.DeviceUtils
import com.jskaleel.sangaelakkiyangal.utils.PrintLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_page.view.*


class EttuThogaiFragment : Fragment(), CategoryItemClickListener {

    private lateinit var mContext: Context


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
    }

    override fun onItemClickListener(singleItem: ResponseModel.CategoryItemResponse, itemPosition: Int) {
        PrintLog.debug("Khaleel", "itemPosition - $itemPosition - ${singleItem.title}")
        startActivity(CategoryDetailsActivity.getDetailsIntent(mContext, singleItem))
    }

    lateinit var categoryAdapter: CategoryListAdapter
    private lateinit var layoutView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.fragment_page, container, false)

        categoryAdapter = CategoryListAdapter(mContext, ArrayList(), this)


        layoutView.rvVerticalList.setHasFixedSize(true)
        val numberOfColumns = DeviceUtils.calculateNoOfColumns(mContext)
        val layoutManager = StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL)
        layoutView.rvVerticalList.layoutManager = layoutManager
        layoutView.rvVerticalList.adapter = categoryAdapter
        initApi()
        return layoutView
    }

    private var disposable: Disposable? = null

    private fun initApi() {
        val ettuThogaiCall = SEAppUtil.getRetrofit().getEttuthogai()
        disposable = ettuThogaiCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    categoryAdapter.setItems(result)
                }, {
                    layoutView.rvVerticalList.visibility = View.GONE
                    layoutView.txtError.visibility = View.VISIBLE
                    layoutView.txtError.text = getString(R.string.try_again_later)
                })
    }

    companion object {

        fun newInstance(): EttuThogaiFragment {
            return EttuThogaiFragment()
        }
    }
}