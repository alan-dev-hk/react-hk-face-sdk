package com.luxand;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.Map;
import java.util.HashMap;

import android.util.Base64;

public class FaceSDKModule extends ReactContextBaseJavaModule {

    private class Image extends FSDK.HImage {

        Image() {
            this.himage = -1;
        }

        Image(final int handle) {
            this.himage = handle;
        }

    }

    private class Tracker extends FSDK.HTracker {

        Tracker() {
            this.htracker = -1;
        }

        Tracker(final int handle) {
            this.htracker = handle;
        }

    }

    private class Camera extends FSDK.HCamera {

        Camera() {
            this.hcamera = -1;
        }

        Camera(final int handle) {
            this.hcamera = hcamera;
        }

    }

    private class ImageMode extends FSDK.FSDK_IMAGEMODE {

        ImageMode(final Integer mode) {
            this.mode = mode.intValue();
        }

    }

    private class VideoCompressionType extends FSDK.FSDK_VIDEOCOMPRESSIONTYPE {

        VideoCompressionType(final Integer type) {
            this.type = type.intValue();
        }

    }

    private interface SDKFunction {

        int invoke(final WritableMap map);

    }

    public interface Callable<T> {

        int call(final T result);

    }

    private final ReactApplicationContext reactContext;

    private static final Map<String, Integer> ERROR = new HashMap<String, Integer>() {{
            put("OK",                                FSDK.FSDKE_OK);
            put("FAILED",                            FSDK.FSDKE_FAILED);
            put("NOT_ACTIVATED",                     FSDK.FSDKE_NOT_ACTIVATED);
            put("OUT_OF_MEMORY",                     FSDK.FSDKE_OUT_OF_MEMORY);
            put("INVALID_ARGUMENT",                  FSDK.FSDKE_INVALID_ARGUMENT);
            put("IO_ERROR",                          FSDK.FSDKE_IO_ERROR);
            put("IMAGE_TOO_SMALL",                   FSDK.FSDKE_IMAGE_TOO_SMALL);
            put("FACE_NOT_FOUND",                    FSDK.FSDKE_FACE_NOT_FOUND);
            put("INSUFFICIENT_BUFFER_SIZE",          FSDK.FSDKE_INSUFFICIENT_BUFFER_SIZE);
            put("UNSUPPORTED_IMAGE_EXTENSION",       FSDK.FSDKE_UNSUPPORTED_IMAGE_EXTENSION);
            put("CANNOT_OPEN_FILE",                  FSDK.FSDKE_CANNOT_OPEN_FILE);
            put("CANNOT_CREATE_FILE",                FSDK.FSDKE_CANNOT_CREATE_FILE);
            put("BAD_FILE_FORMAT",                   FSDK.FSDKE_BAD_FILE_FORMAT);
            put("FILE_NOT_FOUND",                    FSDK.FSDKE_FILE_NOT_FOUND);
            put("CONNECTION_CLOSED",                 FSDK.FSDKE_CONNECTION_CLOSED);
            put("CONNECTION_FAILED",                 FSDK.FSDKE_CONNECTION_FAILED);
            put("IP_INIT_FAILED",                    FSDK.FSDKE_IP_INIT_FAILED);
            put("NEED_SERVER_ACTIVATION",            FSDK.FSDKE_NEED_SERVER_ACTIVATION);
            put("ID_NOT_FOUND",                      FSDK.FSDKE_ID_NOT_FOUND);
            put("ATTRIBUTE_NOT_DETECTED",            FSDK.FSDKE_ATTRIBUTE_NOT_DETECTED);
            put("INSUFFICIENT_TRACKER_MEMORY_LIMIT", FSDK.FSDKE_INSUFFICIENT_TRACKER_MEMORY_LIMIT);
            put("UNKNOWN_ATTRIBUTE",                 FSDK.FSDKE_UNKNOWN_ATTRIBUTE);
            put("UNSUPPORTED_FILE_VERSION",          FSDK.FSDKE_UNSUPPORTED_FILE_VERSION);
            put("SYNTAX_ERROR",                      FSDK.FSDKE_SYNTAX_ERROR);
            put("PARAMETER_NOT_FOUND",               FSDK.FSDKE_PARAMETER_NOT_FOUND);
            put("INVALID_TEMPLATE",                  FSDK.FSDKE_INVALID_TEMPLATE);
            put("UNSUPPORTED_TEMPLATE_VERSION",      FSDK.FSDKE_UNSUPPORTED_TEMPLATE_VERSION);
            put("CAMERA_INDEX_DOES_NOT_EXIST",       FSDK.FSDKE_CAMERA_INDEX_DOES_NOT_EXIST);
            put("PLATFORM_NOT_LICENSED",             FSDK.FSDKE_PLATFORM_NOT_LICENSED);
    }};

    private static final Map<Integer, String> ERROR_NAME = new HashMap<Integer, String>();

    static {
        for (Map.Entry<String, Integer> it : ERROR.entrySet())
            ERROR_NAME.put(it.getValue(), it.getKey());
    }

