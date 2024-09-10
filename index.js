import { NativeModules, requireNativeComponent, findNodeHandle, UIManager } from 'react-native';
import { encode, decode } from 'base64-arraybuffer';
import React, { Component } from 'react';

const { FaceSDK } = NativeModules;

const ERROR = FaceSDK.ERROR;
const FEATURE = FaceSDK.FEATURE;
const IMAGEMODE = FaceSDK.IMAGEMODE;
const FACIAL_FEATURE_COUNT = Object.keys(FEATURE).length;
const VIDEOCOMPRESSIONTYPE = FaceSDK.VIDEOCOMPRESSIONTYPE;

const NativeCameraWrapper = requireNativeComponent('CameraWrapper');

const ON_ERROR = {
    'SILENT': 0,
    'ALERT':  1,
    'REJECT': 2,
    'RETURN': 3,
};

const defaultFacePosition = { facePosition: { xc: 0, yc: 0, w: 0, angle: 0 } };

export class FSDKError extends Error {

    constructor(message, errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}

async function executeSDKFunction(func, result, ...args) {
    var res;
    try {
        res = await FaceSDK[func](...args);
    } catch (e) {
        if (FSDK.onError == ON_ERROR.SILENT)
            return result();

        const errorCode = e.userInfo.errorCode;
        if (FSDK.onError == ON_ERROR.RETURN)
            return errorCode;

        var msg = `Fuction FSDK.${func} failed with error ${e.code}: ${errorCode}.\nArguments were ${JSON.stringify(args)}`;
        if (errorCode == ERROR.NOT_ACTIVATED)
            msg += '\nPlease call FSDK.ActivateLibrary(yourLicenseKey) before using any other function.';

        if (FSDK.onError == ON_ERROR.ALERT) {
            alert(msg);
            return result();
        }

        if (FSDK.onError == ON_ERROR.REJECT)
            return Promise.reject(new FSDKError(msg, errorCode));
        
        return Promise.reject(new Error(`Unknown ON_ERROR value: ${onReject}`));
    }

    return result(res);
}

function returnImage(result = { image: -1 }) {
    return new Image(result.image);
}

function returnTracker(result = { tracker: -1 }) {
    return new Tracker(result.tracker);
}

function returnCamera(result = { camera: -1 }) {
    return new Camera(result.camera);
}

function returnFacePostion(result = { facePosition: { xc: 0, yc: 0, w: 0, angle: 0 } }) {
    return result.facePosition;
}

function returnUndefined(result = undefined) {
    return undefined;
}

function returnDefault(name, value) {
    return (result = { [name]: value }) => result[name];
}

const returnTemplate = returnDefault('template', '');
const returnFeatures = returnDefault('features', {});

async function returnBuffer(func, ...args) {
    try {
        const base64 = await func(...args);
        return base64 == '' ? new ArrayBuffer(0) : decode(base64);
    } catch(e) {
        return Promise.reject(e);
    }
}

export class CameraWrapper extends Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.handle = findNodeHandle(this.ref);
    }

    setDefault() {
        UIManager.dispatchViewManagerCommand(this.handle, 0, null);
    }

    grabFrame() {
        this.setDefault();
        return executeSDKFunction('GrabFrame', returnImage, -1);
    }

    render() {
        return <NativeCameraWrapper {...this.props} ref={ ref => this.ref = ref } />;
    }

}

export class Image {

    constructor(handle = -1) {
        this.handle = handle;
    }

    static Empty() {
        return executeSDKFunction('CreateEmptyImage', returnImage);
    }

    static FromFile(filename) {
        return executeSDKFunction('LoadImageFromFile', returnImage, filename);
    }

    static FromFileWithAlpha(filename) {
        return executeSDKFunction('LoadImageFromFileWithAlpha', returnImage, filename)
    }

    static FromBase64(base64, width, height, scanLine, imageMode) {
        return executeSDKFunction('LoadImageFromBuffer', returnImage, base64, width, height, scanLine, imageMode);
    }

    static FromBuffer(buffer, width, height, scanLine, imageMode) {
        return Image.FromBase64(encode(buffer), width, height, scanLine, imageMode);
    }

    static FromJpegBase64(base64) {
        return executeSDKFunction('LoadImageFromJpegBuffer', returnImage, base64);
    }

