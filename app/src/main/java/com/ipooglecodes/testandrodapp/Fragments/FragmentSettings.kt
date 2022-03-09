package com.ipooglecodes.testandrodapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import com.ipooglecodes.testandrodapp.Activities.TutorialActivity


class FragmentSettings:Fragment(){

    private lateinit var slider: Slider
    private lateinit var txtreportbug: TextView
    private lateinit var txtsuggestfeature: TextView

    private lateinit var reltutorial: RelativeLayout
    private lateinit var relreportbug: RelativeLayout
    private lateinit var relsuggestfeature: RelativeLayout
    private lateinit var relshareapp: RelativeLayout
    private lateinit var switchAutoPaste: Switch

    var mail_recipient = "support@massiveappsfzco.com"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val pref = requireContext().getSharedPreferences("default", Context.MODE_PRIVATE)
        val editor = pref.edit()

        var pastespeed = pref.getFloat("pastespeed", 0.0F)

        slider = root.findViewById(R.id.slider)
        txtreportbug = root.findViewById(R.id.txtreportbug)
        txtsuggestfeature = root.findViewById(R.id.txtsuggestfeature)
        relreportbug = root.findViewById(R.id.relreportbug)
        relsuggestfeature = root.findViewById(R.id.relsuggestfeature)
        reltutorial = root.findViewById(R.id.reltutorial)
        relshareapp = root.findViewById(R.id.relshareapp)
        switchAutoPaste = root.findViewById(R.id.switchAutoPaste)
        slider.value = pastespeed

        slider.addOnChangeListener { slider, value, fromUser ->
            Log.e("slider ", "value  " + value)

            editor.putFloat("pastespeed", value)
            editor.apply()
        }

        var autopasteIsChecked =pref.getBoolean("autopaste",false)
        switchAutoPaste.isChecked=autopasteIsChecked

        switchAutoPaste.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Switch1:ON" else "Switch1:OFF"

            editor.putBoolean("autopaste",isChecked)
            editor.apply()
        }

        relreportbug.setOnClickListener {
            try {
                val selectorIntent = Intent(Intent.ACTION_SENDTO)
                selectorIntent.data = Uri.parse("mailto:")
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(mail_recipient))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "AutoSnap (Android) - Support")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "")
                emailIntent.selector = selectorIntent
                startActivity(Intent.createChooser(emailIntent, "Send feedback to XYZ"))
            } catch (ex: ActivityNotFoundException) {
            }

//            val intent = Intent(Intent.ACTION_SENDTO)
//            intent.data = Uri.parse("mailto:") // only email apps should handle this
//            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mail_recipient))
//            intent.putExtra(Intent.EXTRA_SUBJECT, "AutoSnap (Android) - Support")
//            startActivity(intent)
        }

        relsuggestfeature.setOnClickListener {
            try {
                val selectorIntent = Intent(Intent.ACTION_SENDTO)
                selectorIntent.data = Uri.parse("mailto:")
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(mail_recipient))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "AutoSnap (Android) - Support")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "")
                emailIntent.selector = selectorIntent
                startActivity(Intent.createChooser(emailIntent, "Send feedback to XYZ"))
            } catch (ex: ActivityNotFoundException) {
            }

//            val intent = Intent(Intent.ACTION_SENDTO)
//            intent.data = Uri.parse("mailto:") // only email apps should handle this
//            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mail_recipient))
//            intent.putExtra(Intent.EXTRA_SUBJECT, "AutoSnap (Android) - Support")
//            startActivity(intent)
        }

        reltutorial.setOnClickListener {
            val intent = Intent(context, TutorialActivity::class.java)
            startActivity(intent)
        }

        relshareapp.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Auto Snap")
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    """
                    ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {

            }
        }
        return root
    }

}