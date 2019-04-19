package iste.not.com.POST;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidnetworking.AndroidNetworking;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import iste.not.com.R;


public class ViewPagerAdapter extends PagerAdapter
{

    Activity activity;
    String[] images;
    ImageLoader loader;
    LayoutInflater layoutInflater;
    File file;

   // PhotoViewAttacher pAttacher;
    public ViewPagerAdapter(Activity activity, String[] images, ImageLoader loader) {
        this.activity = activity;
        this.images = images;
        this.loader = loader;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position)
    {
        layoutInflater =(LayoutInflater)activity.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.viewpager_item,container,false);
       final ZoomableImageView imageView = (ZoomableImageView) itemView.findViewById(R.id.imageView);


        final ProgressBar  bar =(ProgressBar) itemView.findViewById(R.id.spinner);
        final ProgressBar spinner =  new ProgressBar(activity, null, android.R.attr.progressBarStyleSmall);
        DisplayImageOptions.Builder options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.placeholder_image)
                .showImageOnFail(R.drawable.add_picture);
        loader.displayImage(images[position], imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                bar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                bar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                bar.setVisibility(View.GONE);
              /*  pAttacher = new PhotoViewAttacher(imageView);
                pAttacher.update();*/
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (  ( android.support.v4.view.ViewPager)container).removeView((View)object);
    }
    public static Bitmap getBitmapFromURL(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