    static FromJpegBuffer(buffer) {
        return Image.FromJpegBase64(encode(buffer));
    }

    static FromPngBase64(base64) {
        return executeSDKFunction('LoadImageFromPngBuffer', returnImage, base64);
    }

    static FromPngBuffer(buffer) {
        return Image.FromPngBase64(encode(buffer));
    }

    static FromPngBase64WithAlpha(base64) {
        return executeSDKFunction('LoadImageFromPngBufferWithAlpha', returnImage, base64);
    }

    static FromPngBufferWithAlpha(buffer) {
        return Image.FromPngBase64WithAlpha(encode(buffer));
    }

    saveToFile(filename) {
        return executeSDKFunction('SaveImageToFile', returnUndefined, filename, this.handle);
    }

    getWidth() {
        return executeSDKFunction('GetImageWidth', returnDefault('width', 0), this.handle);
    }

    getHeight() {
        return executeSDKFunction('GetImageHeight', returnDefault('height', 0), this.handle);
    }

    getBufferSize(imageMode) {
        return executeSDKFunction('GetImageBufferSize', returnDefault('bufferSize', 0), this.handle, imageMode);
    }

    async saveToBase64(imageMode) {
        try {
            const bufferSize = await this.getBufferSize(imageMode);
            return executeSDKFunction('SaveImageToBuffer', returnDefault('buffer', ''), this.handle, imageMode, bufferSize);
        } catch(e) {
            return Promise.reject(e);
        }
    }

    saveToBuffer(imageMode) {
        return returnBuffer(this.saveToBase64, imageMode);
    }

    detectFace() {
        return executeSDKFunction('DetectFace', returnFacePostion, this.handle);
    }

    detectMultipleFaces(maxFaces = 100) {
        return executeSDKFunction('DetectMultipleFaces', returnDefault('faces', []), this.handle, maxFaces);
    }

    detectFacialFeatures() {
        return executeSDKFunction('DetectFacialFeatures', returnFeatures, this.handle);
    }

    detectFacialFeaturesInRegion(facePosition) {
        return executeSDKFunction('DetectFacialFeaturesInRegion', returnFeatures, this.handle, facePosition);
    }

    detectEyes() {
        return executeSDKFunction('DetectEyes', returnFeatures, this.handle);
    }

    detectEyesInRegion(facePosition) {
        return executeSDKFunction('DetectEyesInRegion', returnFeatures, this.handle, facePosition);
    }

    copy() {
        return executeSDKFunction('CopyImage', returnImage, this.handle);
    }

    resize(ratio) {
        return executeSDKFunction('ResizeImage', returnImage, this.handle);
    }

    rotate90(multiplier) {
        return executeSDKFunction('RotateImage90', returnImage, this.handle, multiplier);
    }

    rotate(angle) {
        return executeSDKFunction('RotateImage', returnImage, this.handle, angle);
    }

    rotateCenter(angle, x, y) {
        return executeSDKFunction('RotateImageCenter', returnImage, this.handle, angle, x, y);
    }

    copyRect(x1, y1, x2, y2) {
        return executeSDKFunction('CopyRect', returnImage, this.handle, x1, y1, x2, y2);
    }

    copyRectReplicateBorder(x1, y1, x2, y2) {
        return executeSDKFunction('CopyRectReplicateBorder', returnImage, this.handle, x1, y1, x2, y2);
    }

    mirror(vertical) {
        return executeSDKFunction('MirrorImage', returnUndefined, this.handle, vertical);
    }

    extractFace(features) {
        return executeSDKFunction('ExtractFaceImage', (result = { image: new Image(-1), features: {} }) => result, this.handle, features);
    }

    getFaceTemplateBase64() {
        return executeSDKFunction('GetFaceTemplate', returnTemplate, this.handle);
    }

    getFaceTemplate() {
        return returnBuffer(this.getFaceTemplateBase64);
    }

    getFaceTemplateInRegionBase64(facePosition) {
        return executeSDKFunction('GetFaceTemplateInRegion', returnTemplate, this.handle, facePosition);
    }

    getFaceTemplateInRegion(facePosition) {
        return returnBuffer(this.getFaceTemplateInRegionBase64, facePosition);
    }

    getFaceTemplateUsingFeaturesBase64(features) {
        return executeSDKFunction('GetFaceTemplateUsingFeatures', returnTemplate, this.handle, features);
    }

