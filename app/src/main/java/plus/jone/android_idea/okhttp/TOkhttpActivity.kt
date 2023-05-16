package plus.jone.android_idea.okhttp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import okhttp3.Request
import plus.jone.android_idea.R
import plus.jone.android_idea.base.utills.JsonUtils
import plus.jone.android_idea.base.utills.LoadingUtils
import plus.jone.android_idea.databinding.ActivityTokhttpBinding
import plus.jone.android_idea.okhttp.fra.OkInterfaceFrag
import plus.jone.android_idea.okhttp.fra.OkJSONFrag
import kotlin.concurrent.thread

class TOkhttpActivity : AppCompatActivity(), MenuProvider {

    interface OnDataChanged {
        fun onChanging(data: String?)
    }

    private var data: String? = null
    private var onDataChanged: MutableList<OnDataChanged> = mutableListOf()
    fun setOnDataChanged(dataChange: OnDataChanged?) {
        dataChange ?: return
        this.onDataChanged.add(dataChange)
    }

    fun unRegisterOnDataChanged(dataChange: OnDataChanged?) {
        dataChange ?: return
        this.onDataChanged.remove(dataChange)
    }

    private val binding: ActivityTokhttpBinding by lazy {
        ActivityTokhttpBinding.inflate(layoutInflater)
    }

    val handler = Handler(Looper.getMainLooper()) { msg ->
        onDataChanged.forEach {
            it.onChanging("${msg.obj}")
        }
//        binding.toContent.text = JsonUtils.prettyJson("${msg.obj}")
//        dialog?.cancel()
        true
    }


    private val fraArray = listOf(OkInterfaceFrag(),OkJSONFrag())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            fraArray.forEach {
                add(R.id.ok_http_fag_container,it)
            }
            show(fraArray[0])
            hide(fraArray[1])
            commit()
        }


        binding.okHttpBtnSend.setOnClickListener {
            sendRequest()
        }
        addMenuProvider(this)
    }

    var dialog: Dialog? = null
    private fun sendRequest() {
        dialog = LoadingUtils.showLoading(this)
        thread {
            val request = Request.Builder()
                .url("${binding.okHttpInp.text}")
                .build()
            try {
                val response = OkHttpRequest.instance().newCall(request).execute()
                val ms = handler.obtainMessage()
                ms.obj = response.body()?.string()
                handler.sendMessage(ms)
            } catch (e: Exception) {
                e.printStackTrace()
            }finally {
                dialog?.cancel()
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.okhttp_toggle_ui, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.ok_json -> {
                toggle(1)
            }
            R.id.ok_inter_ -> {
                toggle(0)
            }
        }
        return true
    }

    fun toggle(pos:Int){
        supportFragmentManager.beginTransaction().apply {
            val fragment = fraArray[pos]
            if (!fragment.isAdded) {
                add(R.id.ok_http_fag_container,fragment)
            }
            show(fragment)
            hide(fraArray[if(pos == 0 ) 1 else 0])
            commit()
        }
    }

}