package kh.edu.rupp.fe.ruppmad;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * RUPPMAD
 * Created by leapkh on 4/26/17.
 */

public class AppSingleton {

    private static AppSingleton instance;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private Object[] data;

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }

    private AppSingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {

            }
        });
    }

    public static AppSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new AppSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
