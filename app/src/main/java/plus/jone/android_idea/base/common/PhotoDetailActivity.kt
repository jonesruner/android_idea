package plus.jone.android_idea.base.common

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.ortiz.touchview.TouchImageView
import plus.jone.android_idea.R
import plus.jone.android_idea.glide.TGlideActivity

class PhotoDetailActivity
    : AppCompatActivity()
{

    companion object{
        val ARG_1 = "url"
        val ARG_2 = "url_list"
        val ARG_3 = "url_pos"
        fun startThis(context: Context,url:String) {
            context.startActivity(Intent(context,PhotoDetailActivity::class.java).apply {
                putExtra(ARG_1,url)
            })
        }
        fun startThis(context: Context, urlList: List<String?>, position: Int) {
            context.startActivity(Intent(context,PhotoDetailActivity::class.java).apply {
                putExtra(ARG_2, urlList.toTypedArray())
                putExtra(ARG_3, position)
            })
        }
    }


    private var pos = 0
    private var urlList = arrayListOf<String>()
    private val viewPager by lazy {
        ViewPager2(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }


    fun loadImgInto(url: String, v: ImageView) {
        Glide.with(this).load(url).placeholder(
            R.mipmap.ic_launcher
        ).apply {
            into(v)
        }
    }


    inner class VH(view:View) : RecyclerView.ViewHolder(view){
        fun bind(pos : Int){
            (itemView as TouchImageView).run {
                loadImgInto(urlList[pos],this)
            }
        }
    }



    private val adapter = object  : RecyclerView.Adapter<VH>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                TouchImageView(this@PhotoDetailActivity).apply {
                    isFocusable = true
                    isClickable = true
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    setOnTouchListener { view, event ->
                        var result = true
                        //can scroll horizontally checks if there's still a part of the image
                        //that can be scrolled until you reach the edge
                        if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && canScrollHorizontally(-1)) {
                            //multi-touch event
                            result = when (event.action) {
                                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                                    // Disallow RecyclerView to intercept touch events.
                                    parent.requestDisallowInterceptTouchEvent(true)
                                    // Disable touch on view
                                    false
                                }
                                MotionEvent.ACTION_UP -> {
                                    // Allow RecyclerView to intercept touch events.
                                    parent.requestDisallowInterceptTouchEvent(false)
                                    true
                                }
                                else -> true
                            }
                        }
                        result
                    }
                }
            )
        }
        override fun getItemCount(): Int = urlList.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(position)
        }
    }

//    private val img by lazy {
//        TouchImageView(this).apply {
//            isFocusable = true
//            isClickable = true
//            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
//        }
//    }



    override fun onCreate(savedInstanceState: Bundle?) {
        hideSystemUI()
        //去除actionbar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(viewPager)
        getUrlsFromIntent()

        window.decorView.setOnClickListener {
            hideSystemUI()
        }
    }

    private fun getUrlsFromIntent() {
        val url = intent.getStringExtra(ARG_1)
        val position = intent.getIntExtra(ARG_3,0)
        if(url != null){
            urlList.add(url)
            setAdapter()
            return
        }

        val listExtra = intent.getStringArrayExtra(ARG_2)

        if(listExtra != null)
        {
            pos = position
            urlList.addAll(listExtra)
            setAdapter()
        }
    }

    private fun setAdapter() {
        viewPager.adapter = adapter
        Log.d("TAG__", "setAdapter: ${urlList.size}")
        viewPager.setCurrentItem(pos,false)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}