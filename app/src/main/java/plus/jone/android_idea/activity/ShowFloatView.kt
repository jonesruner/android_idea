package plus.jone.android_idea.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.textview.MaterialTextView
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.anim.DefaultAnimator
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import plus.jone.android_idea.R

object ShowFloatView {

    @SuppressLint("StaticFieldLeak")
    private var floatView: EasyFloat.Builder? = null

    fun show(context: Context, viewLayout: View) {
        if (floatView != null) {
            EasyFloat.dismiss()
        }
        floatView = EasyFloat.with(context)
            // 设置浮窗xml布局文件/自定义View，并可设置详细信息
            .setLayout(viewLayout) { }
            // 设置浮窗显示类型，默认只在当前Activity显示，可选一直显示、仅前台显示
            .setShowPattern(ShowPattern.ALL_TIME)
            // 设置吸附方式，共15种模式，详情参考SidePattern
            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
            // 设置浮窗的标签，用于区分多个浮窗
            .setTag("testFloat")
            // 设置浮窗是否可拖拽
            .setDragEnable(true)
            // 浮窗是否包含EditText，默认不包含
            .hasEditText(false)
            // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
            .setLocation(100, 200)
            // 设置浮窗的对齐方式和坐标偏移量
            .setGravity(Gravity.END or Gravity.CENTER_VERTICAL, 0, 200)
            // 设置当布局大小变化后，整体view的位置对齐方式
            .setLayoutChangedGravity(Gravity.END)
            // 设置拖拽边界值
            .setBorder(100, 100, 800, 800)
            // 设置宽高是否充满父布局，直接在xml设置match_parent属性无效
            .setMatchParent(widthMatch = false, heightMatch = false)
            // 设置浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
            .setAnimator(DefaultAnimator())

        // 浮窗的一些状态回调，如：创建结果、显示、隐藏、销毁、touchEvent、拖拽过程、拖拽结束。
        // ps：通过Kotlin DSL实现的回调，可以按需复写方法，用到哪个写哪个
        // 创建浮窗（这是关键哦😂）

        floatView?.show()
    }

    fun dismiss() {
        EasyFloat.dismiss("testFloat")
        msgList.clear()
    }

    private var msgList = mutableListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun addMsg(msg: String, cb: () -> Unit) {
        if(EasyFloat.getFloatView("testFloat") == null)return
        msgList.add(msg)
        (EasyFloat.getFloatView("testFloat") as RecyclerView).adapter?.notifyDataSetChanged()
        cb()
    }


    fun listFloatView(context: Context): RecyclerView {
        msgList.clear()
        val view = RecyclerView(context.applicationContext)
        view.tag = "recv"
        view.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        view.layoutManager = LinearLayoutManager(context.applicationContext)
        view.adapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return object : ViewHolder(MaterialTextView(context.applicationContext).apply {
                    tag = "text"
                }) {}
            }

            override fun getItemCount(): Int = msgList.size

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.itemView.findViewWithTag<MaterialTextView>("text").run {
                    text = msgList[position]
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    setPadding(5, 5, 5, 5)
                    setOnLongClickListener {
                        dismiss()
                        false
                    }
                }
            }
        }
        return view
    }
}