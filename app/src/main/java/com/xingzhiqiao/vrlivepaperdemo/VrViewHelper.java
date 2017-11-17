package com.xingzhiqiao.vrlivepaperdemo;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.vr.sdk.widgets.common.VrWidgetRenderer;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.pano.VrRender;


/**
 *
 * @author xingzhiqiao
 * @date 2017/11/16.
 */
public class VrViewHelper {


    private VrRender renderer;
    private VrPanoramaEventListener eventListener = new VrPanoramaEventListener();
    private DisplayMetrics displayMetrics;
    private Context context;
    private VrWidgetRenderer.GLThreadScheduler scheduler;

    public VrViewHelper(Context context, VrWidgetRenderer.GLThreadScheduler scheduler) {
        this.context = context;
        this.scheduler = scheduler;
        init();
    }

    private void init() {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        Display display = windowManager.getDefaultDisplay();
        this.displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealMetrics(this.displayMetrics);
        } else {
            display.getMetrics(this.displayMetrics);
        }
        //初始化render
        initializeRenderingView();
    }

    VrRender getRenderer() {
        return this.renderer;
    }

    private void initializeRenderingView() {
        float xMetersPerPixel = 0.0254F / this.displayMetrics.xdpi;
        float yMetersPerPixel = 0.0254F / this.displayMetrics.ydpi;
        this.renderer = new VrRender(context, scheduler, xMetersPerPixel, yMetersPerPixel);
    }

    public void loadImageFromBitmap(Bitmap bitmap, VrPanoramaView.Options panoOptions) {

        if (panoOptions == null) {
            panoOptions = new VrPanoramaView.Options();
        } else {
            if ((panoOptions.inputType <= 0) || (panoOptions.inputType >= 3)) {
                int i = panoOptions.inputType;
                Log.e(this.getClass().toString(), 38 + "Invalid Options.inputType: " + i);
                panoOptions.inputType = 1;
            }
        }
        this.renderer.loadImageFromBitmap(bitmap, panoOptions, this.eventListener);
    }
}


