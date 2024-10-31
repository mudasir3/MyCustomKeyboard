package com.ipooglecodes.testandrodapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.KeyboardView
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ipooglecodes.testandrodapp.Activities.isInEditMode


class MyInputMethodService : InputMethodService(), KeyboardView.OnKeyboardActionListener,
    TextAdapter.OnItemClickListener {


    private val adapter = TextAdapter(this)
    private var isInDetailView = false
    private var currentSnap = ""
    private var isSpamming = false
    var keyboardDetailView: RelativeLayout? = null
    var constraintlayout: ConstraintLayout? = null
    var mainTextView: TextView? = null
    private lateinit var img_cross: ImageView
    private lateinit var main_text_view: TextView


    //    @RequiresApi(Build.VERSION_CODES.P) //REMOVE THIS LATER TO SUPPORT LOWER API LEVELS
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateInputView(): View {

        var View = layoutInflater.inflate(R.layout.keyboard_view, null)
        var recycler_view = View.findViewById(R.id.keyboard_recycler_view) as RecyclerView
        var switchkeyboardTV = View.findViewById(R.id.switch_keyboard_tv) as TextView
        keyboardDetailView = View.findViewById(R.id.keyboard_detail_view)
        constraintlayout = View.findViewById(R.id.constraintlayout)
        mainTextView = View.findViewById(R.id.main_text_view)
        main_text_view = View.findViewById(R.id.main_text_view)
        var backTextView = View.findViewById(R.id.back_text_view) as TextView
        var sendButton = View.findViewById(R.id.send_button) as Button
        img_cross = View.findViewById(R.id.img_cross)


        val pref = getSharedPreferences("default", Context.MODE_PRIVATE)
//        var autopaste =pref.getBoolean("autopaste",false)
//        if (autopaste){
//            sendButton.text="Send once or hold to AutoPaste"
//        }else
//        {
//            sendButton.text="Send"
//        }



        val mode = applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        when (mode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                constraintlayout!!.setBackground(ContextCompat.getDrawable(applicationContext, R.color.dark))
                keyboardDetailView!!.setBackground(ContextCompat.getDrawable(applicationContext, R.color.dark))
                main_text_view.setTextColor(resources.getColor(R.color.white))
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                constraintlayout!!.setBackground(ContextCompat.getDrawable(applicationContext, R.color.white))
                keyboardDetailView!!.setBackground(ContextCompat.getDrawable(applicationContext, R.color.white))
                main_text_view.setTextColor(resources.getColor(R.color.dark))

            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                constraintlayout!!.setBackground(ContextCompat.getDrawable(applicationContext, R.color.white))
                keyboardDetailView!!.setBackground(ContextCompat.getDrawable(applicationContext, R.color.white))
                main_text_view.setTextColor(resources.getColor(R.color.dark))

            }
        }



        //Get data in shared preferences
        val textsString = pref.getString("AllSavedTexts", "?").toString()
        val pastespeed = pref.getFloat("pastespeed", 0.0F)
        var speed = 0
        if (pastespeed == 0.0F) {
            speed = 100
        }
        if (pastespeed == 1.0F) {
            speed = 1000
        }
        if (pastespeed == 2.0F) {
            speed = 900
        }
        if (pastespeed == 3.0F) {
            speed = 800
        }
        if (pastespeed == 4.0F) {
            speed = 700
        }
        if (pastespeed == 5.0F) {
            speed = 600
        }
        if (pastespeed == 6.0F) {
            speed = 500
        }
        if (pastespeed == 7.0F) {
            speed = 400
        }
        if (pastespeed == 8.0F) {
            speed = 300
        }
        if (pastespeed == 9.0F) {
            speed = 200
        }
        if (pastespeed == 10.0F) {
            speed = 100
        }

        Log.e("sliderr ", " speed " + speed)

        Log.d("open", textsString)

        val imeOptions = currentInputEditorInfo.imeOptions
        val action = imeOptions and EditorInfo.IME_MASK_ACTION

        Log.d("Keyboard", "Did startup" + action)

        if (action == 6) {
            sendButton.text = "Send once or hold to AutoPaste"
        } else {
            sendButton.text = "Send once or hold to AutoSend"
        }


        keyboardDetailView?.visibility = android.view.View.GONE

        if (Build.VERSION.SDK_INT > 28) {
            if (!shouldOfferSwitchingToNextInputMethod()) {
                switchkeyboardTV.visibility = android.view.View.GONE
            }

            switchkeyboardTV.setOnClickListener {
                Log.d("onClick", "Did tap switchKeyboard")
                switchToNextInputMethod(false)
            }
        }

        var handler = Handler()

        //var cleartexthandler = Handler()
        val cleartextRunnable: Runnable = object : Runnable {
            override fun run() {
                val inputConnection = currentInputConnection
                inputConnection.deleteSurroundingText(1, 0)
                val selectedText = inputConnection.getSelectedText(0)

                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.deleteSurroundingText(1, 0)
                } else {
                    inputConnection.commitText("", 1)
                }
                handler.postDelayed(this, 100)
            }
        }


        img_cross.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(arg0: View, arg1: MotionEvent): Boolean {
                when (arg1.action) {
                    MotionEvent.ACTION_DOWN -> {
                        handler.postDelayed(cleartextRunnable, 100)
                        Log.e("clickkk ", " ACTION_DOWN ")
                    }
                    MotionEvent.ACTION_UP -> {
                        handler.removeCallbacks(cleartextRunnable)
                        Log.e("clickkk ", " ACTION_UP ")
                    }
                }
                return false
            }
        })

        img_cross.setOnClickListener {
            Log.e("clickkk ", " ONCLICKKK ")

            val inputConnection = currentInputConnection
            val selectedText = inputConnection.getSelectedText(0)

            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0)
            } else {
                inputConnection.commitText("", 1)
            }
        }

        backTextView.setOnClickListener {
            isInDetailView = false
            keyboardDetailView?.visibility = android.view.View.GONE
        }

        val myRunnable: Runnable = object : Runnable {
            override fun run() {
                if (action == 6) {
                    currentInputConnection.commitText(currentSnap, 1)
                    return
                } else {
                    currentInputConnection.commitText(currentSnap, 1)
                    currentInputConnection.sendKeyEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_ENTER
                        )
                    )
                }