    getFaceTemplateUsingFeatures(features) {
        return returnBuffer(this.getFaceTemplateUsingFeaturesBase64, features);
    }

    getFaceTemplateUsingEyesBase64(eyes) {
        return executeSDKFunction('GetFaceTemplateUsingEyes', returnTemplate, this.handle, eyes);
    }

    getFaceTemplateUsingEyes(eyes) {
        return returnBuffer(this.getFaceTemplateUsingEyesBase64, eyes);
    }

    detectFacialAttributeUsingFeatures(features, name, maxSize = 256) {
        return executeSDKFunction('DetectFacialAttributeUsingFeatures', returnDefault('values', ''), this.handle, features, name, maxSize);
    }

    free() {
        return executeSDKFunction('FreeImage', returnUndefined, this.handle);
    }

}

export class Tracker {

    constructor(handle = -1) {
        this.handle = handle;
    }

    static Create() {
        return executeSDKFunction('CreateTracker', returnTracker);
    }

    static FromFile(filename) {
        return executeSDKFunction('LoadTrackerMemoryFromFile', returnTracker, filename);
    }

    static FromBase64(base64) {
        return executeSDKFunction('LoadTrackerMemoryFromBuffer', returnTracker, base64);
    }

    static FromBuffer(buffer) {
        return executeSDKFunction('LoadTrackerMemoryFromBuffer', returnTracker, encode(buffer));
    }

    setParameter(name, value) {
        return executeSDKFunction('SetTrackerParameter', returnUndefined, this.handle, name, value);
    }

    setMultipleParameters(parameters) {
        return executeSDKFunction('SetTrackerMultipleParameters', returnDefault('errorPosition', 0), this.handle, parameters);
    }

    getParameter(name, maxSize = 100) {
        return executeSDKFunction('GetTrackerParameter', returnDefault('value', ''), this.handle, name, maxSize)
    }

    feedFrame(image, maxFaces = 1024, index = 0) {
        return executeSDKFunction('FeedFrame', returnDefault('ids', []), this.handle, index, image.handle, maxFaces);
    }

    getEyes(id, index = 0) {
        return executeSDKFunction('GetTrackerEyes', returnFeatures, this.handle, index, id);
    }

    getFacialFeatures(id, index = 0) {
        return executeSDKFunction('GetTrackerFacialFeatures', returnFeatures, this.handle, index, id);
    }

    getFacePosition(id, index = 0) {
        return executeSDKFunction('GetTrackerFacePosition', returnFacePostion, this.handle, index, id);
    }

    lockID(id) {
        return executeSDKFunction('LockID', returnUndefined, this.handle, id);
    }

    purgeID(id) {
        return executeSDKFunction('PurgeID', returnUndefined, this.handle, id);
    }

    unlockID(id) {
        return executeSDKFunction('UnlockID', returnUndefined, this.handle, id);
    }

    setName(id, name) {
        return executeSDKFunction('SetName', returnUndefined, this.handle, id, name);
    }

    getName(id, maxSize = 256) {
        return executeSDKFunction('GetName', returnDefault('name', ''), this.handle, id, maxSize);
    }

    getAllNames(id, maxSize = 256) {
        return executeSDKFunction('GetAllNames', returnDefault('names', ''), this.handle, id, maxSize);
    }

    getIDReassignment(id) {
        return executeSDKFunction('GetIDReassignment', returnDefault('reassignedId', -1), this.handle, id);
    }

    getSimilarIDCount(id) {
        return executeSDKFunction('GetSimilarIDCount', returnDefault('count', 0), this.handle, id);
    }

    async getSimilarIDList(id) {
        try {
            const count = await this.getSimilarIDCount(id);
            return executeSDKFunction('GetSimilarIDList', returnDefault('ids', {}), this.handle, id, count);
        } catch(e) {
            return Promise.reject(e);
        }
    }

    saveToFile(filename) {
        return executeSDKFunction('SaveTrackerMemoryToFile', returnUndefined, this.handle, filename);
    }

    getBufferSize() {
        return executeSDKFunction('GetTrackerMemoryBufferSize', returnDefault('bufferSize', 0), this.handle);
    }

