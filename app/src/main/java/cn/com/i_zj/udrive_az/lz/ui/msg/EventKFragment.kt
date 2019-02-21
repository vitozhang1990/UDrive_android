package cn.com.i_zj.udrive_az.lz.ui.msg

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.com.i_zj.udrive_az.DBSBaseFragment
import cn.com.i_zj.udrive_az.R
import cn.com.i_zj.udrive_az.lz.adapter.EventListAdapter
import cn.com.i_zj.udrive_az.model.ActivityInfo
import cn.com.i_zj.udrive_az.model.ret.RetEventObj
import cn.com.i_zj.udrive_az.network.UObserver
import cn.com.i_zj.udrive_az.network.UdriveRestClient
import cn.com.i_zj.udrive_az.utils.StringUtils
import cn.com.i_zj.udrive_az.utils.ToolsUtils
import cn.com.i_zj.udrive_az.view.EmptyView
import cn.com.i_zj.udrive_az.web.WebActivity
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * @author JayQiu
 * @create 2018/11/15
 * @Describe
 */
class EventKFragment : DBSBaseFragment() {
    val instance by lazy { this }
    lateinit var eventListAdapter: EventListAdapter
    var page: Int = 1
    lateinit var emptyView: EmptyView
    lateinit var list: ArrayList<ActivityInfo>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.layoutManager = LinearLayoutManager(context)
        list = ArrayList()
        emptyView = EmptyView(recycler.context, recycler)
        eventListAdapter = EventListAdapter(R.layout.item_event_list, null, this@EventKFragment)
        recycler.adapter = eventListAdapter
        eventListAdapter.bindToRecyclerView(recycler)
        swipeRefresh.isEnableLoadmore = true

        swipeRefresh.setOnRefreshListener {
            page = 1
            initData()
        }
        swipeRefresh.setOnLoadmoreListener {
            page++
            initData()
        }
        eventListAdapter.setOnItemClickListener { adapter, view, position ->
            val activityInfo = list[position]
            if (activityInfo != null) {
                WebActivity.startWebActivity(this@EventKFragment.context, activityInfo.href)
            }
        }
        swipeRefresh.autoRefresh()
    }

    private fun initData() {
        UdriveRestClient.getClentInstance().activityPage(page, 10)
                .subscribeOn(Schedulers.io())
                .compose(this@EventKFragment.bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : UObserver<RetEventObj>() {
                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                    }
                    override fun onSuccess(response: RetEventObj?) {
                        if (response != null) {
                            if (page == 1) {
                                if (!StringUtils.isEmpty(response.list)) {
                                    list.clear()
                                    list.addAll(response.list)
                                } else {

                                    emptyView.setImage(R.mipmap.pic_activity_null)
                                    emptyView.setMsg("暂无活动哦~")
                                    eventListAdapter.emptyView = emptyView
                                }
                                eventListAdapter.replaceData(response.list)
                            } else {
                                if (!StringUtils.isEmpty(response.list)) {
                                    list.addAll(response.list)
                                }
                                eventListAdapter.addData(response.list)
                            }
                            swipeRefresh.isEnableLoadmore = response.isHasNextPage
                        }
                    }

                    override fun onException(code: Int, message: String?) {

                        if (page == 1) {
                            emptyView.setImage(R.mipmap.pic_activity_null)
                            emptyView.setMsg("暂无活动哦~")
                            eventListAdapter.emptyView = emptyView
                        } else {
                            showToast(message)
                        }
                    }

                    override fun onFinish() {
                        if (page == 1) {
                            swipeRefresh.finishRefresh()
                        } else {
                            swipeRefresh.finishLoadmore()
                        }
                    }
                })
    }
}