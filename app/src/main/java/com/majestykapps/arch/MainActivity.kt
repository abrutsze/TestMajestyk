package com.majestykapps.arch

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.majestykapps.arch.data.repository.TasksRepositoryImpl
import com.majestykapps.arch.data.source.local.TasksLocalDataSource
import com.majestykapps.arch.data.source.local.ToDoDatabase
import com.majestykapps.arch.presentation.common.ViewModelFactory
import com.majestykapps.arch.presentation.taskdetail.TaskDetailViewModel
import com.majestykapps.arch.presentation.taskdetail.TaskFragment
import com.majestykapps.arch.presentation.tasks.TasksFragment
import com.majestykapps.arch.presentation.tasks.TasksViewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var searchView: SearchView
    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var taskDetailViewModel: TaskDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tasksViewModel = initViewModel()
        taskDetailViewModel = initDetailViewModel()
        initViewModelObservers()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, TasksFragment.newInstance())
                .commit()
        }
    }

    private fun initViewModel(): TasksViewModel {
        val tasksDao = ToDoDatabase.getInstance(applicationContext).taskDao()
        val localDataSource = TasksLocalDataSource.getInstance(tasksDao)
        val tasksRepository = TasksRepositoryImpl.getInstance(localDataSource)
        val factory = ViewModelFactory.getInstance(tasksRepository)
        return ViewModelProviders.of(this, factory).get(TasksViewModel::class.java)
    }

    private fun initDetailViewModel(): TaskDetailViewModel {
        val tasksDao = ToDoDatabase.getInstance(applicationContext).taskDao()
        val localDataSource = TasksLocalDataSource.getInstance(tasksDao)
        val tasksRepository = TasksRepositoryImpl.getInstance(localDataSource)
        val factory = ViewModelFactory.getInstance(tasksRepository)
        return ViewModelProviders.of(this, factory).get(TaskDetailViewModel::class.java)
    }

    private fun initViewModelObservers() {
        tasksViewModel.apply {
            launchEvent.observe(this@MainActivity, Observer { id ->
                Log.d(TAG, "launchTask: launching task with id = $id")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContent, TaskFragment.newInstance(id)).commit()
            })
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
