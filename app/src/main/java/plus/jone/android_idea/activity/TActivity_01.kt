package plus.jone.android_idea.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.lzf.easyfloat.EasyFloat
import plus.jone.android_idea.R
import plus.jone.android_idea.databinding.ActivityTactivity01Binding

class TActivity_01 : AppCompatActivity() {
    private val TAG = "TActivity_01_"
    private val binding by lazy {
        ActivityTactivity01Binding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ShowFloatView.show(this,ShowFloatView.listFloatView(this))


//        Thread.sleep(1000)
        Log.d(TAG, "activity 01 onCreate has been called..... ")
        addMsg("activity 01 onCreate has been called..... ")
        binding.mt01.setOnClickListener {
            startActivity(Intent(this,TActivity_02::class.java))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addMsg(msg: String){
        ShowFloatView.addMsg(msg){
//            (EasyFloat.getFloatView() as RecyclerView).adapter?.notifyDataSetChanged()
        }
    }


    override fun onPause() {
        super.onPause()
        addMsg("activity 01 onPause has been called..... ")
        Log.d(TAG, "activity 01 onPause has been called..... ")
    }


    override fun onStop() {
        super.onStop()
        addMsg("activity 01 onStop has been called..... ")
        Log.d(TAG, "activity 01 onStop has been called..... ")
    }


    override fun onDestroy() {
        super.onDestroy()
        addMsg("activity 01 onDestroy has been called..... ")
        Log.d(TAG, "activity 01 onDestroy has been called..... ")
    }


    override fun onRestart() {
        super.onRestart()
        addMsg("activity 01 onRestart has been called..... ")
        Log.d(TAG, "activity 01 onRestart has been called..... ")
    }


    override fun onStart() {
        super.onStart()
        addMsg("activity 01 onStart has been called..... ")
        Log.d(TAG, "activity 01 onStart has been called..... ")
    }

    override fun onResume() {
        super.onResume()
        addMsg("activity 01 onResume has been called..... ")
        Log.d(TAG, "activity 01 onResume has been called..... ")
    }



}