package com.xingzhiqiao.vrlivepaperdemo;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;

import com.google.vr.sdk.widgets.common.VrWidgetRenderer;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.pano.VrRender;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 壁纸服务
 *
 * @author xingzhiqiao
 * @date 2017/11/16.
 */

public class VrWallpaperService extends GLWallpaperService {

    public static final String TAG = VrWallpaperService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public Engine onCreateEngine() {
        return new VrEngine();
    }

    class VrEngine extends GLEngine {
        private VrRender vrRender;
        private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();
        private Uri fileUri = null;
        private ImageLoaderTask loaderTask;
        private VrViewHelper vrView;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
            setEGLContextClientVersion(2);
            setEGLConfigChooser(8, 8, 8, 8, 16, 8);
            VrWidgetRenderer.GLThreadScheduler scheduler = new VrWidgetRenderer.GLThreadScheduler() {
                @Override
                public void queueGlThreadEvent(Runnable runnable) {
                    queueEvent(runnable);
                }
            };
            vrView = new VrViewHelper(VrWallpaperService.this, scheduler);
            vrRender = vrView.getRenderer();
            setRenderer(vrRender);
            // Load the bitmap in a background thread to avoid blocking the UI thread. This operation can
            // take 100s of milliseconds.
            if (loaderTask != null) {
                // Cancel any task from a previous intent sent to this activity.
                loaderTask.cancel(true);
            }
            loaderTask = new ImageLoaderTask();
            loaderTask.execute(Pair.create(fileUri, panoOptions));
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            vrRender.shutdown();
            vrRender = null;
            if (loaderTask != null) {
                loaderTask.cancel(true);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            vrRender.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onResume() {
            super.onResume();
            vrRender.onResume();
        }

        /**
         * Helper class to manage threading.
         */
        class ImageLoaderTask extends AsyncTask<Pair<Uri, VrPanoramaView.Options>, Void, Boolean> {

            /**
             * Reads the bitmap from disk in the background and waits until it's loaded by pano widget.
             */
            @Override
            protected Boolean doInBackground(Pair<Uri, VrPanoramaView.Options>... fileInformation) {
                // It's safe to use null VrPanoramaView.Options.
                VrPanoramaView.Options panoOptions = null;
                InputStream istr = null;
                if (fileInformation == null || fileInformation.length < 1
                        || fileInformation[0] == null || fileInformation[0].first == null) {
                    AssetManager assetManager = getAssets();
                    try {
                        istr = assetManager.open("andes.jpg");
                        panoOptions = new VrPanoramaView.Options();
                        panoOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
                    } catch (IOException e) {
                        Log.e(TAG, "Could not decode default bitmap: " + e);
                        return false;
                    }
                } else {
                    try {
                        istr = new FileInputStream(new File(fileInformation[0].first.getPath()));
                        panoOptions = fileInformation[0].second;
                    } catch (IOException e) {
                        Log.e(TAG, "Could not load file: " + e);
                        return false;
                    }
                }

                vrView.loadImageFromBitmap(BitmapFactory.decodeStream(istr), panoOptions);
                try {
                    istr.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close input stream: " + e);
                }

                return true;
            }
        }
    }
}