    async saveToBase64() {
        try {
            const bufferSize = await this.getBufferSize();
            return executeSDKFunction('SaveTrackerMemoryToBuffer', returnDefault('buffer', ''), this.handle, bufferSize);
        } catch(e) {
            return Promise.reject(e);
        }
    }

    saveToBuffer() {
        return returnBuffer(this.saveToBase64);
    }

    getFacialAttribute(id, name, index = 0, maxSize = 256) {
        return executeSDKFunction('GetTrackerFacialAttribute', returnDefault('values', ''), this.handle, index, id, name, maxSize);
    }

    clear() {
        return executeSDKFunction('ClearTracker', returnUndefined, this.handle);
    }

    free() {
        return executeSDKFunction('FreeTracker', returnUndefined, this.handle);
    }

}

export class Camera {

    constructor(handle = -1) {
        this.handle = handle;
    }

    static SetHTTPProxy(address, port, username, password) {
        return executeSDKFunction('SetHTTPProxy', returnUndefined, address, port, username, password);
    }

    static OpenIPVideoCamera(comression, url, username, password, timeout) {
        return executeSDKFunction('OpenIPVideoCamera', returnCamera, comression, url, username, password, timeout);
    }

    static InitializeCapturing() {
        return executeSDKFunction('InitializeCapturing', returnUndefined);
    }

    static FinalizeCapturing() {
        return executeSDKFunction('FinalizeCapturing', returnUndefined);
    }

    grabFrame() {
        return executeSDKFunction('GrabFrame', returnImage, this.handle);
    }

    close() {
        return executeSDKFunction('CloseVideoCamera', returnUndefined, this.handle);
    }

}

export default class FSDK {

    static FaceSDK = FaceSDK;

    static Image = Image;
    static Camera = Camera;
    static Tracker = Tracker;
    static CameraWrapper = CameraWrapper;

    static ERROR = ERROR;
    static FEATURE = FEATURE;
    static IMAGEMODE = IMAGEMODE;
    static FACIAL_FEATURE_COUNT = FACIAL_FEATURE_COUNT;
    static VIDEOCOMPRESSIONTYPE = VIDEOCOMPRESSIONTYPE;

    static ON_ERROR = ON_ERROR;
    static onError = ON_ERROR.REJECT;

    static ActivateLibrary(licenseKey) {
        return executeSDKFunction('ActivateLibrary', returnUndefined, licenseKey);
    }

    static Initialize() {
        return executeSDKFunction('Initialize', returnUndefined);
    }

    static Finalize() {
        return executeSDKFunction('Finalize', returnUndefined);
    }

    static async ActivateAndInitalize(licenseKey) {
        try {
            await ActivateLibrary(licenseKey);
            await Initialize();
        } catch(e) {
            return Promise.reject(e);
        }
    }

    static GetLicenseInfo() {
        return executeSDKFunction('GetLicenseInfo', returnDefault('licenseInfo', ''));
    }

    static SetNumThreads(threads) {
        return executeSDKFunction('SetNumThreads', returnUndefined, threads);
    }

    static GetNumThreads() {
        return executeSDKFunction('GetNumThreads', returnDefault('numThreads', 0));
    }

    static CreateEmptyImage() {
        return Image.Empty();
    }

    static FreeImage(image) {
        return image.free();
    }

    static LoadImageFromFile(filename) {
        return Image.FromFile(filename);
    }

    static LoadImageFromFileWithAlpha(filename) {
        return Image.FromFileWithAlpha(filename);
    }

    static SaveImageToFile(image, filename) {
        return image.saveToFile(filename);
    }

    static SetJpegCompressionQuality(quality) {
        return executeSDKFunction('SetJpegCompressionQuality', undefined, quality);
    }

    static GetImageWidth(image) {
        return image.width();
    }

    static GetImageHeight(image) {
        return image.height();
    }

    static LoadImageFromBase64(base64, width, height, scanLine, imageMode) {
        return Image.FromBase64(base64, width, height, scanLine, imageMode);
    }

    static LoadImageFromBuffer(buffer, width, height, scanLine, imageMode) {
        return Image.FromBuffer(buffer, width, height, scanLine, imageMode);
    }

    static SaveImageToBase64(image, imageMode) {
        return image.saveToBase64(imageMode);
    }

    static SaveImageToBuffer(image, imageMode) {
        return image.saveToBuffer(imageMode);
    }

