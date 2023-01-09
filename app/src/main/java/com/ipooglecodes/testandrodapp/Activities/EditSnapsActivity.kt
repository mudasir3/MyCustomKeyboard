package com.ipooglecodes.testandrodapp.Activities


import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ipooglecodes.testandrodapp.R
import com.ipooglecodes.testandrodapp.allSavedTexts
import com.ironsource.adapters.supersonicads.SupersonicConfig
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.InterstitialListener

var lastPostitonIndex = -1


class EditSnapsActivity() : AppCompatActivity() {
    private var mInterstitialAd: InterstitialAd? = null

    var firstLoad=true


    override fun onResume() {
        super.onResume()
        if(firstLoad){
            firstLoad=!firstLoad
        }
        else
        {
            val intent = Intent(this@EditSnapsActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_snaps)

        setUpInterstitial()
        setUpIronSource()

        val actionBar = supportActionBar
        var textView = findViewById<EditText>(R.id.editTextTextMultiLine)
        val selectAllButton = findViewById<Button>(R.id.selectAllButton)
        val pasteButton = findViewById<Button>(R.id.pasteButton)

        //registerForContextMenu(textView)
        textView.requestFocus()
        val imm: InputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)

//        if (textView.requestFocus()) {
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT)
//        }

        if (lastPostitonIndex == -1) {
            actionBar!!.title = "New"
        } else {
            actionBar!!.title = "Edit"
            textView.setText(allSavedTexts[lastPostitonIndex],TextView.BufferType.EDITABLE)
        }

        actionBar.setDisplayHomeAsUpEnabled(true)

        selectAllButton.setOnClickListener {
            //textView.isSelected = true
            //textView.setSelectAllOnFocus(true)
            textView.selectAll()
            //textView.requestFocus()
            //textView.setSelectAllOnFocus(false)
        }

        pasteButton.setOnClickListener {
            var clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val item = clipboard.primaryClip?.getItemAt(0)
            // Gets the clipboard as text.
            val pasteData = item?.text
            //textView.append(pasteData)
            if (pasteData != null) {
                val start = Math.max(textView.getSelectionStart(), 0)
                val end = Math.max(textView.getSelectionEnd(), 0)
                Log.d("paste", "Start: " + start + " End: " + end + " CLIPBOARD: " + pasteData)
                val newText = textView.text.replaceRange(
                    start,
                    end,
                    pasteData
                )
                textView.setText(newText,TextView.BufferType.EDITABLE)
                textView.setSelection(textView.text.toString().length)

                //textView.setSelection(start+pasteData.length)
//                textView.getText().replace(
//                    Math.min(start, end), Math.max(start, end),
//                    pasteData, 0, pasteData.length
//                )
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.save_item -> {
            // do stuff
            var textView = findViewById<TextView>(R.id.editTextTextMultiLine)
//            allSavedTexts = textView.text + allSavedTexts
            if (textView.text != null) {
                if (lastPostitonIndex == -1) { //Add Snap to start of array
                    allSavedTexts = append(allSavedTexts, textView.text.toString(), 0)
                } else { //Replace Snap in correct index of array
                    allSavedTexts[lastPostitonIndex] = textView.text.toString()
                }
                //Save data in shared preferences
                val pref = getSharedPreferences("default", Context.MODE_PRIVATE)
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
                val imm: InputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                editor.putString("AllSavedTexts", sb.toString())
                editor.commit()
            }
            for (text in allSavedTexts) {
                Log.d("allSavedTexts", text)
            }
            //finish()

            // Only runs if there is a view that is currently focused
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }

//            textView.clearFocus()

            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
                var isReady =IronSource.isInterstitialReady()
                if(isReady){
                    IronSource.showInterstitial();
                }
                else
                {
                    Toast.makeText(this,"Text Saved",Toast.LENGTH_SHORT).show()
                }
            }



//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun setUpInterstitial(){
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("iinterstitial", "onAdFailedToLoad" +adError?.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.e("iinterstitial", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.e("iinterstitial", "Ad was clicked.")
                val intent = Intent(this@EditSnapsActivity, MainActivity::class.java)
                startActivity(intent)
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.e("iinterstitial", "Ad dismissed fullscreen content.")
                mInterstitialAd = null

                val intent = Intent(this@EditSnapsActivity, MainActivity::class.java)
            startActivity(intent)
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)

                val intent = Intent(this@EditSnapsActivity, MainActivity::class.java)
                startActivity(intent)
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.e("iinterstitial", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.e("iinterstitial", "Ad showed fullscreen content.")

                val intent = Intent(this@EditSnapsActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun setUpIronSource(){
        val advertisingId = IronSource.getAdvertiserId(this)
        // we're using an advertisingId as the 'userId'
        initIronSource(advertisingId)
        IronSource.shouldTrackNetworkState(this, true)
    }

    private fun initIronSource(userId: String?) {
        SupersonicConfig.getConfigObj().clientSideCallbacks = true
        // set the IronSource user id
        IronSource.setUserId(userId)
        // init the IronSource SDK
        IronSource.init(this, "13523bd89", IronSource.AD_UNIT.INTERSTITIAL)

        createAndloadInterstitial()
    }

    fun createAndloadInterstitial(){
        IronSource.setInterstitialListener( object :InterstitialListener {
            override fun onInterstitialAdReady() {

            }

            override fun onInterstitialAdLoadFailed(p0: IronSourceError?) {

            }

            override fun onInterstitialAdOpened() {

            }

            override fun onInterstitialAdClosed() {

            }

            override fun onInterstitialAdShowSucceeded() {

            }

            override fun onInterstitialAdShowFailed(p0: IronSourceError?) {

            }

            override fun onInterstitialAdClicked() {

            }

        })

        IronSource.loadInterstitial();

    }
}

//These functions convert arrays to a mutible list so that they can be modified, this could be changed in the future though so that alllEditSnaps is a mutable list in itself
fun append(arr: Array<String>, element: String, index: Int): Array<String> {
    val list: MutableList<String> = arr.toMutableList()
    list.add(index, element)
    return list.toTypedArray()
}


fun remove(arr: Array<String>, index: Int): Array<String> {
    val list: MutableList<String> = arr.toMutableList()
    list.removeAt(index)
    return list.toTypedArray()
}