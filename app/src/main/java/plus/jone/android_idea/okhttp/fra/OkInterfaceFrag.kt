package plus.jone.android_idea.okhttp.fra

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import plus.jone.android_idea.R
import plus.jone.android_idea.base.model.Res
import plus.jone.android_idea.base.utills.JsonUtils
import plus.jone.android_idea.databinding.RvOkInterfaceItemBinding
import plus.jone.android_idea.okhttp.TOkhttpActivity

class OkInterfaceFrag : Fragment() {
    private val recv by lazy {
        RecyclerView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recv.layoutManager = LinearLayoutManager(requireContext())
        return recv
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    var  res:Res ?= null

    inner class VH(view:View):RecyclerView.ViewHolder(view){
        fun bind(pos:Int){
            RvOkInterfaceItemBinding.bind(itemView).apply {
                rvTv.text = "${res!!.data!![pos]}"
            }
        }
    }

    private val onDataChanged = object : TOkhttpActivity.OnDataChanged{
        override fun onChanging(data: String?) {
        data?:return
            Log.d("TAG__OK_INTER_", "onChanging: ${data}")
           res = JsonUtils.convertJsonToObj(data, Res::class.java)
            recv.adapter = object : RecyclerView.Adapter<VH>(){
                override fun onBindViewHolder(holder: VH, position: Int) {
                    holder.bind(position)
                }
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                    return VH(layoutInflater.inflate(R.layout.rv_ok_interface_item,parent,false))
                }
                override fun getItemCount(): Int = if(res?.data?.size == null) 0 else res!!.data!!.size
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