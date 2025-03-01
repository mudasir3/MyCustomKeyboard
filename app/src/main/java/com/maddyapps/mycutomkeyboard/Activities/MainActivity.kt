package com.maddyapps.mycutomkeyboard.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.maddyapps.mycutomkeyboard.FragmentSettings
import com.maddyapps.mycutomkeyboard.Fragments.FragmentEditSnaps
import com.maddyapps.mycutomkeyboard.R
import com.maddyapps.mycutomkeyboard.TextAdapter
import com.maddyapps.mycutomkeyboard.allSavedTexts
//import com.ironsource.adapters.supersonicads.SupersonicConfig
//import com.ironsource.mediationsdk.ISBannerSize
//import com.ironsource.mediationsdk.IronSource
//import com.ironsource.mediationsdk.logger.IronSourceError
//import com.ironsource.mediationsdk.sdk.BannerListener
import java.util.*


class MainActivity : AppCompatActivity(), TextAdapter.OnItemClickListener{

    private lateinit var rel_snaps:RelativeLayout
    private lateinit var rel_settings:RelativeLayout
    private lateinit var txt_text:TextView
    private lateinit var txt_settings:TextView
    private lateinit var imgsnaps:ImageView
    private lateinit var imgsettings:ImageView
    private lateinit var mAdView: AdView

    private val adapter = TextAdapter(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(
            this
        ) {

        }

        //mAdView = findViewById(R.id.adView)

        //setUpBannerAd()

        val actionBar = supportActionBar
        actionBar!!.title = "Edit Snaps"

        rel_snaps = findViewById(R.id.rel_snaps)
        rel_settings = findViewById(R.id.rel_settings)
        txt_text = findViewById(R.id.txt_text)
        txt_settings = findViewById(R.id.txt_settings)
        imgsnaps = findViewById(R.id.imgsnaps)
        imgsettings = findViewById(R.id.imgsettings)

        val textFragment =
            FragmentEditSnaps()
        val transaction: FragmentTransaction =
            supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, textFragment, "editsnaps")
        transaction.addToBackStack(null)
        transaction.commit()

        rel_snaps.setOnClickListener {

            setImageResource(this,imgsnaps,"edit_snap_selected")
            setImageResource(this,imgsettings,"settings_unselected")
            txt_text.setTextColor(resources.getColor(R.color.purple_500))
            txt_settings.setTextColor(resources.getColor(R.color.txt_gray))
            actionBar.title = "Edit Snaps"

            val textFragment =
                FragmentEditSnaps()
            val transaction: FragmentTransaction =
                supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, textFragment, "editsnaps")
            transaction.addToBackStack(null)
            transaction.commit()

        }

        rel_settings.setOnClickListener {

            setImageResource(this,imgsnaps,"edit_snap_unselected")
            setImageResource(this,imgsettings,"settings_selected")
            txt_text.setTextColor(resources.getColor(R.color.txt_gray))
            txt_settings.setTextColor(resources.getColor(R.color.purple_500))

            actionBar!!.title = "Settings"

            val settingsFragment =
                FragmentSettings()
            val transaction: FragmentTransaction =
                supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, settingsFragment, "editsettings")
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }


    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0) {
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
        editor.putString("AllSavedTexts", sb.toString())
        editor.commit()
    }

    private fun openNextActivity(position: Int) {
        lastPostitonIndex = position
        val intent = Intent(this, EditSnapsActivity::class.java)
        startActivity(intent)
    }

    fun setTextAppearance(context: Context, textView: TextView, style:Int){
        textView.setTextAppearance(context, style)
    }

    fun setImageResource(context: Context, img: ImageView, drawable: String){
        img.setImageResource(getImage(context,drawable))
    }

    fun getImage(context: Context, imageName: String?): Int {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName())
    }


    override fun onItemClick(position: Int) {
        //Toast.makeText(this, "Item $position clicked", Toast.LENGTH_SHORT).show()
        if (!isInEditMode) {
            openNextActivity(position)
        }
    }

    override fun removeItemClick(postion: Int) {
        Log.d("testing", "recieved remove for item $postion")
        allSavedTexts = remove(allSavedTexts, postion)
        saveAllEditSnaps()
        adapter.notifyItemRemoved(postion)
    }

//    fun setUpBannerAd(){
//        val adRequest = AdRequest.Builder().build()
//        mAdView.loadAd(adRequest)
//
//        mAdView.adListener = object: AdListener() {
//            override fun onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//            }
//
//            override fun onAdClosed() {
//                // Code to be executed when the user is about to return
//                // to the app after tapping on an ad.
//            }
//
//            override fun onAdFailedToLoad(adError : LoadAdError) {
//                // Code to be executed when an ad request fails.
//
//                LoadIronSource()
//            }
//
//            override fun onAdImpression() {
//                // Code to be executed when an impression is recorded
//                // for an ad.
//            }
//
//            override fun onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//            }
//
//            override fun onAdOpened() {
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//            }
//        }
//
//    }
//
//    fun LoadIronSource(){
//        val advertisingId = IronSource.getAdvertiserId(this)
//        // we're using an advertisingId as the 'userId'
//        initIronSource(advertisingId)
//        IronSource.shouldTrackNetworkState(this, true)
//    }
//
//    private fun initIronSource(userId: String?) {
//        SupersonicConfig.getConfigObj().clientSideCallbacks = true
//        // set the IronSource user id
//        IronSource.setUserId(userId)
//        // init the IronSource SDK
//        IronSource.init(this, "13523bd89", IronSource.AD_UNIT.BANNER)
//
//        createAndloadBanner()
//    }
//
//    fun createAndloadBanner(){
//        val banner = IronSource.createBanner(this, ISBannerSize.BANNER)
//        val layoutParams = FrameLayout.LayoutParams(
//            FrameLayout.LayoutParams.MATCH_PARENT,
//            FrameLayout.LayoutParams.WRAP_CONTENT
//        )
//        mAdView.addView(banner, 0, layoutParams)
//
//        banner.bannerListener = object : BannerListener {
//            override fun onBannerAdLoaded() {
//                // Called after a banner ad has been successfully loaded
//            }
//
//            override fun onBannerAdLoadFailed(error: IronSourceError) {
//                // Called after a banner has attempted to load an ad but failed.
//                //runOnUiThread { bannerContainer.removeAllViews() }
//            }
//
//            override fun onBannerAdClicked() {
//                // Called after a banner has been clicked.
//            }
//
//            override fun onBannerAdScreenPresented() {
//                // Called when a banner is about to present a full screen content.
//            }
//
//            override fun onBannerAdScreenDismissed() {
//                // Called after a full screen content has been dismissed
//            }
//
//            override fun onBannerAdLeftApplication() {
//                // Called when a user would be taken out of the application context.
//            }
//        }
//        IronSource.loadBanner(banner)
//    }
}