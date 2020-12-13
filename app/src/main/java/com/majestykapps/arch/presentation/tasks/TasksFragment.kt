package com.majestykapps.arch.presentation.tasks

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.majestykapps.arch.R
import com.majestykapps.arch.util.isNetworkConnected
import com.majestykapps.arch.util.showSnackBarMessage
import com.majestykapps.arch.util.showSnackBarWithRetryButton
import kotlinx.android.synthetic.main.fragment_tasks.*


class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private lateinit var searchView: SearchView
    private val viewModel: TasksViewModel by activityViewModels()
    private var adapter: TaskAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.onBackPressedDispatcher?.addCallback(this) {
            Log.i(TAG, "onCreate: ")
            if (!searchView.isIconified) {
                searchView.onActionViewCollapsed()
            } else {
                activity!!.finish()
            }
        }


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        adapter = TaskAdapter(mutableListOf()) {

            it?.let { taskId ->
                viewModel.onTaskClick(taskId)
            }
        }
        rvTask.adapter = adapter
        context?.let {
            rvTask.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModelObservers()
    }

    private fun initViewModelObservers() {
        viewModel.apply {
            loadingEvent.observe(viewLifecycleOwner, Observer { isRefreshing ->
                Log.d(TAG, "loadingEvent observed")
                swipeRefresh.isRefreshing = isRefreshing
            })

            errorEvent.observe(viewLifecycleOwner, Observer { throwable ->
                Log.e(TAG, "errorEvent observed", throwable)
                view?.showSnackBarWithRetryButton(getString(R.string.not_update_api)) {
                    swipeRefresh.isRefreshing = true
                    viewModel.refresh()
                }
            })

            tasks.observe(viewLifecycleOwner, Observer { tasks ->
                Log.d(TAG, "tasks observed: $tasks")
                context?.let {
                    if (!it.isNetworkConnected()) {
                        view?.showSnackBarMessage(getString(R.string.no_data_available))
                    }
                }
                adapter?.updateList(tasks)
            })
        }
    }

    companion object {
        private const val TAG = "TasksFragment"

        fun newInstance() = TasksFragment()
    }
}