    private static final Map<String, Integer> FEATURE = new HashMap<String, Integer>() {{
        put("LEFT_EYE",                    FSDK.FSDKP_LEFT_EYE);
        put("LEFT_EYE_INNER_CORNER",       FSDK.FSDKP_LEFT_EYE_INNER_CORNER);
        put("LEFT_EYE_OUTER_CORNER",       FSDK.FSDKP_LEFT_EYE_OUTER_CORNER);
        put("LEFT_EYE_LOWER_LINE1",        FSDK.FSDKP_LEFT_EYE_LOWER_LINE1);
        put("LEFT_EYE_LOWER_LINE2",        FSDK.FSDKP_LEFT_EYE_LOWER_LINE2);
        put("LEFT_EYE_LOWER_LINE3",        FSDK.FSDKP_LEFT_EYE_LOWER_LINE3);
        put("LEFT_EYE_UPPER_LINE1",        FSDK.FSDKP_LEFT_EYE_UPPER_LINE1);
        put("LEFT_EYE_UPPER_LINE2",        FSDK.FSDKP_LEFT_EYE_UPPER_LINE2);
        put("LEFT_EYE_UPPER_LINE3",        FSDK.FSDKP_LEFT_EYE_UPPER_LINE3);
        put("LEFT_EYE_LEFT_IRIS_CORNER",   FSDK.FSDKP_LEFT_EYE_LEFT_IRIS_CORNER);
        put("LEFT_EYE_RIGHT_IRIS_CORNER",  FSDK.FSDKP_LEFT_EYE_RIGHT_IRIS_CORNER);
        put("RIGHT_EYE_INNER_CORNER",      FSDK.FSDKP_RIGHT_EYE_INNER_CORNER);
        put("RIGHT_EYE_OUTER_CORNER",      FSDK.FSDKP_RIGHT_EYE_OUTER_CORNER);
        put("RIGHT_EYE_LOWER_LINE1",       FSDK.FSDKP_RIGHT_EYE_LOWER_LINE1);
        put("RIGHT_EYE_LOWER_LINE2",       FSDK.FSDKP_RIGHT_EYE_LOWER_LINE2);
        put("RIGHT_EYE_LOWER_LINE3",       FSDK.FSDKP_RIGHT_EYE_LOWER_LINE3);
        put("RIGHT_EYE_UPPER_LINE1",       FSDK.FSDKP_RIGHT_EYE_UPPER_LINE1);
        put("RIGHT_EYE_UPPER_LINE2",       FSDK.FSDKP_RIGHT_EYE_UPPER_LINE2);
        put("RIGHT_EYE_UPPER_LINE3",       FSDK.FSDKP_RIGHT_EYE_UPPER_LINE3);
        put("RIGHT_EYE_LEFT_IRIS_CORNER",  FSDK.FSDKP_RIGHT_EYE_LEFT_IRIS_CORNER);
        put("RIGHT_EYE_RIGHT_IRIS_CORNER", FSDK.FSDKP_RIGHT_EYE_RIGHT_IRIS_CORNER);
        put("LEFT_EYEBROW_INNER_CORNER",   FSDK.FSDKP_LEFT_EYEBROW_INNER_CORNER);
        put("LEFT_EYEBROW_MIDDLE",         FSDK.FSDKP_LEFT_EYEBROW_MIDDLE);
        put("LEFT_EYEBROW_MIDDLE_LEFT",    FSDK.FSDKP_LEFT_EYEBROW_MIDDLE_LEFT);
        put("RIGHT_EYEBROW_INNER_CORNER",  FSDK.FSDKP_RIGHT_EYEBROW_INNER_CORNER);
        put("RIGHT_EYEBROW_MIDDLE",        FSDK.FSDKP_RIGHT_EYEBROW_MIDDLE);
        put("RIGHT_EYEBROW_MIDDLE_LEFT",   FSDK.FSDKP_RIGHT_EYEBROW_MIDDLE_LEFT);
        put("RIGHT_EYEBROW_MIDDLE_RIGHT",  FSDK.FSDKP_RIGHT_EYEBROW_MIDDLE_RIGHT);
        put("RIGHT_EYEBROW_OUTER_CORNER",  FSDK.FSDKP_RIGHT_EYEBROW_OUTER_CORNER);
        put("NOSE_TIP",                    FSDK.FSDKP_NOSE_TIP);
        put("NOSE_BOTTOM",                 FSDK.FSDKP_NOSE_BOTTOM);
        put("NOSE_BRIDGE",                 FSDK.FSDKP_NOSE_BRIDGE);
        put("NOSE_LEFT_WING",              FSDK.FSDKP_NOSE_LEFT_WING);
        put("NOSE_LEFT_WING_OUTER",        FSDK.FSDKP_NOSE_LEFT_WING_OUTER);
        put("NOSE_LEFT_WING_LOWER",        FSDK.FSDKP_NOSE_LEFT_WING_LOWER);
        put("NOSE_RIGHT_WING",             FSDK.FSDKP_NOSE_RIGHT_WING);
        put("NOSE_RIGHT_WING_OUTER",       FSDK.FSDKP_NOSE_RIGHT_WING_OUTER);
        put("NOSE_RIGHT_WING_LOWER",       FSDK.FSDKP_NOSE_RIGHT_WING_LOWER);
        put("MOUTH_RIGHT_CORNER",          FSDK.FSDKP_MOUTH_RIGHT_CORNER);
        put("MOUTH_BOTTOM",                FSDK.FSDKP_MOUTH_BOTTOM);
        put("MOUTH_BOTTOM_INNER",          FSDK.FSDKP_MOUTH_BOTTOM_INNER);
        put("MOUTH_LEFT_TOP",              FSDK.FSDKP_MOUTH_LEFT_TOP);
        put("MOUTH_LEFT_TOP_INNER",        FSDK.FSDKP_MOUTH_LEFT_TOP_INNER);
        put("MOUTH_RIGHT_TOP",             FSDK.FSDKP_MOUTH_RIGHT_TOP);
        put("MOUTH_RIGHT_TOP_INNER",       FSDK.FSDKP_MOUTH_RIGHT_TOP_INNER);
        put("MOUTH_LEFT_BOTTOM",           FSDK.FSDKP_MOUTH_LEFT_BOTTOM);
        put("MOUTH_LEFT_BOTTOM_INNER",     FSDK.FSDKP_MOUTH_LEFT_BOTTOM_INNER);
        put("MOUTH_RIGHT_BOTTOM",          FSDK.FSDKP_MOUTH_RIGHT_BOTTOM);
        put("MOUTH_RIGHT_BOTTOM_INNER",    FSDK.FSDKP_MOUTH_RIGHT_BOTTOM_INNER);
        put("NASOLABIAL_FOLD_LEFT_UPPER",  FSDK.FSDKP_NASOLABIAL_FOLD_LEFT_UPPER);
        put("NASOLABIAL_FOLD_LEFT_LOWER",  FSDK.FSDKP_NASOLABIAL_FOLD_LEFT_LOWER);
        put("NASOLABIAL_FOLD_RIGHT_UPPER", FSDK.FSDKP_NASOLABIAL_FOLD_RIGHT_UPPER);
        put("NASOLABIAL_FOLD_RIGHT_LOWER", FSDK.FSDKP_NASOLABIAL_FOLD_RIGHT_LOWER);
        put("CHIN_BOTTOM",                 FSDK.FSDKP_CHIN_BOTTOM);
        put("CHIN_LEFT",                   FSDK.FSDKP_CHIN_LEFT);
        put("CHIN_RIGHT",                  FSDK.FSDKP_CHIN_RIGHT);
        put("FACE_CONTOUR1",               FSDK.FSDKP_FACE_CONTOUR1);
        put("FACE_CONTOUR2",               FSDK.FSDKP_FACE_CONTOUR2);
        put("FACE_CONTOUR12",              FSDK.FSDKP_FACE_CONTOUR12);
        put("FACE_CONTOUR13",              FSDK.FSDKP_FACE_CONTOUR13);
        put("FACE_CONTOUR14",              FSDK.FSDKP_FACE_CONTOUR14);
        put("FACE_CONTOUR15",              FSDK.FSDKP_FACE_CONTOUR15);
        put("FACE_CONTOUR16",              FSDK.FSDKP_FACE_CONTOUR16);
        put("FACE_CONTOUR17",              FSDK.FSDKP_FACE_CONTOUR17);
    }};

