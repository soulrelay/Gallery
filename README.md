## <font color=#C4573C size=5 face="黑体">前言</font>

之前写了一篇[图片加载库的封装案例](https://github.com/soulrelay/ImageLoaderUtil)，其中基于Glide完成了图片加载库[ImageLoaderUtil](https://github.com/soulrelay/ImageLoaderUtil)的封装，ImageLoaderUtil提供了诸多图片加载相关的接口：
>* 正常加载图片
>* 针对于GIF图片的特殊加载
>* 加载图片的进度回调
>* 清除缓存
>* 获取缓存大小
>* 图片本地保存
>* 进度回调和本地保存均支持GIF


经过一段时间的检验，[ImageLoaderUtil](https://github.com/soulrelay/ImageLoaderUtil)在实际项目中表现良好，并且随着各种问题的暴露、跟进、解决，ImageLoaderUtil也逐渐趋于完善，能够满足项目中大部分关于图片加载的需求，之前的ImageLoaderUtil只是给出了一个很简单的案例来证明这套图片加载库可用，并没有很全面地呈现ImageLoaderUtil真正的价值，因此，我准备基于ImageLoaderUtil来实现一个图集功能，一个大部分APP中都会呈现的一个功能
## <font color=#C4573C size=5 face="黑体">图集功能简介</font>
一般图集的入口是这样的：（不过这不是重点，我们主要实现点击进去的图集详情页）
<center>
![这里写图片描述](http://img.blog.csdn.net/20170216153433383?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvczAwMzYwM3U=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
</center>
点击进入是这样的：
<center>
![这里写图片描述](http://img.blog.csdn.net/20170215200838740?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvczAwMzYwM3U=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
</center>
不要在意数据的不匹配，数据都是随便搞的，哈哈哈

言归正传！
来捋一下一个图集详情页应有的基本功能：
>* 支持图片的手势滑动（ViewPager），多点触控放大缩小（PhotoView）
>* 伴随图片切换的标题简介切换
>* 图集的保存、分享（暂无）、评论（暂无）、收藏（暂无）
>* 当前图集浏览完毕，支持切换到相关的图集推荐（目前很简陋）

## <font color=#C4573C size=5 face="黑体">部分功能点分析</font>

### <font color=#C4573C size=4 face="黑体">Gallery中的页面UI结构</font>

<center>
![这里写图片描述](http://img.blog.csdn.net/20170216155931486?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvczAwMzYwM3U=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
</center>
[参考源码，了解更多](https://github.com/soulrelay/Gallery)
### <font color=#C4573C size=4 face="黑体">通过ImageLoaderUtil提供的loadImageWithProgress实现图片加载的进度回调</font>

```
    private void showImage(final View retryView, ImageItem item, final PhotoView photoView, final TextView progressStr, final View progressLayout) {
        if (item == null) {
            return;
        }
        String url = item.getImage();
        ImageLoaderUtil.getInstance().loadImageWithProgress(url, photoView, new ProgressLoadListener() {
            @Override
            public void update(int bytesRead, int contentLength) {
                progressStr.setText(bytesRead * 100 / contentLength + "%");
            }

            @Override
            public void onException() {
                retryView.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void onResourceReady() {
                progressLayout.setVisibility(View.GONE);
            }
        });
    }
```
进度加载效果图如下：
<center>
![这里写图片描述](http://img.blog.csdn.net/20170215200859084?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvczAwMzYwM3U=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
</center>
### <font color=#C4573C size=4 face="黑体">通过ImageLoaderUtil提供的saveImage实现图片加载的本地保存</font>

```
   private void saveImage() {
        if (list == null) {
            ToastUtils.toastCenter(getActivity(), R.string.save_image_fail);
            return;
        }
        final String url = list.get(viewPager.getCurrentItem()).getImage();
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                ImageLoaderUtil.getInstance().saveImage(getActivity(), url,
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/gallery",
                        "pic" + System.currentTimeMillis(), new ImageSaveListener() {
                            @Override
                            public void onSaveSuccess() {
                                handler.obtainMessage(MSG_PIC_SAVE_SUCC).sendToTarget();
                            }

                            @Override
                            public void onSaveFail() {
                                handler.obtainMessage(MSG_PIC_SAVE_FAIL).sendToTarget();
                            }
                        });
            }
        });
    }
```
效果图如下：
<center>
![这里写图片描述](http://img.blog.csdn.net/20170215200919928?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvczAwMzYwM3U=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
</center>
### <font color=#C4573C size=4 face="黑体"> Android 六点零 运行时权限处理</font>

上面说到图片的保存功能，自然需要获得内存卡的读写权限，在API23+以上（targetSdkVersion设置到23或者以上时），不止要在AndroidManifest.xml里面添加权限
```
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
还需要在需要改权限的地方主动调用（详情请参考GalleryFragment，同时需要注意Fragment与Activity在申请运行时权限的不同，Activity中使用ActivityCompat.requestPermissions，而Fragment使用自己本身，如GalleryFragment.this.requestPermissions）
否则会报（java.io.FileNotFoundException:open failed: EACCES (Permission denied)）

上图即是在targetSdkVersion设置到23，在6.0系统的手机上运行，弹出授权dialog，只有允许才能保存图片成功，否在会报权限拒绝

参考代码：
```
    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            GalleryFragment.this.requestPermissions(PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        } else {
            saveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                // Permission Denied
                ToastUtils.toastCenter(getActivity(), R.string.permission_denied);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
```

### <font color=#C4573C size=4 face="黑体"> ViewPaper嵌套相关处理</font>
ViewPaper嵌套使用时，当里面的viewpaper未滑动到最后一个时，
外面的viewpaper禁止滑动
```
public class MyViewPager extends ViewPager {

    private OnNeedScrollListener mOnNeedScrollListener;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnNeedScrollListener(OnNeedScrollListener listener){
        this.mOnNeedScrollListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mOnNeedScrollListener != null && !mOnNeedScrollListener.needScroll()){
            return false;
        } else {
            try {
                return super.onTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(mOnNeedScrollListener != null && !mOnNeedScrollListener.needScroll()){
            return false;
        } else {
            try {
                return super.onInterceptTouchEvent(event);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public interface OnNeedScrollListener{
        boolean needScroll();
    }
}
```

只有当GalleryFragment滑动到最后一个，且图集推荐中有内容才会滑动外层的ViewPager
```

    @Override
    public boolean needScroll() {
        if (mCurrFragment instanceof GalleryFragment) {
            if (!((GalleryFragment) mCurrFragment).isLastItem()) {
                return false;
            } else {
                //当图集滑动到最后一个时，如果图集推荐没有内容，则禁止滑动
                GalleryRelatedFragment relatedFragment = (GalleryRelatedFragment) fragments.get(1);
                if (!relatedFragment.isHasData()) {
                    return false;
                }
            }
        }
        return true;
    }
```
## <font color=#C4573C size=5 face="黑体">源码传送门</font>
[Gallery](https://github.com/soulrelay/Gallery)