    static LoadImageFromJpegBase64(base64) {
        return Image.FromJpegBase64(base64);
    }

    static LoadImageFromJpegBuffer(buffer) {
        return Image.FromJpegBuffer(buffer);
    }

    static LoadImageFromPngBase64(base64) {
        return Image.FromPngBase64(base64);
    }

    static LoadImageFromPngBuffer(buffer) {
        return Image.FromPngBuffer(buffer);
    }

    static LoadImageFromPngBase64WithAlpha(base64) {
        return Image.FromPngBase64WithAlpha(base64);
    }

    static LoadImageFromPngBufferWithAlpha(buffer) {
        return Image.FromPngBufferWithAlpha(buffer);
    }

    static DetectFace(image) {
        return image.detectFace();
    }

    static DetectMultipleFaces(image, maxFaces = 100) {
        return image.detectMultipleFaces(maxFaces);
    }

    static SetFaceDetectionParameters(handleArbitraryRotations, determineFaceRotationAngle, internalResizeWidth) {
        return executeSDKFunction('SetFaceDetectionParameters', returnUndefined, 
            handleArbitraryRotations, determineFaceRotationAngle, internalResizeWidth);
    }

    static SetFaceDetectionThreshold(threshold) {
        return executeSDKFunction('SetFaceDetectionThreshold', returnUndefined, threshold);
    }

    static GetDetectedFaceConfidence() {
        return executeSDKFunction('GetDetectedFaceConfidence', returnDefault('confidence', 0));
    }

    static DetectFacialFeatures(image) {
        return image.detectFacialFeatures();
    }

    static DetectFacialFeaturesInRegion(image, facePosition) {
        return image.detectFacialFeaturesInRegion(facePosition);
    }

    static DetectEyes(image) {
        return image.detectEyes();
    }

    static DetectEyesInRegion(image, facePosition) {
        return image.detectEyesInRegion(facePosition);
    }

    static CopyImage(image) {
        return image.copy();
    }

    static ResizeImage(image, ratio) {
        return image.resize(ratio);
    }

    static RotateImage90(image, multiplier) {
        return image.rotate90(multiplier);
    }

    static RotateImage(image, angle) {
        return image.rotate(angle);
    }

    static RotateImageCenter(image, angle, x, y) {
        return image.rotateCenter(angle, x, y);
    }

    static CopyRect(image, x1, y1, x2, y2) {
        return image.copyRect(x1, y1, x2, y2);
    }

    static CopyRectReplicateBorder(image, x1, y1, x2, y2) {
        return image.copyRectReplicateBorder(x1, y1, x2, y2);
    }

    static MirrorImage(image, vertical) {
        return image.mirror(vertical);
    }

    static ExtractFaceImage(image, features) {
        return image.extractFace(features);
    }

    static GetFaceTemplateBase64(image) {
        return image.getFaceTemplateBase64();
    }

    static GetFaceTemplate(image) {
        return image.getFaceTemplate();
    }

    static GetFaceTemplateInRegionBase64(image, facePosition) {
        return image.getFaceTemplateInRegionBase64(facePosition);
    }

    static GetFaceTemplateInRegion(image, facePosition) {
        return image.getFaceTemplateInRegion(facePosition);
    }

    static GetFaceTemplateUsingFeaturesBase64(image, features) {
        return image.getFaceTemplateUsingFeaturesBase64(features);
    }

    static GetFaceTemplateUsingFeatures(image, features) {
        return image.getFaceTemplateUsingFeatures(features);
    }

    static GetFaceTemplateUsingEyesBase64(image, eyes) {
        return image.getFaceTemplateUsingEyesBase64(eyes);
    }

    static GetFaceTemplateUsingEyes(image, eyes) {
        return image.getFaceTemplateUsingEyes(eyes);
    }

    static MatchFacesBase64(template1, template2) {
        return executeSDKFunction('MatchFaces', returnDefault('similarity', 0), template1, template2);
    }

    static MatchFaces(template1, template2) {
        return executeSDKFunction('MatchFaces', returnDefault('similarity', 0), encode(template1), encode(template2));
    }

    static GetMatchingThresholdAtFAR(value) {
        return executeSDKFunction('GetMatchingThresholdAtFAR', returnDefault('threshold', 0), value);
    }

