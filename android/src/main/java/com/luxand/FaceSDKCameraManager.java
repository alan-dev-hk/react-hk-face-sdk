package com.luxand;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
    
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.View;

public class FaceSDKCameraManager extends ViewGroupManager<FaceSDKCameraView> {

    @Override
    public String getName() {
        return "CameraWrapper";
    }

    @Override
    public FaceSDKCameraView createViewInstance(ThemedReactContext context) {
        return new FaceSDKCameraView(context);
    }

    private static final Map<String, Integer> Commands = new HashMap<String, Integer>() {{
        put("setDefault", 0);
    }};

    public Map<String, Integer> getCommandsMap() {
        return Commands;
    }

    @Override
    public void receiveCommand(final FaceSDKCameraView view, final int id, final ReadableArray args) {
        super.receiveCommand(view, id, args);

        switch (id) {
            case 0:
                FaceSDKCameraView.Instance = view;
                break;
            default:
                break;
        }
    }

    private boolean trySetupTextureView(final FaceSDKCameraView parent, final View child) {
        if (child instanceof TextureView) {
            parent.setTextureView((TextureView)child);
            return true;
        }

        if (child instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup)child;
            for (int i = 0, size = group.getChildCount(); i < size; ++i)
                if (trySetupTextureView(parent, group.getChildAt(i)))
                    return true;
        }

        return false;
    }

    @Override
    public void addView(final FaceSDKCameraView parent, final View child, final int index) {
        trySetupTextureView(parent, child);
        parent.addView(child, index);
    }

    @Override
    public void addViews(final FaceSDKCameraView parent, final List<View> views) {
        UiThreadUtil.assertOnUiThread();

        for (int i = 0, size = views.size(); i < size; ++i) {
            if (trySetupTextureView(parent, views.get(i)))
                break;
        }

        for (int i = 0, size = views.size(); i < size; i++) {
            addView(parent, views.get(i), i);
        }
    }

}