package plus.jone.android_idea.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.lzf.easyfloat.EasyFloat
import plus.jone.android_idea.R
import plus.jone.android_idea.databinding.ActivityTactivity02Binding

class TActivity_02 : AppCompatActivity() {
    private val TAG = "TActivity_02_"
    private val binding by lazy {
        ActivityTactivity02Binding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d(TAG, "activity 02 onCreate has been called..... ")

        binding.mt02.setOnClickListener {
            finish()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun addMsg(msg: String){
        ShowFloatView.addMsg(msg){
        }
    }


    override fun onPause() {
        super.onPause()
        addMsg("activity 02 onPause has been called..... ")
        Log.d(TAG, "activity 02 onPause has been called..... ")
    }


    override fun onStop() {
        super.onStop()
        addMsg("activity 02 onStop has been called..... ")
        Log.d(TAG, "activity 02 onStop has been called..... ")
    }


    override fun onDestroy() {
        super.onDestroy()
        addMsg("activity 02 onDestroy has been called..... ")
        Log.d(TAG, "activity 02 onDestroy has been called..... ")
    }


    override fun onRestart() {
        super.onRestart()
        addMsg("activity 02 onRestart has been called..... ")
        Log.d(TAG, "activity 02 onRestart has been called..... ")
    }


    override fun onStart() {
        super.onStart()
        addMsg("activity 02 onStart has been called..... ")
        Log.d(TAG, "activity 02 onStart has been called..... ")
    }

    override fun onResume() {
        super.onResume()
        addMsg("activity 02 onResume has been called..... ")
        Log.d(TAG, "activity 02 onResume has been called..... ")
    }
}