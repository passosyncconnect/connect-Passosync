package com.pasosync.pasosyncconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.QueryData
import com.pasosync.pasosyncconnect.data.QueryDataRV
import com.pasosync.pasosyncconnect.data.VideoProgressData
import kotlinx.android.synthetic.main.query_layout.view.*

class QueryAdapter(var mList: List<QueryDataRV>) :
    RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {

    inner class QueryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((Long) -> Unit)? = null
    fun setOnItemClickListener(listener: (Long) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryViewHolder {
        return QueryViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.query_layout,parent,false)
        )


    }

    override fun onBindViewHolder(holder: QueryViewHolder, position: Int) {
        holder.itemView.apply {
            tvTimestamp.text="${mList[position].timestamp} "
            tvQuery.text=mList[position].query

            setOnClickListener {
                onItemClickListener?.let {
                    it(mList[position].milliTimeStamp!!)
                }
            }

        }


    }

    override fun getItemCount(): Int {
        return mList.size

    }


}