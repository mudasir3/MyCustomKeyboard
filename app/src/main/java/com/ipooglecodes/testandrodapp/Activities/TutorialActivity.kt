package com.ipooglecodes.testandrodapp.Activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.ipooglecodes.testandrodapp.R

class TutorialActivity : AppCompatActivity() {

    var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        var titleTextView = findViewById(R.id.titleTextView) as TextView
        var descriptionTextView = findViewById(R.id.descriptionTextView) as TextView
        var nextbutton = findViewById(R.id.nextButton) as Button
        var image = findViewById(R.id.screenshot_image_view) as ImageView
        var buttonsettings = findViewById(R.id.btnOpenSettings) as Button
        buttonsettings.setOnClickListener {
            val intent = Intent()
            intent.component = ComponentName(
                "com.android.settings",
                "com.android.settings.Settings\$LanguageAndInputSettingsActivity"
            )
            startActivity(intent)
        }

        nextbutton.setOnClickListener {
            if (page == 0) {
                //Transition to next tutroial screen
                titleTextView.text = "Switch to AutoSnap"
                descriptionTextView.text = "To switch to AutoSnap keyboard while in an app, tap the small keyboard button right of your keyboard, then select AutoSnap. Remember AutoSnap can only AutoSend on apps like Snapchat that have the send button built into the keyboard. In all other apps, you have to hold the paste button and then tap send manually."
                nextbutton.text = "Go to App"
                image.setImageResource(R.drawable.switch_to_keyboard_image)
            } else {
                val pref = getSharedPreferences("default", Context.MODE_PRIVATE)
                val editor = pref.edit()
                editor.putBoolean("firstOpen", false)
                editor.commit()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            page++
        }
    }
}