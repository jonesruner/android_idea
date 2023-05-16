# OkHTTP

## 1. 简单运行示例：

1. 新建 OkHttpClient
2. 创建 Request
3. 使用 **OkHttpClient** 创建 **Call**
4. 接收响应即可

```kotlin 
    class  OkHttpRequest(){
        companion object {
           private val INSTANCE : OkHttpClient by lazy {
                OkHttpClient.Builder()
                    .apply {
                        readTimeout(30,TimeUnit.SECONDS)
                        writeTimeout(30,TimeUnit.SECONDS)
                    }
                    .build()
            }
            fun instance() = INSTANCE
        }
    }

    private fun sendRequest() {
        thread {
            val request = Request.Builder()
                .url("${binding.okHttpInp.text}")
                .build()
            try {
                val response = OkHttpRequest.instance().newCall(request).execute()
                val ms = handler.obtainMessage()
                ms.obj = response.body()?.string()
                handler.sendMessage(ms)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    } 
```

