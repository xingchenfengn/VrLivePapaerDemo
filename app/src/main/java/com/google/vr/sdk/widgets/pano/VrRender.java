package com.google.vr.sdk.widgets.pano;


import android.content.Context;

import com.google.vr.sdk.widgets.common.VrWidgetRenderer;

import java.lang.reflect.Field;

import javax.microedition.khronos.opengles.GL10;

/**
 * Vr实现的Render
 *
 * @author xingzhiqiao
 * @date 2017/11/16.
 */
public class VrRender extends VrPanoramaRenderer {
    private boolean isVisiable;

    public VrRender(Context context, GLThreadScheduler glThreadScheduler, float xMetersPerPixel, float yMetersPerPixel) {
        super(context, glThreadScheduler, xMetersPerPixel, yMetersPerPixel);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        Field field = null;
        long id = 0;
        try {
            field = VrWidgetRenderer.class.getDeclaredField("nativeRenderer");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        try {
            id = field.getLong(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (id != 0L) {
            this.nativeRenderFrame(id);
        }

    }
}
