package com.luxand;

import java.lang.Math;
import java.util.Arrays;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Size;
import android.view.View;
import android.view.Surface;
import android.widget.Toast;
import android.graphics.RectF;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

public class FaceSDKCameraView extends FrameLayout {

    private class PromiseHolder {

        public Promise promise = null;

    }

    private final Context context;
    private final PromiseHolder promiseHolder = new PromiseHolder();

    private Bitmap bitmap = null;
    private ByteBuffer buffer = null;
    private int width = -1, height = -1;

    public static FaceSDKCameraView Instance = null;

    public FaceSDKCameraView(final Context context) {
        super(context);
        this.context = context;

        Instance = this;
    }

    private void setupBitmap(final int width, final int height) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        buffer = ByteBuffer.allocate(width * height * 4);
    }

    public void setTextureView(final TextureView textureView) {

        final TextureView.SurfaceTextureListener listener = textureView.getSurfaceTextureListener();
        if (listener == null)
            return;

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(final SurfaceTexture surface, final int w, final int h) {
                listener.onSurfaceTextureAvailable(surface, w, h);
                setupBitmap(w, h);
            }

            @Override
            public void onSurfaceTextureSizeChanged(final SurfaceTexture surface, final int w, final int h) {
                listener.onSurfaceTextureSizeChanged(surface, w, h);
                setupBitmap(w, h);
            }

            @Override
            public void onSurfaceTextureUpdated(final SurfaceTexture surface) {
                listener.onSurfaceTextureUpdated(surface);

                synchronized(promiseHolder) {
                    if (promiseHolder.promise == null)
                        return;

                    if (bitmap == null)
                        setupBitmap(textureView.getWidth(), textureView.getHeight());

                    textureView.getBitmap(bitmap);
                    
                    buffer.clear();
                    bitmap.copyPixelsToBuffer(buffer);

                    FSDK.HImage image = new FSDK.HImage();
                    final int errorCode = FSDK.LoadImageFromBuffer(image, buffer.array(), bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth() * 4, 
                        new FSDK.FSDK_IMAGEMODE() {{
                            mode = FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_32BIT;
                        }});

                    final WritableMap map = Arguments.createMap();
                    map.putInt("image", image.himage);

                    if (errorCode != FSDK.FSDKE_OK)
                        FaceSDKModule.rejectPromise(promiseHolder.promise, errorCode);
                    else
                        promiseHolder.promise.resolve(map);

                    promiseHolder.promise = null;
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(final SurfaceTexture surface) {
                return listener.onSurfaceTextureDestroyed(surface);
            }

        });

    }

    public void requestFrame(final Promise promise) {
        synchronized (promiseHolder) {
            if (promiseHolder.promise != null)
                FaceSDKModule.rejectPromise(promise, FSDK.FSDKE_FAILED);
            promiseHolder.promise = promise;
        }
    }

}
