package com.ipooglecodes.testandrodapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ipooglecodes.testandrodapp.Activities.isInEditMode


var allSavedTexts: Array<String> = emptyArray()


class TextAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<TextAdapter.TextViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.text_item, parent, false)
        //Log.d("activityState", "Loaded adapter")
        return TextViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        val text = allSavedTexts[position]
        holder.textView.text = text
        if (isInEditMode) {
            holder.imageView.setImageResource(R.drawable.reorder)
            holder.removeImageView.visibility = View.VISIBLE
        } else {
            holder.imageView.setImageResource(R.drawable.keyboard_arrow_right)
            holder.removeImageView.visibility= View.GONE
        }
        //Log.d("activityState", "Bound adapter")
        //update()
//        Timer().schedule(500) {
//            withContext(Dispatchers.Main) {
//                update()
//            }
//            Log.d("activityState", "Updated RecyclerView")
//        }
    }

    override fun getItemCount() = allSavedTexts.size

//    override fun getItemCount(): Int {
//        return allSavedTexts.size
//    }

    fun update(){
        notifyDataSetChanged()
    }

//    fun removeImageClick(view: View?) {
//        //Implement image click function
//        Log.d("testing", "tapped on remove")
//    }

    inner class TextViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val textView: TextView = itemView.findViewById(R.id.text_view)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val removeImageView: ImageView = itemView.findViewById(R.id.removeImageView)

        init {
            itemView.setOnClickListener(this)
            removeImageView.setOnClickListener {
                val position: Int = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    Log.d("testing", "position $position")
                    listener.removeItemClick(position)
                }
                Log.d("testing", "tapped on remove")
            }
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun removeItemClick(postion: Int)
    }

}