//                val imeOptions = currentInputEditorInfo.imeOptions
//                val action = imeOptions and EditorInfo.IME_MASK_ACTION
//                var b = currentInputConnection.performEditorAction(action)

                handler.postDelayed(this, speed.toLong())
            }
        }

        sendButton.setOnClickListener {

            if (isSpamming) {
                Log.d("sendButton", "isSpamming")

                if (action == 6) {
                    sendButton.text = "Send once or hold to AutoPaste"
                } else {
                    sendButton.text = "Send once or hold to AutoSend"
                }
                handler.removeCallbacks(myRunnable)

                sendButton.setBackgroundColor(Color.YELLOW)
                sendButton.setTextColor(Color.BLACK)
                isSpamming = false
            } else {

                var b = currentInputConnection.commitText(currentSnap, 1)

                var send = currentInputConnection.sendKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER
                    )
                )

            }
        }

        sendButton.setOnLongClickListener {
            var autopaste =pref.getBoolean("autopaste",false)
            if(autopaste) {
                Log.d("sendButton", "long press")
                isSpamming = true
                sendButton.text = "Stop"
                sendButton.setBackgroundColor(Color.RED)
                sendButton.setTextColor(Color.WHITE)
                handler.postDelayed(myRunnable, 100)
            }
            else
            {
                Toast.makeText(applicationContext,"Auto Paste is turned off",Toast.LENGTH_SHORT).show()
            }
            return@setOnLongClickListener true
        }


        allSavedTexts = textsString.split("◳").toTypedArray()

        isInEditMode = false

        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        return View
    }


    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        setInputView(onCreateInputView());
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onPress(primaryCode: Int) {
        Log.d("Keyboard", "onPress")
    }

    override fun onRelease(primaryCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        TODO("Not yet implemented")
    }

    override fun onText(text: CharSequence?) {
        TODO("Not yet implemented")
    }

    override fun swipeLeft() {
        TODO("Not yet implemented")
    }

    override fun swipeRight() {
        TODO("Not yet implemented")
    }

    override fun swipeDown() {
        TODO("Not yet implemented")
    }

    override fun swipeUp() {
        TODO("Not yet implemented")
    }

    override fun onItemClick(position: Int) {
        //if (!isInDetailView) {
        Log.d("Keyboard", "onItemClick")
        currentSnap = allSavedTexts[position]
        mainTextView?.text = currentSnap
        keyboardDetailView?.visibility = View.VISIBLE
        isInDetailView = true
        //  }
    }

    override fun removeItemClick(postion: Int) {
        //Nothing needs to go here
    }


}