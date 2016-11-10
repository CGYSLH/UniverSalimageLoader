package com.mi.universalimageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    @BindView(R.id.but_normal)
    Button butNormal;
    @BindView(R.id.iv)
    ImageView iv;
    private ImageLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.but_normal)//基础的图片显示
    void but_normal() {
        //1初始化用于加载图片的imageLoader对象
        loader = ImageLoader.getInstance();
        //2通过init方法初始化框架
        loader.init(ImageLoaderConfiguration.createDefault(this));//必须进行性初始化不然就报异常这个方法的作用是配置框架的信息 如设置只在本地存储 不在内存存储等的设置
        //通过displayImage方法直接设置图片显示即可 此方法自带缓存处理
        loader.displayImage("http://ossweb-img.qq.com/upload/adw/image/201611/1478368739745917250.jpg",//传递图片的网址
            iv    //显示图片下载结果的控件的对象
        );
    }

    @OnClick(R.id.but_display)//设置图片显示的配置的图片显示
    void but_display()
    {
        //设置图片显示的相关的配置
        //1 初始化用于加载图片显示的imageloader对象
        loader=ImageLoader.getInstance();
        //2通过init方法初始化框架
        //参数 代表的是配置的信息 如设置只在背地存储 不在内存存储等
        loader.init(ImageLoaderConfiguration.createDefault(this));//这个方法同常情况下创建一次即可

        //准备DiaplayiamgeOptions的对象
        //准备的方式一 使用默认的配置
//        DisplayImageOptions opts=DisplayImageOptions.createSimple();
        //准备的方式二 自定义配置信息
        DisplayImageOptions opts=new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.ARGB_8888)//设置图片的编码的格式
                .showImageOnLoading(R.mipmap.ic_launcher)//设置图片联网加载过程中Imageview显示的临时图片
                .showImageOnFail(R.mipmap.ic_launcher)//设置图片加载失败时显示的图片
                .cacheInMemory(true)//图片下载后是否要存储在内存中
                .cacheOnDisk(true)//图片下载后是否保存在本地
                .delayBeforeLoading(1000) //设置等待参数中指定的时间再加载图片
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT) //设置图片缩放的模式 当前的模式为 将原图缩放图片后的图片比较
                //将相对较小的图片保存到内存中
                .resetViewBeforeLoading(true)//每次重新下载时候 是否将原有的数据清空在进行下载
       //        .displayer(new CircleBitmapDisplayer(Color.RED,20))//设置图片以圆角进行显示
                .displayer(new RoundedBitmapDisplayer(60))//网上找得说的是必须在xml的文件中设置图片的宽和高
                .build();
        //4通过displayimage的方法显示图片
        loader.displayImage("http://game.gtimg.cn/images/yxzj/cp/a20160422lbpc/zy-yxbz.jpg",//传递图片的网址
                iv,//显示图片下载结果的控件的对象
                opts//DisplayImageOptions对象 用于设置图片显示的相关的配置
        );
    }

    @OnClick(R.id.but_conf)//ImageloaderConfiguration的相关的配置缓存的策略
    void but_conf() {
        //1 初始化用于加载图片显示的imageloader对象
        loader=ImageLoader.getInstance();

        //2自定义ImageLoaderConfiguration的对象
        ImageLoaderConfiguration conf=
                new ImageLoaderConfiguration.Builder(this)
                .diskCache(new LimitedAgeDiskCache(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM"),24*60*60))//设置本地的图片存储的策略
                .diskCacheExtraOptions(300,300,null)//设置图片在本地种存储的最大的图片的大小

//                .diskCacheFileCount(100)//设置本地存储中最多存储的文件的个数
                .diskCacheSize(100*1024)//设置本地存储中存储的最大的大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//设置图片的命名的方式  这种方式生成的不是标准的图片文件
//                 .memoryCache(new LruMemoryCache((int) (Runtime.getRuntime().maxMemory()/8)))//设置内存的缓存的策略
//                .memoryCacheExtraOptions(300,300)//图片存储在内存中的最大的宽和高
//                        .memoryCacheSize()//设置内存 缓存的最大大小
//                        .memoryCacheSizePercentage()//设置内存缓存的最大大小相对于所有的内存的百分比
                        .threadPoolSize(3)//设置可以同时下载多少张图片
                .build();
        if (loader.isInited()) {
                        loader.destroy();//这个方法 是销毁之前创建的ImageLoader的对象因为通常情况下我们只需要创建一次就好了
        }

        //如果我们设置了上面的话 会发现图片保存不到本地这个是因为我们没有设置cacheOnDisk
        DisplayImageOptions optss=
                new DisplayImageOptions.Builder()
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(20)).build();

        loader.init(conf);//这个方法同常情况下创建一次即可
        loader.displayImage("http://game.gtimg.cn/images/yxzj/cp/a20160422lbpc/zy-yxbz.jpg",
                iv,optss,animateFirstListener);//增加动画加载的效果

    }
    @OnClick(R.id.but_downonly) void downonly() {//仅仅是下载图片的
        //1 初始化用于加载图片显示的imageloader对象
        loader=ImageLoader.getInstance();
        loader.init(ImageLoaderConfiguration.createDefault(this));
        //loader中的补充的方法
        //此方法纯粹用于加载图片
        loader.loadImage(""//图片下载的网址的
                , new ImageSize(400, 400)//下载的图片的宽和高
                , DisplayImageOptions.createSimple()//图片显示的配置对象
                , new ImageLoadingListener() {
                    //当图片开始下载后调用的方法
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    //            图片下载失败调用的方法
                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    //            图片下载完成调用的方法
                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    }

                    //            当图片下载取消的时候调用的方法
                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                }
                , new ImageLoadingProgressListener() {//图片下载进度改变的时候调用的方法
                    @Override
                    public void onProgressUpdate(String s, View view, int i, int i1) {

                    }
                }
        );
//        loader.setDefaultLoadingListener();单独设置ImageLoadingLister的方法
        loader.clearDiskCache();//清除本地缓存
        loader.clearMemoryCache();//清除内存的缓存
        loader.getMemoryCache();//得到内存的缓存
        loader.getDiskCache();//得到本地的缓存
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);//淡入点阵显示器 增加淡入的效果
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
