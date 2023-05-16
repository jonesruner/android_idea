package plus.jone.android_idea.glide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.jone.plus.jdt.base.entity.git.GitPhoto
import okhttp3.Request
import plus.jone.android_idea.R
import plus.jone.android_idea.base.common.PhotoDetailActivity
import plus.jone.android_idea.base.ui.refresh.SwipeLayout
import plus.jone.android_idea.base.utills.JsonUtils.convertJsonToObjOrArray
import plus.jone.android_idea.base.utills.LoadingUtils
import plus.jone.android_idea.databinding.ActivityTglideBinding
import plus.jone.android_idea.databinding.RvImgViewBinding
import plus.jone.android_idea.okhttp.OkHttpRequest
import java.util.*
import kotlin.concurrent.thread


class TGlideActivity : AppCompatActivity() {
    private val TAG = TGlideActivity::class.java.simpleName
    private val binding by lazy {
        ActivityTglideBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.glideList.contentRv.layoutManager =  StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        sendRequest()

        binding.glideList.actionListener = object : SwipeLayout.ActionListener {
            override fun onRefresh() {
                array?.clear()
                sendRequest()
            }

            override fun onLoadMore() {
//                if (array == null) {
//                    sendRequest()
//                } else {
//                    val newArr = mutableListOf<GitPhoto>()
//                    Collections.copy(array, newArr)
//                    newArr.forEach {
//                        it.download_url += "?time=${System.currentTimeMillis()}"
//                    }
//                    array!!.addAll(newArr)
//                    mAdapter.notifyItemRangeChanged(array!!.size / 2, array!!.size)
//                }
            }
        }
    }

    fun loadImgInto(url: String, v: ImageView) {
        Glide.with(this@TGlideActivity).load(url).placeholder(
            R.mipmap.ic_launcher
        ).apply {
            into(v)
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var aniPos = -1
        fun bind(position: Int) {
            RvImgViewBinding.bind(itemView).run {
                val photo = array!![position]
                loadImgInto(photo.download_url!!, rvImg)

                rvImg.setOnClickListener {
                    val urlList = array!!.map {
                        it.download_url
                    }
                    PhotoDetailActivity.startThis(this@TGlideActivity, urlList , position)
                }

                rvImg.setOnLongClickListener {
                    if (aniPos != -1) return@setOnLongClickListener false
                    binding.prewImg.visibility = View.GONE
                    loadImgInto(photo.download_url!!, binding.prewImg)
                    aniPos = position
                    binding.prewImg.layoutParams.run {
                        width = root.width
                        height = root.height
                    }

                    binding.prewImg.x = root.left.toFloat()
                    binding.prewImg.y = root.top.toFloat()

                    binding.prewImg.visibility = View.VISIBLE
                    Log.d(
                        TAG,
                        "bind: left:${root.left} top:${root.top} right:${root.right} bottom:${root.bottom}"
                    )

                    val sc = binding.root.width / root.width.toFloat()
                    binding.prewImg.animate().apply {
                        scaleX(sc)
                        scaleY(sc)
                        translationX((binding.root.width - root.width) / 2f)
                        translationY((binding.root.height - root.height * sc) / 2f)

                        duration = 300
                        setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                root.postDelayed({
                                    resetAnim {
                                        aniPos = -1
                                        binding.prewImg.visibility = View.GONE
                                    }
                                }, 1500)
                            }
                        })
                    }


                    true
                }

            }
        }

        private fun resetAnim(cb: () -> Unit) {
            binding.prewImg.animate().apply {
                scaleX(1f)
                scaleY(1f)
                val view = binding.glideList.contentRv.findViewHolderForAdapterPosition(aniPos)?.itemView
                if(view != null){
                    translationX(view.left.toFloat())
                    translationY(view.top.toFloat())
                }
                duration = 200
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        cb()
                    }
                })
            }
        }
    }

    var mAdapter = object : RecyclerView.Adapter<VH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(layoutInflater.inflate(R.layout.rv_img_view, parent, false))
        }

        override fun getItemCount(): Int = if (array == null) 0 else array!!.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(position)
        }
    }


    var array: MutableList<GitPhoto>? = null
    val imgFormat = arrayListOf("jpg", "jpeg", "png", "webp", "gif", "tiff")
    val handler = Handler(Looper.getMainLooper()) { msg ->
        dialog?.cancel()
        array = convertJsonToObjOrArray<GitPhoto>(
            "${msg.obj}", object : TypeToken<Collection<GitPhoto>>() {}.type
        ).toMutableList()
        val iterator = array?.iterator()
        while (iterator!!.hasNext()) {
            val p = iterator.next()
            val suffix = p.download_url!!.substring(p.download_url!!.lastIndexOf('.') + 1)
            if (imgFormat.indexOf(suffix) == -1) {
                iterator.remove()
            } else
                p.download_url =  "https://cdn.staticaly.com/gh/jonesruner/j-photo-repo@main/${p.path}"
        }
        arrayListOf(
            "https://img.pigpig.in/imgapi.cn/%E4%B8%80%E5%B0%8F%E5%A4%AE%E6%B3%BD%20%E2%80%93%20%E7%B3%AF%E7%B1%B3%E5%85%83%E5%AE%B5/09.jpg",
            "https://imgapi.cn.senpian.com/imgapi.cn/5a12450fc1623.jpg",
            "https://cdn.seovx.com/img/mom19-12%20(54).jpg",
            "https://th.bing.com/th/id/R.c3f3699650f6385ee3ca3ef829238430?rik=5Hi6q%2fUTvUPEGg&riu=http%3a%2f%2fbbsimage.res.meizu.com%2fforum%2f201411%2f25%2f153444yyvrsbia7in8sf8q.jpg&ehk=R9hDyv%2b0dN9o%2fzZM8I8z%2f9e9o0VQBwqpPjInFhX48do%3d&risl=&pid=ImgRaw&r=0"
        ).forEach {
            array!!.add(GitPhoto("", "", "", "", it))
        }
        loadImgInto("https://cdn.seovx.com/img/mom19-12%20(54).jpg", binding.prewImg)
        binding.glideList.contentRv.adapter = mAdapter
        true
    }


    var dialog: Dialog? = null
    private fun sendRequest() {
        dialog = LoadingUtils.showLoading(this)
        thread {
            val request = Request.Builder()
                .url("https://api.github.com/repos/jonesruner/j-photo-repo/contents/imgs")
                .build()
            try {
                val response = OkHttpRequest.instance().newCall(request).execute()
                val ms = handler.obtainMessage()
                ms.obj = response.body()?.string()
                handler.sendMessage(ms)
            } catch (e: Exception) {
                e.printStackTrace()
                dialog?.cancel()
            }
        }
    }
}