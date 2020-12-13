package com.majestykapps.arch.presentation.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.majestykapps.arch.R
import com.majestykapps.arch.domain.entity.Task
import kotlinx.android.synthetic.main.item_task.view.*
import java.util.*

class TaskAdapter(private var list: MutableList<Task>,private val itemClick:(taskId:String?)->Unit) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>(),
    Filterable {
    var countryFilterList = mutableListOf<Task>()

    init {
        countryFilterList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_task, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(countryFilterList[position])
    }

    override fun getItemCount(): Int = countryFilterList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(model: Task) {
            itemView.vTitle.text = model.title
            itemView.vDescription.text = model.description
            itemView.setOnClickListener {
                itemClick.invoke(model.id)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                countryFilterList = if (charSearch.isEmpty()) {
                    list
                } else {
                    val resultList = mutableListOf<Task>()
                    for (row in list) {
                        if (row.title.toLowerCase()
                                .contains(constraint.toString().toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countryFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as MutableList<Task>
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(tasks: List<Task>) {
        list.clear()
        list.addAll(tasks)
        notifyDataSetChanged()
    }
}
