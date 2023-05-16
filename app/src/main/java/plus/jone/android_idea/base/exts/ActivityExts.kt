package plus.jone.android_idea.base.exts

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat


fun ComponentActivity.requestPermissions(permissions:Array<String>,cb:(Map<String,Boolean>)->Unit){
    val perm = mutableListOf<String>()
    permissions.forEach {
        if (ActivityCompat.checkSelfPermission(this,it) != PackageManager.PERMISSION_GRANTED) {
            perm.add(it)
        }
    }

    if(perm.size == 0){
        cb(mapOf(Pair("all",true)))
        return
    }

    val getPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        cb(it)
    }
    getPermission.launch(perm.toTypedArray())
}