    private static final Map<String, Integer> IMAGEMODE = new HashMap<String, Integer>() {{
        put("IMAGE_GRAYSCALE_8BIT", FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_GRAYSCALE_8BIT);
        put("IMAGE_COLOR_24BIT",    FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT);
        put("IMAGE_COLOR_32BIT",    FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_32BIT);
    }};

    private static final Map<String, Integer> VIDEOCOMPRESSIONTYPE = new HashMap<String, Integer>() {{
        put("MJPEG", FSDK.FSDK_VIDEOCOMPRESSIONTYPE.FSDK_MJPEG);
    }};

    public static void rejectPromise(final Promise promise, final int errorCode) {
        final WritableMap map = Arguments.createMap();
        map.putInt("errorCode", errorCode);
        promise.reject(ERROR_NAME.get(errorCode), map);
    }

    public FaceSDKModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "FaceSDK";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("ERROR", ERROR);
        constants.put("IMAGEMODE", IMAGEMODE);
        constants.put("VIDEOCOMPRESSIONTYPE", VIDEOCOMPRESSIONTYPE);
        constants.put("FEATURE", FEATURE);
        return constants;
    }

    private WritableMap FacePostionToWritableMap(final FSDK.TFacePosition position) {
        return new WritableNativeMap() {{
            putInt("xc",       position.xc);
            putInt("yc",       position.yc);
            putInt("w",        position.w);
            putDouble("angle", position.angle);
        }};
    }

    private FSDK.TFacePosition ReadableMapToFacePosition(final ReadableMap map) {
        return new FSDK.TFacePosition() {{
            xc = map.getInt("xc");
            yc = map.getInt("yc");
            w  = map.getInt("w");
            angle = map.getDouble("angle");
        }};
    }

    private WritableMap PointToWritableMap(final FSDK.TPoint point) {
        if (point == null)
            return new WritableNativeMap() {{
                putInt("x", 0);
                putInt("y", 0);
            }};

        return new WritableNativeMap() {{
            putInt("x", point.x);
            putInt("y", point.y);
        }};
    }

    private FSDK.TPoint ReadableMapToPoint(final ReadableMap map) {
        return new FSDK.TPoint() {{
            x = map.getInt("x");
            y = map.getInt("y");
        }};
    }

    private WritableArray FeaturesToWritableArray(final FSDK.FSDK_Features features) {
        WritableArray result = new WritableNativeArray();
        for (final FSDK.TPoint point : features.features)
            result.pushMap(PointToWritableMap(point));
        return result;
    }

    private FSDK.FSDK_Features ReadableArrayToFeatures(final ReadableArray array) {
        final FSDK.FSDK_Features features = new FSDK.FSDK_Features();
        for (int i = 0; i < array.size(); ++i)
            features.features[i] = ReadableMapToPoint(array.getMap(i));
        return features;
    }

    private FSDK.FSDK_FaceTemplate Base64ToTemplate(final String base64) {
        return new FSDK.FSDK_FaceTemplate() {{
            template = Base64.decode(base64, Base64.DEFAULT);
        }};
    }

    public static void ExecuteSDKFunction(final SDKFunction function, final Promise promise) {
        final WritableMap map = Arguments.createMap();
        final int errorCode = function.invoke(map);
        if (errorCode != FSDK.FSDKE_OK) {
            rejectPromise(promise, errorCode);
            return;
        }
        promise.resolve(map);
    }

    private void ExecuteStringResultSDKFunction(final Callable<String[]> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final String[] result = new String[1];
                    final int errorCode = function.call(result);
                    map.putString(name, result[0]);
                    return errorCode;
                }
            }, 
        promise);
    }

    private void ExecuteIntegerResultSDKFunction(final Callable<int[]> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final int[] result = new int[1];
                    final int errorCode = function.call(result);
                    map.putInt(name, result[0]);
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteLongResultSDKFunction(final Callable<long[]> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final long[] result = new long[1];
                    final int errorCode = function.call(result);
                    map.putInt(name, (int)result[0]);
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteFloatResultSDKFunction(final Callable<float[]> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final float[] result = new float[1];
                    final int errorCode = function.call(result);
                    map.putDouble(name, result[0]);
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteCreateImageSDKFunction(final Callable<Image> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final Image result = new Image();
                    final int errorCode = function.call(result);
                    map.putInt(name, result.himage);
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteCreateImageSDKFunction(final Callable<Image> function, final Promise promise) {
        ExecuteCreateImageSDKFunction(function, "image", promise);
    }

    private void ExecuteCreateTrackerSDKFunction(final Callable<Tracker> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final Tracker result = new Tracker();
                    final int errorCode = function.call(result);
                    map.putInt(name, result.htracker);
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteCreateTrackerSDKFunction(final Callable<Tracker> function, final Promise promise) {
        ExecuteCreateTrackerSDKFunction(function, "tracker", promise);
    }

    private void ExecuteCreateCameraSDKFunction(final Callable<Camera> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final Camera result = new Camera();
                    final int errorCode = function.call(result);
                    map.putInt(name, result.hcamera);
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteCreateCameraSDKFunction(final Callable<Camera> function, final Promise promise) {
        ExecuteCreateCameraSDKFunction(function, "camera", promise);
    }

    private void ExecuteImageResultSDKFunction(final Callable<Image> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final Image result = new Image();
                    int errorCode = FSDK.CreateEmptyImage(result);
                    if (errorCode == FSDK.FSDKE_OK)
                        errorCode = function.call(result);
                    map.putInt(name, result.himage);
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteImageResultSDKFunction(final Callable<Image> function, final Promise promise) {
        ExecuteImageResultSDKFunction(function, "image", promise);
    }

    private void ExecuteByteBufferResultSDKFunction(final Callable<byte[]> function, final String name, final Integer size, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final byte[] result = new byte[size];
                    final int errorCode = function.call(result);
                    map.putString(name, Base64.encodeToString(result, Base64.NO_WRAP));
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteFacePositionResultSDKFunction(final Callable<FSDK.TFacePosition> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final FSDK.TFacePosition result = new FSDK.TFacePosition();
                    final int errorCode = function.call(result);
                    map.putMap(name, FacePostionToWritableMap(result));
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteFacePositionResultSDKFunction(final Callable<FSDK.TFacePosition> function, final Promise promise) {
        ExecuteFacePositionResultSDKFunction(function, "facePosition", promise);
    }

    private void ExecuteFacesResultSDKFunction(final Callable<FSDK.TFaces> function, final String name, final Integer max, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final FSDK.TFaces faces = new FSDK.TFaces(max);
                    final int errorCode = function.call(faces);
                    WritableArray result = new WritableNativeArray();
                    if (faces.faces != null)
                        for (final FSDK.TFacePosition face : faces.faces)
                            result.pushMap(FacePostionToWritableMap(face));
                    map.putArray(name, result);
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteFacesResultSDKFunction(final Callable<FSDK.TFaces> function, final Integer max, final Promise promise) {
        ExecuteFacesResultSDKFunction(function, "faces", max, promise);
    }

    private void ExecuteFeaturesResultSDKFunction(final Callable<FSDK.FSDK_Features> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final FSDK.FSDK_Features features = new FSDK.FSDK_Features();
                    final int errorCode = function.call(features);
                    map.putArray(name, FeaturesToWritableArray(features));
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteFeaturesResultSDKFunction(final Callable<FSDK.FSDK_Features> function, final Promise promise) {
        ExecuteFeaturesResultSDKFunction(function, "features", promise);
    }

    private void ExecuteFaceTemplateResultSDKFunction(final Callable<FSDK.FSDK_FaceTemplate> function, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final FSDK.FSDK_FaceTemplate result = new FSDK.FSDK_FaceTemplate();
                    final int errorCode = function.call(result);
                    map.putString(name, Base64.encodeToString(result.template, Base64.NO_WRAP));
                    return errorCode;
                }
            },
        promise);
    }

    private void ExecuteFaceTemplateResultSDKFunction(final Callable<FSDK.FSDK_FaceTemplate> function, final Promise promise) {
        ExecuteFaceTemplateResultSDKFunction(function, "template", promise);
    }

    @ReactMethod
    public void ActivateLibrary(final String LicenseKey, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.ActivateLibrary(LicenseKey);
                }
            }, 
        promise);
    }

    @ReactMethod
    public void GetLicenseInfo(final Promise promise) {
        ExecuteStringResultSDKFunction(
            new Callable<String[]>() {
                public int call(final String[] result) {
                    return FSDK.GetLicenseInfo(result);
                }
            }, 
        "licenseInfo", promise);
    }

    @ReactMethod
    public void SetNumThreads(final Integer Num, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.SetNumThreads(Num);
                }
            },
        promise);
    }

    @ReactMethod
    public void GetNumThreads(final Promise promise) {
        ExecuteIntegerResultSDKFunction(
            new Callable<int[]>() {
                public int call(final int[] result) {
                    return FSDK.GetNumThreads(result);
                }
            }, 
        "numThreads", promise);
    }

    @ReactMethod
    public void Initialize(final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.Initialize();
                }
            },
        promise);
    }

    @ReactMethod
    public void Finalize(final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.Finalize();
                }
            },
        promise);
    }

    @ReactMethod
    public void CreateEmptyImage(final Promise promise) {
        ExecuteCreateImageSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.CreateEmptyImage(result);
                }
            },
        promise);
    }

    @ReactMethod
    public void FreeImage(final Integer image, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.FreeImage(new Image(image));
                }
            },
        promise);
    }

    @ReactMethod 
    public void LoadImageFromFile(final String filename, final Promise promise) {
        ExecuteCreateImageSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.LoadImageFromFile(result, filename);
                }
            },
        promise);
    }

    @ReactMethod 
    public void LoadImageFromFileWithAlpha(final String filename, final Promise promise) {
        ExecuteCreateImageSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.LoadImageFromFileWithAlpha(result, filename);
                }
            },
        promise);
    }

    @ReactMethod 
    public void SaveImageToFile(final String filename, final Integer image, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.SaveImageToFile(new Image(image), filename);
                }
            },
        promise);
    }

    @ReactMethod 
    public void SetJpegCompressionQuality(final Integer quality, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.SetJpegCompressionQuality(quality.intValue());
                }
            },
        promise);
    }

    @ReactMethod
    public void GetImageWidth(final Integer image, final Promise promise) {
        ExecuteIntegerResultSDKFunction(
            new Callable<int[]>() {
                public int call(final int[] result) {
                    return FSDK.GetImageWidth(new Image(image), result);
                }
            }, 
        "width", promise);
    }

    @ReactMethod
    public void GetImageHeight(final Integer image, final Promise promise) {
        ExecuteIntegerResultSDKFunction(
            new Callable<int[]>() {
                public int call(final int[] result) {
                    return FSDK.GetImageHeight(new Image(image), result);
                }
            }, 
        "height", promise);
    }

    @ReactMethod
    public void LoadImageFromBuffer(final String base64, final Integer width, final Integer height, 
        final Integer scanLine, final Integer imageMode, final Promise promise) {
        final byte[] buffer = Base64.decode(base64, Base64.DEFAULT);
        ExecuteCreateImageSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.LoadImageFromBuffer(result, buffer, width.intValue(), height.intValue(), 
                        scanLine.intValue(), new ImageMode(imageMode));
                }
            },
        promise);
    }

    @ReactMethod
    public void GetImageBufferSize(final Integer image, final Integer imageMode, final Promise promise) {
        ExecuteIntegerResultSDKFunction(
            new Callable<int[]>() {
                public int call(final int[] result) {
                    return FSDK.GetImageBufferSize(new Image(image), result, new ImageMode(imageMode));
                }
            }, 
        "bufferSize", promise);
    }

    @ReactMethod
    public void SaveImageToBuffer(final Integer image, final Integer imageMode, final Integer bufferSize, final Promise promise) {
        ExecuteByteBufferResultSDKFunction(
            new Callable<byte[]>() {
                public int call(final byte[] result) {
                    return FSDK.SaveImageToBuffer(new Image(image), result, new ImageMode(imageMode));
                }
            }, 
        "buffer", bufferSize, promise);
    }

    @ReactMethod
    public void LoadImageFromJpegBuffer(final String base64, final Promise promise) {
        final byte[] buffer = Base64.decode(base64, Base64.DEFAULT);
        ExecuteCreateImageSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.LoadImageFromJpegBuffer(result, buffer, buffer.length);
                }
            },
        promise);
    }

    @ReactMethod
    public void LoadImageFromPngBuffer(final String base64, final Promise promise) {
        final byte[] buffer = Base64.decode(base64, Base64.DEFAULT);
        ExecuteCreateImageSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.LoadImageFromPngBuffer(result, buffer, buffer.length);
                }
            },
        promise);
    }

    @ReactMethod
    public void LoadImageFromPngBufferWithAlpha(final String base64, final Promise promise) {
        final byte[] buffer = Base64.decode(base64, Base64.DEFAULT);
        ExecuteCreateImageSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.LoadImageFromPngBufferWithAlpha(result, buffer, buffer.length);
                }
            },
        promise);
    }

    @ReactMethod
    public void DetectFace(final Integer image, final Promise promise) {
        ExecuteFacePositionResultSDKFunction(
            new Callable<FSDK.TFacePosition>() {
                public int call(final FSDK.TFacePosition result) {
                    return FSDK.DetectFace(new Image(image), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void DetectMultipleFaces(final Integer image, final Integer maxFaces, final Promise promise) {
        ExecuteFacesResultSDKFunction(
            new Callable<FSDK.TFaces>() {
                public int call(final FSDK.TFaces result) {
                    return FSDK.DetectMultipleFaces(new Image(image), result);
                }
            },
        maxFaces, promise);
    }

    @ReactMethod 
    public void SetFaceDetectionParameters(final Boolean handleArbitraryRotations, final Boolean determineFaceRotationAngle, 
        final Integer internalResizeWidth, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.SetFaceDetectionParameters(handleArbitraryRotations.booleanValue(),
                        determineFaceRotationAngle.booleanValue(), internalResizeWidth.intValue());
                }
            },
        promise);
    }

    @ReactMethod
    public void SetFaceDetectionThreshold(final Integer threshold, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.SetFaceDetectionThreshold(threshold.intValue());
                }
            },
        promise);
    }

    @ReactMethod
    public void GetDetectedFaceConfidence(final Promise promise) {
        ExecuteIntegerResultSDKFunction(
            new Callable<int[]>() {
                public int call(final int[] result) {
                    return FSDK.GetDetectedFaceConfidence(result);
                }
            },
        "confidence", promise);
    }

    @ReactMethod
    public void DetectFacialFeatures(final Integer image, final Promise promise) {
        ExecuteFeaturesResultSDKFunction(
            new Callable<FSDK.FSDK_Features>() {
                public int call(final FSDK.FSDK_Features result) {
                    return FSDK.DetectFacialFeatures(new Image(image), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void DetectFacialFeaturesInRegion(final Integer image, final ReadableMap position, final Promise promise) {
        ExecuteFeaturesResultSDKFunction(
            new Callable<FSDK.FSDK_Features>() {
                public int call(final FSDK.FSDK_Features result) {
                    return FSDK.DetectFacialFeaturesInRegion(new Image(image), ReadableMapToFacePosition(position), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void DetectEyes(final Integer image, final Promise promise) {
        ExecuteFeaturesResultSDKFunction(
            new Callable<FSDK.FSDK_Features>() {
                public int call(final FSDK.FSDK_Features result) {
                    return FSDK.DetectEyes(new Image(image), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void DetectEyesInRegion(final Integer image, final ReadableMap position, final Promise promise) {
        ExecuteFeaturesResultSDKFunction(
            new Callable<FSDK.FSDK_Features>() {
                public int call(final FSDK.FSDK_Features result) {
                    return FSDK.DetectEyesInRegion(new Image(image), ReadableMapToFacePosition(position), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void CopyImage(final Integer image, final Promise promise) {
        ExecuteImageResultSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.CopyImage(new Image(image), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void ResizeImage(final Integer image, final Double ratio, final Promise promise) {
        ExecuteImageResultSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.ResizeImage(new Image(image), ratio.doubleValue(), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void RotateImage90(final Integer image, final Integer multiplier, final Promise promise) {
        ExecuteImageResultSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.RotateImage90(new Image(image), multiplier.intValue(), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void RotateImage(final Integer image, final Double angle, final Promise promise) {
        ExecuteImageResultSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.RotateImage(new Image(image), angle.doubleValue(), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void RotateImageCenter(final Integer image, final Double angle, final Double x, final Double y, final Promise promise) {
        ExecuteImageResultSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.RotateImageCenter(new Image(image), angle.doubleValue(), x.doubleValue(), y.doubleValue(), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void CopyRect(final Integer image, final Integer x1, final Integer y1, final Integer x2, final Integer y2, final Promise promise) {
        ExecuteImageResultSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.CopyRect(new Image(image), x1.intValue(), y1.intValue(), x2.intValue(), y2.intValue(), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void CopyRectReplicateBorder(final Integer image, final Integer x1, final Integer y1, final Integer x2, final Integer y2, final Promise promise) {
        ExecuteImageResultSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.CopyRectReplicateBorder(new Image(image), x1.intValue(), y1.intValue(), x2.intValue(), y2.intValue(), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void MirrorImage(final Integer image, final Boolean vertical, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.MirrorImage(new Image(image), vertical.booleanValue());
                }
            },
        promise);
    }

    @ReactMethod
    public void ExtractFaceImage(final Integer image, final ReadableArray features, final Integer width, final Integer height, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final Image resultImage = new Image();
                    final FSDK.FSDK_Features resultFeatures = new FSDK.FSDK_Features();
                    final int errorCode = FSDK.ExtractFaceImage(new Image(image), ReadableArrayToFeatures(features), 
                        width.intValue(), height.intValue(), resultImage, resultFeatures);
                    map.putInt("image", resultImage.himage);
                    map.putArray("features", FeaturesToWritableArray(resultFeatures));
                    return errorCode;
                }
            },
        promise);
    }

    @ReactMethod
    public void GetFaceTemplate(final Integer image, final Promise promise) {
        ExecuteFaceTemplateResultSDKFunction(
            new Callable<FSDK.FSDK_FaceTemplate>() {
                public int call(final FSDK.FSDK_FaceTemplate result) {
                    return FSDK.GetFaceTemplate(new Image(image), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void GetFaceTemplateInRegion(final Integer image, final ReadableMap position, final Promise promise) {
        ExecuteFaceTemplateResultSDKFunction(
            new Callable<FSDK.FSDK_FaceTemplate>() {
                public int call(final FSDK.FSDK_FaceTemplate result) {
                    return FSDK.GetFaceTemplateInRegion(new Image(image), ReadableMapToFacePosition(position), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void GetFaceTemplateUsingFeatures(final Integer image, final ReadableArray features, final Promise promise) {
        ExecuteFaceTemplateResultSDKFunction(
            new Callable<FSDK.FSDK_FaceTemplate>() {
                public int call(final FSDK.FSDK_FaceTemplate result) {
                    return FSDK.GetFaceTemplateUsingFeatures(new Image(image), ReadableArrayToFeatures(features), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void GetFaceTemplateUsingEyes(final Integer image, final ReadableArray eyes, final Promise promise) {
        ExecuteFaceTemplateResultSDKFunction(
            new Callable<FSDK.FSDK_FaceTemplate>() {
                public int call(final FSDK.FSDK_FaceTemplate result) {
                    return FSDK.GetFaceTemplateUsingEyes(new Image(image), ReadableArrayToFeatures(eyes), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void MatchFaces(final String base1, final String base2, final Promise promise) {
        final FSDK.FSDK_FaceTemplate template1 = Base64ToTemplate(base1);
        final FSDK.FSDK_FaceTemplate template2 = Base64ToTemplate(base2);
        ExecuteFloatResultSDKFunction(
            new Callable<float[]>() {
                public int call(final float[] result) {
                    return FSDK.MatchFaces(template1, template2, result);
                }
            },
        "similarity", promise);
    }

    @ReactMethod
    public void GetMatchingThresholdAtFAR(final Double value, final Promise promise) {
        ExecuteFloatResultSDKFunction(
            new Callable<float[]>() {
                public  int call(final float[] result) {
                    return FSDK.GetMatchingThresholdAtFAR(value.floatValue(), result);
                }
            },
        "threshold", promise);
    }

    @ReactMethod
    public void GetMatchingThresholdAtFRR(final Double value, final Promise promise) {
        ExecuteFloatResultSDKFunction(
            new Callable<float[]>() {
                public int call(final float[] result) {
                    return FSDK.GetMatchingThresholdAtFRR(value.floatValue(), result);
                }
            },
        "threshold", promise);
    }

    @ReactMethod
    public void CreateTracker(final Promise promise) {
        ExecuteCreateTrackerSDKFunction(
            new Callable<Tracker>() {
                public int call(final Tracker result) {
                    return FSDK.CreateTracker(result);
                }
            },
        promise);
    }

    @ReactMethod
    public void FreeTracker(final Integer tracker, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.FreeTracker(new Tracker(tracker));
                }
            },
        promise);
    }

    @ReactMethod
    public void ClearTracker(final Integer tracker, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.ClearTracker(new Tracker(tracker));
                }
            },
        promise);
    }

    @ReactMethod
    public void SetTrackerParameter(final Integer tracker, final String name, final String value, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.SetTrackerParameter(new Tracker(tracker), name, value);
                }
            },
        promise);
    }

    @ReactMethod
    public void SetTrackerMultipleParameters(final Integer tracker, final String parameters, final Promise promise) {
        ExecuteIntegerResultSDKFunction(
            new Callable<int[]>() {
                public int call(final int[] result) {
                    return FSDK.SetTrackerMultipleParameters(new Tracker(tracker), parameters, result);
                }
            },
        "errorPosition", promise);
    }

    @ReactMethod
    public void GetTrackerParameter(final Integer tracker, final String name, final Integer maxSize, final Promise promise) {
        ExecuteStringResultSDKFunction(
            new Callable<String[]>() {
                public int call(final String[] result) {
                    return FSDK.GetTrackerParameter(new Tracker(tracker), name, result, maxSize.intValue());
                }
            },
        "value", promise);
    }

    @ReactMethod
    public void FeedFrame(final Integer tracker, final Integer index, final Integer image, final Integer maxFaces, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final long[] count = new long[1];
                    final long[] ids = new long[maxFaces];
                    final int errorCode = FSDK.FeedFrame(new Tracker(tracker), index.longValue(), new Image(image), count, ids);
                    WritableArray result = new WritableNativeArray();
                    for (int i = 0; i < count[0]; ++i)
                        result.pushInt((int)ids[i]);
                    map.putArray("ids", result);
                    return errorCode;
                }
            },
        promise);
    }

    @ReactMethod
    public void GetTrackerEyes(final Integer tracker, final Integer index, final Integer id, final Promise promise) {
        ExecuteFeaturesResultSDKFunction(
            new Callable<FSDK.FSDK_Features>() {
                public int call(final FSDK.FSDK_Features result) {
                    return FSDK.GetTrackerEyes(new Tracker(tracker), index.longValue(), id.longValue(), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void GetTrackerFacialFeatures(final Integer tracker, final Integer index, final Integer id, final Promise promise) {
        ExecuteFeaturesResultSDKFunction(
            new Callable<FSDK.FSDK_Features>() {
                public int call(final FSDK.FSDK_Features result) {
                    return FSDK.GetTrackerFacialFeatures(new Tracker(tracker), index.longValue(), id.longValue(), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void GetTrackerFacePosition(final Integer tracker, final Integer index, final Integer id, final Promise promise) {
        ExecuteFacePositionResultSDKFunction(
            new Callable<FSDK.TFacePosition>() {
                public int call(final FSDK.TFacePosition result) {
                    return FSDK.GetTrackerFacePosition(new Tracker(tracker), index.longValue(), id.longValue(), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void LockID(final Integer tracker, final Integer id, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.LockID(new Tracker(tracker), id.longValue());
                }
            },
        promise);
    }

    @ReactMethod
    public void UnlockID(final Integer tracker, final Integer id, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.UnlockID(new Tracker(tracker), id.longValue());
                }
            },
        promise);
    }

    @ReactMethod
    public void PurgeID(final Integer tracker, final Integer id, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.PurgeID(new Tracker(tracker), id.longValue());
                }
            },
        promise);
    }

    @ReactMethod
    public void SetName(final Integer tracker, final Integer id, final String name, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.SetName(new Tracker(tracker), id.longValue(), name);
                }
            },
        promise);
    }

    @ReactMethod
    public void GetName(final Integer tracker, final Integer id, final Integer maxSize, final Promise promise) {
        ExecuteStringResultSDKFunction(
            new Callable<String[]>() {
                public int call(final String[] result) {
                    return FSDK.GetName(new Tracker(tracker), id.longValue(), result, maxSize);
                }
            },
        "name", promise);
    }

    @ReactMethod
    public void GetAllNames(final Integer tracker, final Integer id, final Integer maxSize, final Promise promise) {
        ExecuteStringResultSDKFunction(
            new Callable<String[]>() {
                public int call(final String[] result) {
                    return FSDK.GetAllNames(new Tracker(tracker), id.longValue(), result, maxSize);
                }
            },
        "names", promise);
    }

    @ReactMethod
    public void GetIDReassignment(final Integer tracker, final Integer id, final Promise promise) {
        ExecuteLongResultSDKFunction(
            new Callable<long[]>() {
                public int call(final long[] result) {
                    return FSDK.GetIDReassignment(new Tracker(tracker), id.longValue(), result);
                }
            },
        "reassignedId", promise);
    }

    @ReactMethod
    public void GetSimilarIDCount(final Integer tracker, final Integer id, final Promise promise) {
        ExecuteLongResultSDKFunction(
            new Callable<long[]>() {
                public int call(final long[] result) {
                    return FSDK.GetSimilarIDCount(new Tracker(tracker), id.longValue(), result);
                }
            },
        "count", promise);
    }

    @ReactMethod
    public void GetSimilarIDList(final Integer tracker, final Integer id, final Integer count, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    final long[] ids = new long[count];
                    final int errorCode = FSDK.GetSimilarIDList(new Tracker(tracker), id.longValue(), ids);
                    final WritableArray result = new WritableNativeArray();
                    for (final long id : ids)
                        result.pushInt((int)id);
                    map.putArray("ids", result);
                    return errorCode;
                }
            },
        promise);
    }

    @ReactMethod
    public void SaveTrackerMemoryToFile(final Integer tracker, final String filename, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.SaveTrackerMemoryToFile(new Tracker(tracker), filename);
                }
            },
        promise);
    }

    @ReactMethod
    public void LoadTrackerMemoryFromFile(final String filename, final Promise promise) {
        ExecuteCreateTrackerSDKFunction(
            new Callable<Tracker>() {
                public int call(final Tracker result) {
                    return FSDK.LoadTrackerMemoryFromFile(result, filename);
                }
            },
        promise);
    }

    @ReactMethod
    public void GetTrackerMemoryBufferSize(final Integer tracker, final Promise promise) {
        ExecuteLongResultSDKFunction(
            new Callable<long[]>() {
                public int call(final long[] result) {
                    return FSDK.GetTrackerMemoryBufferSize(new Tracker(tracker), result);
                }
            }, 
        "bufferSize", promise);
    }

    @ReactMethod
    public void SaveTrackerMemoryToBuffer(final Integer tracker, final Integer bufferSize, final Promise promise) {
        ExecuteByteBufferResultSDKFunction(
            new Callable<byte[]>() {
                public int call(final byte[] result) {
                    return FSDK.SaveTrackerMemoryToBuffer(new Tracker(tracker), result);
                }
            }, 
        "buffer", bufferSize, promise);
    }

    @ReactMethod
    public void LoadTrackerMemoryFromBuffer(final String base64, final Promise promise) {
        final byte[] buffer = Base64.decode(base64, Base64.DEFAULT);
        ExecuteCreateTrackerSDKFunction(
            new Callable<Tracker>() {
                public int call(final Tracker result) {
                    return FSDK.LoadTrackerMemoryFromBuffer(result, buffer);
                }
            },
        promise);
    }

    @ReactMethod
    public void GetTrackerFacialAttribute(final Integer tracker, final Integer index, final Integer id, 
        final String name, final Integer maxSize, final Promise promise) {
        ExecuteStringResultSDKFunction(
            new Callable<String[]>() {
                public int call(final String[] result) {
                    return FSDK.GetTrackerFacialAttribute(new Tracker(tracker), index.longValue(), id.longValue(), 
                        name, result, maxSize);
                }
            },
        "values", promise);
    }

    @ReactMethod
    public void DetectFacialAttributeUsingFeatures(final Integer image, final ReadableArray features,
        final String name, final Integer maxSize, final Promise promise) {
        ExecuteStringResultSDKFunction(
            new Callable<String[]>() {
                public int call(final String[] result) {
                    return FSDK.DetectFacialAttributeUsingFeatures(new Image(image), ReadableArrayToFeatures(features),
                        name, result, maxSize);
                }
            },
        "values", promise);
    }

    @ReactMethod
    public void GetValueConfidence(final String attributeValues, final String value, final Promise promise) {
        ExecuteFloatResultSDKFunction(
            new Callable<float[]>() {
                public int call(final float[] result) {
                    return FSDK.GetValueConfidence(attributeValues, value, result);
                }
            },
        "confidence", promise);
    }

    @ReactMethod
    public void SetHTTPProxy(final String address, final Integer port, final String username, final String password, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.SetHTTPProxy(address, port.shortValue(), username, password);
                }
            },
        promise);
    }

    @ReactMethod
    public void OpenIPVideoCamera(final Integer compression, final String url, final String username, final String password, 
        final Integer timeout, final Promise promise) {
        ExecuteCreateCameraSDKFunction(
            new Callable<Camera>() {
                public int call(final Camera result) {
                    return FSDK.OpenIPVideoCamera(new VideoCompressionType(compression), url, username, password, timeout.intValue(), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void CloseVideoCamera(final Integer camera, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.CloseVideoCamera(new Camera(camera));
                }
            },
        promise);
    }

    @ReactMethod
    public void GrabFrame(final Integer camera, final Promise promise) {
        if (camera < 0) {
            if (FaceSDKCameraView.Instance != null) {
                FaceSDKCameraView.Instance.requestFrame(promise);
                return;
            }
            rejectPromise(promise, FSDK.FSDKE_CAMERA_INDEX_DOES_NOT_EXIST);
            return;
        }
        ExecuteCreateImageSDKFunction(
            new Callable<Image>() {
                public int call(final Image result) {
                    return FSDK.GrabFrame(new Camera(camera), result);
                }
            },
        promise);
    }

    @ReactMethod
    public void InitializeCapturing(final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.InitializeCapturing();
                }
            },
        promise);
    }

    @ReactMethod
    public void FinalizeCapturing(final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.FinalizeCapturing();
                }
            },
        promise);
    }

    @ReactMethod
    public void SetParameter(final String name, final String value, final Promise promise) {
        ExecuteSDKFunction(
            new SDKFunction() {
                public int invoke(final WritableMap map) {
                    return FSDK.SetParameter(name, value);
                }
            },
        promise);
    }

    @ReactMethod
    public void SetParameters(final String parameters, final Promise promise) {
        ExecuteIntegerResultSDKFunction(
            new Callable<int[]>() {
                public int call(final int[] result) {
                    return FSDK.SetParameters(parameters, result);
                }
            },
        "errorPosition", promise);
    }

}
