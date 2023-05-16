package plus.jone.android_idea.base.utills
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.concurrent.Executors
import kotlin.concurrent.thread

object NetworkUtils {
    private val single = Executors.newSingleThreadExecutor()
    fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && "${inetAddress.hostAddress} ".trim().indexOf(':')< 0) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }

    var localDevicesName  = ""
    fun getNetName() {
        if(localDevicesName.isNullOrEmpty())
        {
            thread {
                localDevicesName =  "${getLocalIpAddress()}"
            }
        }
    }
}