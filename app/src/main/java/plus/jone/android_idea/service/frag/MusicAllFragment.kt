package plus.jone.android_idea.service.frag

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import plus.jone.android_idea.R
import plus.jone.android_idea.base.exts.showToast
import plus.jone.android_idea.databinding.MusicAllFragmentBinding
import plus.jone.android_idea.service.MusicService
import plus.jone.android_idea.service.MusicServiceActivity
import plus.jone.android_idea.service.model.JSong
import plus.jone.android_idea.service.model.MusicBinder
import plus.jone.android_idea.service.model.Song

class MusicAllFragment : Fragment(), MusicServiceActivity.OnMusicChangedListener,
    MusicServiceActivity.OnMusicPlayModelChangedListener {

    private val recv by lazy {
        RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return recv
    }

    private var currentName = ""
    private var binder: MusicBinder? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as MusicBinder?
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            binder?.stop()
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().bindService(Intent(requireContext(),MusicService::class.java),serviceConnection,Context.BIND_AUTO_CREATE)
    }
    override fun onMusicChanged(songList: MutableList<Song>) {
        currentName = "list_:" + System.currentTimeMillis()
        Log.d("TAG____", "onMusicChanged: ${songList.size} ")
        recv.adapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ViewHolder {
                val view = LayoutInflater.from(requireContext()) .inflate(R.layout.music_all_fragment, parent, false)
                return object : ViewHolder(view) {}
            }

            override fun getItemCount(): Int = songList.size

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                MusicAllFragmentBinding.bind(holder.itemView).apply {
                    allFraSname.text = songList[position].name
                    allFraBtn.setOnClickListener {
                       try {
                           binder ?: return@setOnClickListener
                           if (!binder!!.isCurrrentSongList(currentName)) {
                               binder!!.songListToggle(currentName, songList.map {
                                   it.run {
                                       JSong(name,singer,size, duration, path, albumId, id)
                                   }
                               }, position)
                           }
                           binder!!.toggle(position)
                       }catch (e:Exception){
                           requireContext().showToast("${e.message}")
                           e.printStackTrace()
                       }
                    }
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        (requireActivity() as MusicServiceActivity).setModeChangedListener(this)
        (requireActivity() as MusicServiceActivity).addSongChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MusicServiceActivity).setModeChangedListener(null)
        (requireActivity() as MusicServiceActivity).removeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unbindService(serviceConnection)
    }

    override fun onMusicPlayModelChanged() {
        binder?:return
        binder!!.setIsCircle(!binder!!.isCircle())
    }
}