    static GetMatchingThresholdAtFRR(value) {
        return executeSDKFunction('GetMatchingThresholdAtFRR', returnDefault('threshold', 0), value);
    }

    static CreateTracker() {
        return Tracker.Create();
    }

    static ClearTracker(tracker) {
        return tracker.clear();
    }

    static FreeTracker(tracker) {
        return tracker.free();
    }

    static SetTrackerParameter(tracker, name, value) {
        return tracker.setParameter(name, value);
    }

    static SetTrackerMultipleParameters(tracker, parameters) {
        return tracker.setMultipleParameters(parameters);
    }

    static GetTrackerParameter(tracker, name, maxSize = 100) {
        return tracker.getParameter(name, maxSize);
    }

    static FeedFrame(tracker, image, maxFaces = 5, index = 0) {
        return tracker.feedFrame(image, maxFaces, index);
    }

    static GetTrackerEyes(tracker, id, index = 0) {
        return tracker.getEyes(id, index);
    }

    static GetTrackerFacialFeatures(tracker, id, index = 0) {
        return tracker.getFacialFeatures(id, index);
    }

    static GetTrackerFacePosition(tracker, id, index = 0) {
        return tracker.getFacePosition(id, index);
    }

    static LockID(tracker, id) {
        return tracker.lockID(id);
    }

    static UnlockID(tracker, id) {
        return tracker.unlockID(id);
    }

    static PurgeID(tracker, id) {
        return tracker.purgeID(id);
    }

    static SetName(tracker, id, name) {
        return tracker.setName(id, name);
    }

    static GetName(tracker, id, maxSize = 256) {
        return tracker.getName(id, maxSize);
    }

    static GetAllNames(tracker, id, maxSize = 256) {
        return tracker.getAllNames(id, maxSize);
    }

    static GetIDReassignment(tracker, id) {
        return tracker.getIDReassignment(id);
    }

    static GetSimilarIDCount(tracker, id) {
        return tracker.getSimilarIDCount(id);
    }

    static GetSimilarIDList(tracker, id) {
        return tracker.getSimilarIDList(id);
    }

    static SaveTrackerMemoryToFile(tracker) {
        return tracker.saveToFile();
    }

    static LoadTrackerMemoryFromFile(filename) {
        return Tracker.FromFile(filename);
    }

    static GetTrackerMemoryBufferSize(tracker) {
        return tracker.getBufferSize();
    }

    static SaveTrackerMemoryToBase64(tracker) {
        return tracker.saveToBase64();
    }

    static SaveTrackerMemoryToBuffer(tracker) {
        return tracker.saveToBuffer();
    }

    static LoadTrackerMemoryFromBase64(base64) {
        return Tracker.FromBase64(base64);
    }

    static LoadTrackerMemoryFromBuffer(buffer) {
        return Tracker.FromBuffer(buffer);
    }

    static GetTrackerFacialAttribute(tracker, id, name, index = 0, maxSize = 256) {
        return tracker.getFacialAttribute(id, name, index, maxSize);
    }

    static DetectFacialAttributeUsingFeatures(image, features, name, maxSize = 256) {
        return image.detectFacialAttributeUsingFeatures(features, name, maxSize);
    }

    static GetValueConfidence(attributeValues, value) {
        return executeSDKFunction('GetValueConfidence', returnDefault('confidence', 0), attributeValues, value);
    }

    static SetHTTPProxy(address, port, username, password) {
        return Camera.SetHTTPProxy(address, port, username, password);
    }

    static OpenIPVideoCamera(comression, url, username, password, timeout) {
        return Camera.OpenIPVideoCamera(comression, url, username, password, timeout);
    }

    static CloseVideoCamera(camera) {
        return camera.close();
    }

    static GrabFrame(camera) {
        return camera.grabFrame();
    }

    static InitializeCapturing() {
        return Camera.InitializeCapturing();
    }

    static FinalizeCapturing() {
        return Camera.FinalizeCapturing();
    }

    static SetParameter(name, value) {
        return executeSDKFunction('SetParameter', returnUndefined, name, value);
    }

    static SetParameters(parameters) {
        return executeSDKFunction('SetParameters', returnDefault('errorPosition', 0), parameters);
    }

}

export { ERROR, FEATURE, IMAGEMODE, FACIAL_FEATURE_COUNT, VIDEOCOMPRESSIONTYPE, ON_ERROR };
