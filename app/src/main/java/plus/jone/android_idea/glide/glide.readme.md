## Glide

## 1.  dependency

```groovy
implementation "com.github.bumptech.glide:glide:4.15.1"
```

## 2. Quickly Start

```kotlin
 Glide.with(this@TGlideActivity)
.load(url)
.placeholder(R.mipmap.ic_launcher)
.error(R.mipmap.ic_launcher)
.into(v)
```



## 