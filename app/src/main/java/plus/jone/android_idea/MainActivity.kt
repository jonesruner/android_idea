package plus.jone.android_idea

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import plus.jone.android_idea.activity.TActivity_01
import plus.jone.android_idea.anim.AnimaActivity
import plus.jone.android_idea.base.exts.setLetterTrafficClickListener
import plus.jone.android_idea.base.ui.model.MainMenu
import plus.jone.android_idea.bluetooth.BthActivity
import plus.jone.android_idea.broadcastreceiver.BroadCastActivity
import plus.jone.android_idea.databinding.ActivityMainBinding
import plus.jone.android_idea.databinding.RecvMainListBinding
import plus.jone.android_idea.glide.TGlideActivity
import plus.jone.android_idea.okhttp.TOkhttpActivity
import plus.jone.android_idea.service.MusicServiceActivity
import plus.jone.android_idea.socket.SkChatMainActivity


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(position: Int) {
            val menu = mainMenus[position]
            RecvMainListBinding.bind(itemView).run {
                recvMenuIcon.text = getString(menu.icon)
                recvMenuName.text = menu.title
                itemView.setLetterTrafficClickListener {
                    try {
                        startActivity(Intent(this@MainActivity, menu.action),ActivityOptions.makeSceneTransitionAnimation(
                            this@MainActivity,itemView,"anim"
                        ).toBundle())

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    val mainMenus = arrayListOf(
            MainMenu("OkHttp", TOkhttpActivity::class.java, "An Internet Library", R.string.network),
            MainMenu( "Broad Cast Receiver", BroadCastActivity::class.java, "Broad cast receiver",  R.string.broadcast),
            MainMenu("Activity", TActivity_01::class.java, "Activity Lifecycler ", R.string.activity),
            MainMenu("Photo Glide", TGlideActivity::class.java, "Photo ", R.string.paint),
            MainMenu("Music Service", MusicServiceActivity::class.java, "use service ", R.string.music),
            MainMenu("Socket Chat", SkChatMainActivity::class.java, "socket chat ", R.string.chat),
        MainMenu("Bluetooth", BthActivity::class.java, "Bluetooth chat ", R.string.bluetooth),
        MainMenu("Animation", AnimaActivity::class.java, "Animationm", R.string.animation),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val gridLayoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
        binding.mainRecv.layoutManager = gridLayoutManager
        binding.mainRecv.adapter = object : RecyclerView.Adapter<VH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                return VH(layoutInflater.inflate(R.layout.recv_main_list, parent, false))
            }

            override fun getItemCount(): Int = mainMenus.size

            override fun onBindViewHolder(holder: VH, position: Int) {
                holder.bindView(position)
            }
        }
    }
}