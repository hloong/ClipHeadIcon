# ClipHeadIcon
微信头像剪裁Demo


网上搜到了以下参考资料，其中clip-image犹豫项目太繁琐我没用采用，转而看了clip-image作者引用的2个csdn链接

https://github.com/msdx/clip-image
http://blog.csdn.net/lmj623565791/article/details/39761281
http://blog.csdn.net/xiechengfa/article/details/45702427

最后一个链接中的代码其实已经是很完善了，但是有一个问题，
就是当图片过大的时候处理速度过慢，然后传递过程会出现问题，
比如小米4拍摄的图片分辨率很大，头像其实没必要过于清晰，设置头像大小和手机屏幕差不多宽就可以满足需求了，所以我将代码修改了

ClipImageActivity中的代码片段：
```java
//不在内存中读取图片的宽高
opts.inJustDecodeBounds = true;
BitmapFactory.decodeFile(path, opts);
int width = opts.outWidth;
//注意此处为了解决1080p手机拍摄图片过大所以做了一定压缩，否则bitmap在小米4以及拍图比较大的机型上会显示黑屏
opts.inSampleSize = width > 1080 ? (int)(width / 1080) : 1 ;
opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
```
并且将图片剪裁的时候设置了大小控制，比如我们是500kb的显示那就是

ClipZoomImageView文件中的代码片段：

//将剪裁的图片压缩到500k以下，如果没需求就注释该段代码
```java
     ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
       int options = 100;//保存的图片自动压缩低于500k
       bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);  
       while (baos.toByteArray().length / 1024 > 500) {   
           baos.reset();  
           options -= 10;  
           bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);  
   } 
```
如果你所需要的上传图片是其他值直接将500改成你所要的就可以了

具体分析请看：http://www.hloong.com/?p=328

