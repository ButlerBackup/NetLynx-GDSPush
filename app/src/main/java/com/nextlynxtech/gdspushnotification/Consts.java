package com.nextlynxtech.gdspushnotification;

public class Consts {

    public static String WEB_API = "http://gds.cloudapp.net/wcfFileUpload/svcAppMessaging.svc"; //push

    public static final boolean DEBUG = false;

    public static final int CAMERA_PHOTO_REQUEST = 1337;
    public static final int CAMERA_VIDEO_REQUEST = 1338;
    public static final int CAMERA_PICK_IMAGE_FROM_GALLERY = 1339;
    public static final int SETTING_RESTART_CODE = 1340;

    public static final String CAMERA_SAVED_INSTANCE = "cameraPicture";

    public static final String IMAGE_GALLERY_PASS_EXTRAS = "galleryFileName";
    public static final String IMAGE_CAMERA_PASS_EXTRAS = "cameraFileName";

    public static final String VIDEO_GALLERY_PASS_EXTRAS = "galleryFileName";
    public static final String VIDEO_CAMERA_PASS_EXTRAS = "cameraFileName";
    public static final String VIDEO_CAMERA_PASS_EXTRAS_PURE = "cameraFileNamePure";

    public static final String TIMELINE_ITEM_SELECTED_FROM_MAINACTIVITY = "timelineItem";
    //public static final String WEB_API_URL = "https://www.cloudmessage.me/svcFileUploadWebTier/svcFileUpload.svc";
    public static final String WEB_API_URL = "http://gds.cloudapp.net/wcfFileUpload/svcFileUpload.svc";

    public static final String REGISTER_LOGIN_ID = "loginID";
    public static final String REGISTER_USER_NAME = "username";
    public static final String REGISTER_MOBILE_NUMBER = "mobileNumber";
    public static final String REGISTER_PASSWORD = "password";
    public static final String REGISTER_UDID = "udid";
    public static final String REGISTER_USER_GROUP = "group";
    public static final String REGISTER_CREATE_MESSAGE = "CreateMessage";
    public static final String REGISTER_PHOTO_UPLOAD = "PhotoUpload";
    public static final String REGISTER_RECEIVE_MESSAGE = "ReceiveMessage";

    public static final String SETTINGS_RESTART = "restart";

    public static final String LOCATION_ERROR = "Unable to get location. Turn on GPS";
    public static final String LOCATION_LOADING = "Loading location...";


    //0 failed
    //1 pass
    // 2 uploading

    public static final int SERVICE_VIDEO_UPLOAD = 1337;
    public static final int SERVICE_PHOTO_UPLOAD = 1338;
}
