package com.example.databinding;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageUtil {
    /**
     * 使用ImageLoader显示图片
     * @param imageView
     * @param url
     */
    @BindingAdapter({"image"})
    public static void imageLoader(ImageView imageView, String url) {
        ImageLoader.getInstance().displayImage(url, imageView);
    }
}
