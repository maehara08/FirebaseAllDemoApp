package com.cs.land.riku_maehara.firebasealldemoapp

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.cs.land.riku_maehara.firebasealldemoapp.model.Message
import com.github.curioustechizen.ago.RelativeTimeTextView
import jp.satorufujiwara.binder.recycler.RecyclerBinder

/**
 * Created by riku_maehara on 2017/03/31.
 */

class OtherMessageBinder(activity: Activity,
                         private val message: Message,
                         val listener: OtherMessageBinderListener? = null) :
        RecyclerBinder<BinderViewType>(activity, BinderViewType.OtherMessage) {

    override fun onCreateViewHolder(view: View): RecyclerView.ViewHolder = ViewHolder(view)

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        (viewHolder as ViewHolder).message = message
    }

    override fun layoutResId(): Int = R.layout.list_item_chat_others

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.bindView(R.id.message)
        private val timestampView:RelativeTimeTextView = itemView.bindView(R.id.timestamp)
        var message: Message? = null
            set(value) {
                field = value
                value?.let {
                    bindMessage(it)
                }
            }

        init {
            messageTextView.setOnClickListener {
                //listener.onClick
            }
        }

        private fun bindMessage(message: Message) {
            messageTextView.text = message.message
            timestampView.setReferenceTime(message.timeStamp)
        }
    }

    interface OtherMessageBinderListener {
        fun onOtherMessageClicked(message: Message, viewHolder: ViewHolder)
    }
}