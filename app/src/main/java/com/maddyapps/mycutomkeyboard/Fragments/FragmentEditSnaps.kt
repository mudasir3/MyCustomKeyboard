package com.maddyapps.mycutomkeyboard.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.maddyapps.mycutomkeyboard.*
import com.maddyapps.mycutomkeyboard.Activities.*
import java.util.*

class FragmentEditSnaps():Fragment(), TextAdapter.OnItemClickListener {

    private lateinit var recycler_view:RecyclerView
    private val adapter = TextAdapter(this)
    var firstEdit = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_next, container, false)

        setHasOptionsMenu(true)

        //val actionBar = requireContext().supportActionBar
        val addButton = root.findViewById<FloatingActionButton>(R.id.add_snap_button)
        recycler_view = root.findViewById<RecyclerView>(R.id.recycler_view)

        //Get data in shared preferences
        val pref = requireContext().getSharedPreferences("default", Context.MODE_PRIVATE)
        firstOpen = pref.getBoolean("firstOpen", true)

        if (firstOpen) {
            Log.d("open", "firstOpen")
            allSavedTexts = arrayOf("😈👹 SAY YOU ARE MY BAKA 👹😈",
                "I MISS THE RAGE‼️👹 I MISS THE RAGE‼️👹",
                "🗽Did🗽You🗽Know🗽Liberty🗽Mutual🗽Customizes🗽Your🗽Insurance🗽So🗽You🗽Only🗽Pay🗽For🗽What🗽You🗽Need🗽",
                "Wanna 🙋 break 🚫 from the ads 💸 watch 👀 this short ⬇ video 📽 for 30 minutes ⏰ of 🚫 ad free 🛇 🔥🎶 music 🎶🔥💯",
                "What🥶You🥵Know🥶About🥵 Rolling 🥶Down🥵In🥶The🥵Deep🥶",
                "DORÏME👽 ĪÑTERįME ÅDAPÁRÉ👺👽DØRÌMÉ🌚👽 AMÈÑØ 👽⚠️AMĖÑÕ 👶🏿🐭 ŁÄTÏRÊ👹👹👽 ŁÄTĪRÊMÒ👽🩸")
            saveAllEditSnaps()
            firstOpen = false
            //Save data in shared preferences
            val intent = Intent(context, TutorialActivity::class.java)
            startActivity(intent)
        } else {
            Log.d("open", "Getting AllSavedTexts")
            val textsString = pref.getString("AllSavedTexts", "?").toString()
            Log.d("open", textsString)
            allSavedTexts = textsString.split("◳").toTypedArray()
        }

        //actionBar!!.title = "Edit Snaps"

//        val editButton: MenuItem = findViewById(R.id.edit_item)
//        updateEditButton(editButton)

        addButton.setOnClickListener {
            isInEditMode = false //turn off edit mode
            openNextActivity(-1)
        }

        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.setHasFixedSize(true)
        Log.d("ActivityState", "Opened")
        return root
    }

    override fun onItemClick(position: Int) {
        if (!isInEditMode) {
            openNextActivity(position)
        }
    }

    override fun removeItemClick(postion: Int) {
        allSavedTexts = remove(allSavedTexts, postion)
        saveAllEditSnaps()
        adapter.notifyItemRemoved(postion)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.edit_item -> {
            isInEditMode = !isInEditMode
            updateEditButton(item)
            //adapter.update()
            //adapter.notifyDataSetChanged()

            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    private fun updateEditButton(item: MenuItem) {
        Log.e("menuuu", " updateEditButton")
        if (isInEditMode) {
            if (firstEdit) {
                val itemTouchHelper = ItemTouchHelper(simpleCallback)
                itemTouchHelper.attachToRecyclerView(recycler_view)
                firstEdit = false //Cant figure out a way to remove itemTouchHelper after done is clicked, so this just waits to add it after they click edit the first time
            }
            adapter.update()
            item.title = "Done"
        } else {
            adapter.update()
            item.title = "Edit"
        }
    }

    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP.or(
            ItemTouchHelper.DOWN), 0) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            if (isInEditMode) {
                var startPosition = viewHolder.adapterPosition
                var endPosition = target.adapterPosition

                val list: MutableList<String> = allSavedTexts.toMutableList()
                Collections.swap(list, startPosition, endPosition)
                allSavedTexts = list.toTypedArray()
                saveAllEditSnaps()
                recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)
                return true
            } else {
                return false
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

    }

    fun saveAllEditSnaps() {
        //Save data in shared preferences
        val pref = requireContext().getSharedPreferences("default", Context.MODE_PRIVATE)
        val editor = pref.edit()
        //Convert array into string
        val sb = StringBuilder()
        for (i in 0 until allSavedTexts.size) {
            sb.append(allSavedTexts.get(i))
            if (i != allSavedTexts.size-1) {
                sb.append("◳") //Here I used a random unicode symbol (that no one will probably type) to sepratate each value in the array
            }
        }
        Log.d("save", sb.toString())
        //Save string
        editor.putString("AllSavedTexts", sb.toString())
        editor.commit()
    }

    private fun openNextActivity(position: Int) {
        lastPostitonIndex = position
        val intent = Intent(context, EditSnapsActivity::class.java)
        startActivity(intent)
    }
}