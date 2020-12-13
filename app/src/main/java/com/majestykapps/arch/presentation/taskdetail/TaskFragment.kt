package com.majestykapps.arch.presentation.taskdetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.majestykapps.arch.R
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.fragment_task.view.*

class TaskFragment : Fragment(R.layout.fragment_task) {

    private var taskId: String? = null

    private val viewModel: TaskDetailViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskId = it.getString(TASK_ID)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        taskId?.let {
            viewModel.getTask(it)
        }
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
                vErrorMessage.text = throwable.localizedMessage
                // TODO show error
            })

            task.observe(viewLifecycleOwner, Observer { task->
                Log.d(TAG, "task observed: $task")
                vTitle.text = task.title
                vDescription.text = task.description
            })
        }
    }
    companion object {
        const val TASK_ID = "taskId"
        private const val TAG = "TaskFragment"
        @JvmStatic
        fun newInstance(param1: String) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putString(TASK_ID, param1)
                }
            }
    }
}