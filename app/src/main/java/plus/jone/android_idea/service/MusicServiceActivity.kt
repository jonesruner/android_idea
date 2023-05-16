package plus.jone.android_idea.service

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.textview.MaterialTextView
import plus.jone.android_idea.R
import plus.jone.android_idea.base.exts.requestPermissions
import plus.jone.android_idea.base.exts.showToast
import plus.jone.android_idea.databinding.ActivityMusicServiceBinding
import plus.jone.android_idea.service.frag.MusicAllFragment
import plus.jone.android_idea.service.model.Song

class MusicServiceActivity : AppCompatActivity(), MenuProvider {
    private val binding by lazy {
        ActivityMusicServiceBinding.inflate(layoutInflater)
    }

    private val onChangedListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            binding.amsTabLayout.getTabAt(position)!!.select()
        }
    }
    private val onTablSelectListener = object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            binding.amsViewpager.currentItem = tab!!.position
        }
        override fun onTabUnselected(tab: TabLayout.Tab?) {  }

        override fun onTabReselected(tab: TabLayout.Tab?) {  }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.amsTabLayout.addOnTabSelectedListener(onTablSelectListener)
        val listOf = arrayListOf(
            MusicAllFragment(),
        )

        binding.amsViewpager.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int =  listOf.size
            override fun createFragment(position: Int): Fragment = listOf[position]
        }

        binding.amsViewpager.registerOnPageChangeCallback(onChangedListener)
        arrayListOf("ALL"
//            , "Song", "Singer", "List"
        ).forEach {
            binding.amsTabLayout.addTab(binding.amsTabLayout.newTab().apply {
                val textV = MaterialTextView(this@MusicServiceActivity)
                textV.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textV.text = it
                customView = textV
            })
        }

        addMenuProvider(this)

        val intent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }else {
            startService(intent)
        }
        requestAndScan()
    }

    private fun requestAndScan(){
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)){
            it.values.forEach { isGranted ->
                if(!isGranted)return@requestPermissions
                readMusicFromLocal()
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.music_service_menu, menu)
    }


    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.music_scan -> {
               requestAndScan()
            }
            R.id.music_toggle_play_mode -> {
                playModelChangedListener?.onMusicPlayModelChanged()
            }
        }
        return false
    }


    private var playModelChangedListener:OnMusicPlayModelChangedListener? = null
    fun setModeChangedListener(modeChangedListener: OnMusicPlayModelChangedListener?){
        this.playModelChangedListener = modeChangedListener
    }
    private val songChangedListener: MutableList<OnMusicChangedListener> = mutableListOf()
    fun addSongChangedListener(listener: OnMusicChangedListener) {
        songChangedListener.add(listener)
        listener.onMusicChanged(songList)
    }

    fun removeListener(listener: OnMusicChangedListener) {
        songChangedListener.remove(listener)
    }

    private var songList: MutableList<Song> = mutableListOf()

    /**
     * get music list from local file
     */
    private fun readMusicFromLocal() {
        val getmusic = LocalMusicUtils.getmusic(this)

        songList.clear()
        songList.addAll(getmusic)

        songChangedListener.forEach {
            it.onMusicChanged(songList)
        }
        Log.d("TAG__", "onMenuItemSelected: ：${getmusic}")
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.amsViewpager.unregisterOnPageChangeCallback(onChangedListener)
        binding.amsTabLayout.removeOnTabSelectedListener(onTablSelectListener)
        removeMenuProvider(this)
    }

    interface OnMusicChangedListener {
        fun onMusicChanged(songList: MutableList<Song>)
    }
    interface OnMusicPlayModelChangedListener {
        fun onMusicPlayModelChanged()
    }
}