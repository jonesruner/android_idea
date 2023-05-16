package plus.jone.android_idea.okhttp

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class  OkHttpRequest  {
    companion object {
        private val INSTANCE : OkHttpClient by lazy {
            OkHttpClient.Builder()
                .apply {
                    readTimeout(30, TimeUnit.SECONDS)
                    writeTimeout(30, TimeUnit.SECONDS)
                }
                .build()
        }
        fun instance() = INSTANCE
    }
}
