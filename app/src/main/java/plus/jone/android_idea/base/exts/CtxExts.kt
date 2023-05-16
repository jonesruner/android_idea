package plus.jone.android_idea.base.exts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat


fun Context.showToast(msg:String)
{
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.hasPermission(permission:String):Boolean{
    return ActivityCompat.checkSelfPermission( this,  permission ) == PackageManager.PERMISSION_GRANTED
}
