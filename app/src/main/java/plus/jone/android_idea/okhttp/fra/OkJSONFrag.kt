package plus.jone.android_idea.okhttp.fra

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import com.yuyh.jsonviewer.library.JsonRecyclerView
import plus.jone.android_idea.okhttp.TOkhttpActivity

class OkJSONFrag : Fragment() {
    private val VIEW_TAG = "TAG_JSON_VIEW"
    private val tv by lazy {
        JsonRecyclerView(requireContext()).apply {
            tag = VIEW_TAG
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return tv
    }

    private val onDataChanged = object : TOkhttpActivity.OnDataChanged {
        override fun onChanging(data: String?) {
            data ?: return
            Log.d("TAG__OK_JSON_", "onChanging: ${data}")
            tv.findViewWithTag<JsonRecyclerView>(VIEW_TAG).run {
                bindJson(data)
                expandAll()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as TOkhttpActivity).setOnDataChanged(onDataChanged)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as TOkhttpActivity).unRegisterOnDataChanged(onDataChanged)
    }
}