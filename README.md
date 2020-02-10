### OKsimple：一个对okhttp进行二次封装的网络请求库，相比retrofit更简单易用，扩展性强，基于okhttp4.X版本和kotlin。
###### 目前实现的功能
- get，post，postjson，表单提交等常规请求
- 多文件下载，支持进度监听，支持断点续传
- 多文件上传，支持进度监听
- 支持接入glide实现glide图片加载进度监听
- 全局请求头和全局参数的添加
- 每次请求默认添加tag，支持自定义tag
- 每次请求支持自定义CacheControl
- 对短时间多次相同的请求进行了拦截处理，有效防止用户多次点击导致的重复请求
- 在没有网络的情况下，可以添加force cache
- 基于kotlin但也对java做了支持
- 同步okhttp最新版本，支持brotli compression

<br>

断点续传

<br>

![download](https://github.com/AllenXiao1994/OkSimple/blob/master/download.gif)

<br>

glide进度监听

<br>

![glide](https://github.com/AllenXiao1994/OkSimple/blob/master/glide.gif)

多文件下载

<br>

![multiple_download](https://github.com/AllenXiao1994/OkSimple/blob/master/multiple_download.gif)

<br>


## 项目介绍
基于okhttp的二次封装库。基于okhttp4.X和kotlin，目前更新到okttp4.3.1。设计之初有参考[okgo](https://github.com/jeasonlzy/okhttp-OkGo)，但和okgo对比的话，不管是设计思路还是最终实现的效果，都有很多不同的地方。觉得好用的话，给个star吧，也欢迎提issue，pr或者其他建议。

## minSdkVersion
Oksimple目前minsdkversion是21，一些用我这个lib朋友希望我能支持到19，我的想法是oksimple没有支持19的打算。首先okhttp 3.13以后的版本，对minsdkversion的版本要求都变成21了。原因的话，官方有给出解释，大意是说出于https安全方面的考虑，具体可以参考[这篇文章](https://medium.com/square-corner-blog/okhttp-3-13-requires-android-5-818bb78d07ce)。如果你还想兼容android 5.0以下的版本，可以使用okhttp的3.12.x版本。但oksimple目前是基于4.X开发，没有针对 okhttp 3.12版本重写的打算。这里说一点题外话，其实像微信的最新版，minsdkversion都变成21了。支持到19，我个人感觉没什么必要。


### 接入方法
在根节点的build.gradle
```
allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url  "https://dl.bintray.com/gateoftruth/public"
        }
    }
}
```
然后在app的build.gradle
```
implementation 'com.alen.simpleutil:OkSimple:1.2.4'
```
##### 或者你也可以fork一下，自己编译，oksimple只依赖了okhttp，没有其他依赖，编译出来的aar包也只有30几KB，同时，Oksimple采用api引入okhttp，所以你引入了oksimple的话，就不用重新引入okhttp了。

### 关于demo
所有get，post，postjson，文件上传下载等方法，都是经过测试可行的。但有些方法，不方便写测试用例，便写的随便了些，所以demo中的很多测试类是无法直接运行的，请结合自身项目进行测试。使用过程中有问题的可以先参考demo里的写法，或者提issue给我


### 使用方法
##### 1.全局初始化
oksimple 没有对okhttpclient进行任何的封装方法，基本上okhttpclient提供了接口的方法，我都没有进行二次封装，因为感觉没有必要，我只创建了一个默认的对象，就像这样
``` kotlin 
var okHttpClient = OkHttpClient()
```
所以如果你对okhttpclient有什么特殊处理，诸如connectTimeout，protocols，拦截器等，因为oksimple是基于饿汉模式的全局单例模式，建议在application中对其进行初始化，就像这样
``` kotlin
OkSimple.okHttpClient=OkHttpClient.Builder().addInterceptor(logInterceptor)
            .connectTimeout(100,TimeUnit.SECONDS)
            .writeTimeout(100,TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()
```
关于缓存，okhttp框架本身就对缓存做了相应处理，二次封装感觉实在没有必要。对缓存存在疑问的，可以看一下我翻译的这篇[文章](https://www.jianshu.com/p/ecf64bda343e)<br/>
但oksimple默认会在没有网络的情况下，添加force cache。也就是说你可以不用再写这样的拦截器了，因为这个功能已经实现了。
```kotlin
//不需要再写这样的拦截器，这个功能已经内置了
public class ForceCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (!NetworkUtils.internetAvailable()) {
            builder.cacheControl(CacheControl.FORCE_CACHE);
        }
        
        return chain.proceed(builder.build());
    }
}
okHttpClient.addInterceptor(new ForceCacheInterceptor());
```
要开启这个功能，需要在你的application里初始化oksimple用到的application
```kotlin
OkSimple.application=this//this指代你的application
```
默认添加force cache这个功能也是可以控制的
``` kotlin
OkSimple.networkUnavailableForceCache=true//默认为true,当为false时，则不会在断网的时候添加force cache。
```
如果你一开始就不打算使用这个功能，那么你就不要初始化oksimple用到的application。也就是说要开启在断网情况下添加force cache这个功能，需要满足两个条件，第一个是给oksimple用到的application赋值，第二个是networkUnavailableForceCache为true(虽然它本身默认就是true)。

全局参数的话，诸如各种渠道参数等，或者全局的header，诸如token等，可以分别调用
``` kotlin
OkSimple.addGlobalParams("key","value")
```
和
``` kotlin
OkSimple.addGlobalHeader("key","value")
```
你可以在任何地方调用这两个方法，都会生效，如果你想对这些参数进行一些操作，那么直接获取OkSimple.globalHeaderMap和OkSimple.globalParamsMap即可。

同时，Oksimple默认会防止重复请求，也就是当你向服务器发出一个请求的时候，如果服务器没有返回结果，无论这个结果是成功，失败或者超时，都会对之后的同一个请求进行拦截，之后的请求，不会发送到服务器，直到当前请求有了返回。而判断是否是同一个请求，是根据tag(tag的默认值是url)来进行判断的。防止重复请求的开关是
``` kotlin
OkSimple.preventContinuousRequests=true//默认为true,当为false时，则不会防止重复请求
```
当开关开启的时候，是可以有效防止诸如用户狂点按钮，导致短时间发送多个请求的情况发生的。<br/>
注意事项：
1. 1.2.3以前的版本，是根据url来进行判断的，这样的话，假如同时进行多个Post请求，url相同但参数不同，也会被拦截。所以在1.2.3版本改为使用tag进行判断。可以考虑给url相同，参数不同，但又需要同时请求的post请求设定不同的tag(tag有默认值，默认值是url)
2. 是否有正在进行中的请求是根据tag判断并且全局单例通过ConcurrentHashMap进行管理。虽然会在onFailure和onResponse中进行重置，但在实际使用中有这样一种情况：activity a 里有一个post请求设置了tag，然后有人比较懒，把activity a里的这个post请求整体复制到activity b里，只改了参数，没有改url和tag，这个时候就会有个问题，当我在activity a 里的那个post请求还没有请求完成的时候，跳转到activity b，会发现activity b 里的post请求没有执行。原因就在于tag是相同的。所以建议这种情况下，应该设置不同的tag。这里我放上一部分精简过后的源码方便大家理解。
``` kotlin 
       val status = OkSimple.statusUrlMap[tag] ?: false
       if (status) {
            return
        }
	 client.newCall(requestBuilder.build()).enqueue((object : Callback {
            override fun onFailure(call: Call, e: IOException) {
               ....
                OkSimple.statusUrlMap.remove(tag)
            }

            override fun onResponse(call: Call, response: Response) {
               ...
                OkSimple.statusUrlMap.remove(tag)
            }

        }))
```
如果你还是不能理解的话，可以考虑把OkSimple.preventContinuousRequests设为false

##### 2.ResultCallBack
在开始介绍get，post等请求前，需要先介绍一下ResultCallBack。在Oksimple中，所有的请求回调结果，都是基于ResultCallBack这个抽象类，这个抽象类定义了一系列的抽象方法，并且继承了BaseProgressListener接口实现上传和下载进度监听。不同的请求，需要回调不同的callback。
``` kotlin
abstract class ResultCallBack : BaseProgressListener {
    /**
     * 这些参数是文件下载用的
     */
    var filename = ""
    var filePath = ""
    var downloadLength=0L
    var contentLength=0L
    /**
     * 开始网络请求之前调用，在主线程回调，可以用来弹出dialog等
     */
    abstract fun start()

    /**
     * 和okhttp3.Callback.onResponse(call: Call, response: Response) 同步回调，用于获取请求的结果
     */
    abstract fun response(call: Call, response: Response)
	
    /**
     * 和okhttp3.Callback.onFailure(call: Call, e: IOException) 同步回调，用于接受异常，在主线程回调
     */
    abstract fun failure(call: Call, e: Exception)

    /**
     * 当responseBody 为空时回调 
     */
    abstract fun responseBodyGetNull(call: Call, response: Response)

    /**
     * 捕获诸如gson解析抛出的JsonSyntaxException之类的异常
     */
    abstract fun otherException(call: Call, response: Response, e: Exception)
    
}
```
而BaseProgressListener则没什么好说的了
```kotlin
interface BaseProgressListener {

    fun downloadProgress(url:String,total: Long, current: Long)

    fun downloadProgressOnMainThread(url:String,total: Long, current: Long)

    fun uploadProgress(fileName: String, total: Long, current: Long)

    fun uploadProgressOnMainThread(fileName: String, total: Long, current: Long)

}
```
因为我的下载和上传进度是参照[okhttp的官方sample](https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java)来写的，同时也是为了能接入glide监听图片的加载进度，所以默认的上传和下载的进度回调都是在子线程回调，但我们一般展示进度是在主线程，所以我又新增了两个主线程回调的方法。
目前内置了四个callback，涵盖了常用的大部分需求，这四个callback都继承自ResultCallBack，当然，你也可以根据需求重写
- DataCallBack，用于常get,post，文件上传等，需要获取服务器端返回json的都可以重写这个
- FileResultCallBack，用于文件下载，支持断点续传
- GlideCallBack，接入glide，获取glide加载进度
- BitmapResultCallBack，获取bitmap，支持下载进度，通常情况下用不到，因为一般不会直接下载图片，都是接入第三方框架，作为GlideCallBack的父类

##### 3.DataCallBack
DataCallBack继承自ResultCallBack并新增了三个方法
```kotlin

  /**
  *对传入responseBody.string()进行预处理，例如不想实体类太复杂的话，可以在这里使用JSONObject等对服务器返回的json进行成功或者失败判断的预处理
  */
open fun preProcessBodyString(bodyString:String):String

 /**
  * 需自己根据需要进行重写，传入preProcessBodyString()返回的数据，返回泛型参数。
  */
abstract fun stringToData(string:String):E
 
 
 /**
  * 最终返回结果，在主线程回调。
  */
abstract fun getData(data: E, rawBodyString: String, call: Call, response: Response)
```
这里我以gson为例子，重写了datacalllback，那么就会是这样
```kotlin
abstract class GsonCallBack<E> :DataCallBack<E>() {
    private val gson=Gson()
    override fun stringToData(string: String): E {
        val type=(javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        return gson.fromJson(string,type)
    }

}
```

重写的意义在于把服务器端返回的json数据，转化为泛型定义的实体类，这里你可以使用gson，fastjson。也可以不像我一样使用getGenericSuperclass方法，而改为直接传入一个class类，都是可以的，看自己的喜好。

以上面的GsonCallBack为例子，实际使用的话，get请求的kotlin版本参照下面的代码，而[post请求](https://github.com/AllenXiao1994/OkSimple/blob/master/app/src/main/java/com/alen/oksimpleutil/PostParamsActivity.kt)，[postJson](https://github.com/AllenXiao1994/OkSimple/blob/master/app/src/main/java/com/alen/oksimpleutil/PostJsonActivity.kt)，[表单提交](https://github.com/AllenXiao1994/OkSimple/blob/master/app/src/main/java/com/alen/oksimpleutil/PostFormActivity.kt)，可以点击链接查看
```kotlin
 OkSimple.get(url).apply {
            tag=xxx
            requestCacheControl= CacheControl.FORCE_CACHE
            params(key,value)
            addHeader(key,value)
        }.execute(object :GsonCallBack<T>(){
                override fun start() {
                    dialog.show()//重写start()，进行诸如弹出dialog的操作
                }
                override fun getData(
                    data: T,
                    rawBodyString: String,
                    call: Call,
                    response: Response
                ) {
                    dialog.dismiss()
                   
                }

                override fun failure(call: Call, e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }

            })
```
java的话可以这么写：
```java
OkSimple.INSTANCE.get(url).setTheTag(xxx).addHeader(key,value).setTheRequestCacheControl(CacheControl.FORCE_CACHE).params(key,value).execute(new GsonCallBack<T>() {
            @Override
            public void failure(@NotNull Call call, @NotNull Exception e) {

            }

            @Override
            public void getData(T s, @NotNull String rawBodyString, @NotNull Call call, @NotNull Response response) {
                
            }
        });
```

##### 怎么写的话看个人喜好，这里说一下几个链式调用的方法，addHeader我想就不用多说了，requestCacheControl是okhttp.Request类自带的一个方法，用于支持缓存控制，具体可以参考okhttp的官方文档，而okhttp的全局缓存策略的话，可以在初始化的时候通过自定义okhttpclient传入。tag的话，默认是url，用于支持取消请求。params的话，默认会拼接在url之后，post请求也可以调用。OkSimple里的所有请求，在发起的时候，都可以带上这几个参数，后面的请求，我就不再重复介绍了。

##### 4.DataCallBack文件上传
通常文件上传之后，服务器端也会返回json，告诉你上传成功还是失败。所以这里依然是使用DataCallBack。为了方便介绍，我这里还是以GsonCallBack为例子。
```kotlin
OkSimple.postForm(url).addFormPart(key,file,mediaTypeString).addFormPart(key,value).execute(object :GsonCallBack<T>(){
            override fun getData(
                data: T,
                rawBodyString: String,
                call: Call,
                response: Response
            ) {
                
            }

            override fun failure(call: Call, e: Exception) {
                e.printStackTrace()
            }

            override fun uploadProgressOnMainThread(fileName: String, total: Long, current: Long) {
               //更新progresbar等ui控件在这里更新
            }
	    
	    override fun uploadProgress(fileName: String, total: Long, current: Long) {
                super.uploadProgress(fileName, total, current)
            }

        })
```

通常情况下来说，表单提交的文件上传这样就可以了。Oksimple在使用postForm进行文件上传的时候，是以表单形式提交，同时也支持一个key一个file和一个key多个file。这里先说一下mediaTypeString这个参数，这个参数是string类型，不是必传的，是可选参数，默认值是application/octet-stream，但比如上传图片的时候，有的服务器不认application/octet-stream，只认"image/jpg"，那么这个时候,那么你就把服务器需要的mediatype传过去就行了。然后是fun uploadProgress(fileName: String, total: Long, current: Long)这个方法，因为我重写了RequestBody，所以uploadProgress默认会在子线程被okhttp回调，并且这个方法如果你点进去，看到父类实现的话，是这样实现的
```kotlin
 fun uploadProgress(fileName: String, total: Long, current: Long) {
        OkSimple.mainHandler.post {
            uploadProgressOnMainThread(fileName, total, current)
        }
    }
```
所以，如果你想在自己的handler处理子线程和主线程的通讯，那么你可以把super.uploadProgress(fileName, total, current)这句删掉，同时uploadProgressOnMainThread也就不会被回调了。还有就是有的服务器上传单个文件不是使用表单形式，是使用post形式，没有key值，那么你可以这么写
```kotlin
OkSimple.uploadFile(url,file,mediaTypeString).execute(object :GsonCallBack<T>(){
            override fun getData(
                data: T,
                rawBodyString: String,
                call: Call,
                response: Response
            ) {

            }

            override fun failure(call: Call, e: Exception) {

            }

            override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {

            }

            override fun downloadProgress(url: String, total: Long, current: Long) {
                super.downloadProgress(url, total, current)
            }

        })

```

##### 5.FileResultCallBack文件下载
```kotlin
 OkSimple.downloadFile(url,filename,filepath).execute(object :FileResultCallBack(){
            override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {

            }

            override fun downloadProgress(url: String, total: Long, current: Long) {
                super.downloadProgress(url, total, current)
            }

            override fun failure(call: Call, e: Exception) {

            }

            override fun finish(file: File) {
               
            }

        })
```
通过如上代码，便可完成文件下载，下载完的文件，会在finish()方法里回调，finish是FileResultCallBack继承ResultCallBack后新增的方法，用于获取下载完成的文件。Oksimple默认支持断点续传，假如你的服务器不支持断点续传，也可照常下载。如果你不想断点续传，想重新下载，请在下载前把存在的文件删除。在okhttp的逻辑里，是没有断点续传的概念的，只有通过tag取消一个请求，然后再次请求的概念。oksimple默认给每一个请求一个和url一样的tag，也可以自定义tag，具体可以参考demo。downloadProgressOnMainThread和downloadProgress与之前文件上传的uploadProgressOnMainThread和uploadProgress同理。因为我重写了ResponseBody，所以okhttp默认会在子线程回调downloadProgress，如果你想自己控制主线程子线程的切换，同样可以删掉 super.downloadProgress(url, total, current)这句代码即可。

##### 6.FileResultCallBack多文件下载
多文件下载其实就是多次调用下载方法，但有些许不同，具体可以参考demo里的[MultipleDownloadActivity](https://github.com/AllenXiao1994/OkSimple/blob/master/app/src/main/java/com/alen/oksimpleutil/MultipleDownloadActivity.kt)。demo里没做到诸如各大应用商店里那样的自动下载，但这确实是可以实现的，你可以考虑接入sqlite或者handler等来进行实现。我没有打算做一个专门的下载框架，因为就以往的开发过程中，对多文件下载的需求总是相当复杂的，要考虑的很多，诸如断网，进程杀死等等。很少有一个框架能满足各种UI和功能的需求。所以oksimple也只是提供了多文件下载的能力，具体实现需要自己编码处理。

##### 7.关于多线程分段下载
有用我这个框架的一些朋友让我像别的下载框架一样，加上多线程分段下载。我感到很奇怪，问他用这个功能干嘛。他说他的项目里，需要从服务器下载一些资源文件或者安装包，分段下载可能会快一些。我说如果你用Oksimple只是从你服务器上下载东西，那多线程分段下载其实完全没有必要。因为你服务器的带宽是固定的，在单线程情况下，假设100M带宽，10个人下，每个人分到1M。假设现在每个人都开了两个线程下载，那么100M带宽，假设依然保证每个人1M的下载速率，那么只够分5个人。你是愿意10个人，每个人都慢一点，还是愿意只有五个人能下，每个人能快一点？回到多线程，我认为移动端进行自己服务器的多线程下载，很多时候是没有必要的。多线程分段下载适合迅雷这样的，通过稳定的宽带从非自己服务器下载。但Oksimple本来就是基于android这样的移动平台，不适合做成迅雷这样的。所以我并有在Oksimple里加入多线程分段下载。

##### 8.GlideCallBack进行glide图片加载进度监听
如果你看源代码的话，你会发现GlideCallBack是一个空方法。这是因为Oksimple本质是一个网络请求的框架， 不会考虑引入glide等其他库，所以我提供的只是接入glide的能力。因此我把它放在了demo里而不是lib里，如果你想实现demo里那样的效果的话，我会提供一个很简单的思路。当然你也可以直接去看源码。
首先你需要引入glide相关的库
```kotlin
 implementation 'com.github.bumptech.glide:glide:4.9.0'
 implementation 'com.github.bumptech.glide:annotations:4.9.0'
 kapt 'com.github.bumptech.glide:compiler:4.9.0'//kotlin 使用kapt
 implementation 'com.github.bumptech.glide:okhttp3-integration:4.9.0'//这是glide对okhttp的支持库，必须要引入
```
之后创建一个类，继承自GlideCallBack，类似这样的
```kotlin
class MyGlideCallBack: GlideCallBack() {
    val urlProgressListener= hashMapOf<String,ImageProgress>()
    companion object{
        val instance=MyGlideCallBack()
    }

    override fun downloadProgress(url: String, total: Long, current: Long) {
        OkSimple.mainHandler.post {
            downloadProgressOnMainThread(url, total, current)
        }
    }

    override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {
        urlProgressListener[url]?.downloadProgress( total, current)
        if (total==current){
            urlProgressListener.remove(url)
        }
    }
}
```
然后创建glidemodule
```kotlin
@GlideModule
class GlideModule :AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.replace(GlideUrl::class.java,InputStream::class.java,OkHttpUrlLoader.Factory(OkSimple.getGlideClient(MyGlideCallBack.instance)))
    }
}
```
<br>

这里需要注意的是，当你调用 registry.replace(GlideUrl::class.java,InputStream::class.java,OkHttpUrlLoader.Factory(OkSimple.getGlideClient(MyGlideCallBack.instance)))方法的时候，你对okhttpclient进行的诸如readTimeout()或者 protocols()等初始化会在glide通过okhttp进行访问的时候保留下来。因为都是同一个okhttpclient。但header，globeheader和globeparams等不会进行保留。因为glide并没有使用我的request。

<br>
           

之后创建一个自定义viewgroup
```kotlin
class GlideProgressImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr) {
    constructor(context: Context,attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)


    private val baseView:View = LayoutInflater.from(context).inflate(R.layout.view_glide_progress,this,true)
    private val imageView=findViewById<ImageView>(R.id.iv_for_glide)
    private val progress=findViewById<ProgressBar>(R.id.progress_glide)

    fun<T> into(url:String,requestBuilder:RequestBuilder<T>){
        requestBuilder.into(imageView)
        MyGlideCallBack.instance.urlProgressListener[url]=object :ImageProgress{
            override fun downloadProgress(total: Long, current: Long) {
                val totalDouble=total.toDouble()
                var percent=current/totalDouble
                percent*=100
                progress.progress=percent.toInt()

            }

        }
    }

}
```
使用的时候这样使用
```kotlin
class GlideTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide_test)
        val url="https://images.pexels.com/photos/1837591/pexels-photo-1837591.jpeg?cs=srgb&dl=architecture-big-ben-bird-s-eye-view-1837591.jpg&fm=jpg"
        val url2="https://images.pexels.com/photos/1414050/pexels-photo-1414050.jpeg?cs=srgb&dl=architecture-big-wheel-buoy-1414050.jpg&fm=jpg"
        val options=RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
        glideProgressImageView.into(url,GlideApp.with(this).load(url).apply(options))
        glideProgressImageView2.into(url2,GlideApp.with(this).load(url2).apply(options))

    }
}
```


[view_glide_progress](https://github.com/AllenXiao1994/OkSimple/blob/master/app/src/main/res/layout/view_glide_progress.xml)和[activity_glide_test](https://github.com/AllenXiao1994/OkSimple/blob/master/app/src/main/res/layout/activity_glide_test.xml)请点击查看
。详细的代码都在demo里，我在这里说一下实现glide进度监听的原理。要实现glide进度监听，首先就是要在AppGlideModule里调用Registry.replace，把glide原来使用的httpurlconnection替换为okhttp，在替换的时候，需要传入一个callback统一接收所有url的回调，这里我继承我的GlideCallBack，在回调的时候通过url进行区分。剩下的就很简单了，每次glide 加载前，传入一个url和listener到MyGlideCallBack中，这样，就完成了glide的进度监听。至于具体使用过程中，如何封装，我的demo也仅供参考。


### 联系方式
QQ：1572777804（注明来自github）

