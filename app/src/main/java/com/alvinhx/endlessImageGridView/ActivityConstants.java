package com.alvinhx.endlessImageGridView;

/**
 * Constants class to store constant values
 * Created by solor on 2016-02-09.
 */
public class ActivityConstants {
    public static final String URL_GET_PHOTO_BASIC = "/photos";
    public static final String URL_GET_36_PHOTOS = URL_GET_PHOTO_BASIC + "?rpp=36";
    public static final String URL_IMAGE_SIZE_440_4 = "&image_size=440,4";
    public static final String URL_GET_PHOTO_THUMBNAIL440 = URL_GET_36_PHOTOS + URL_IMAGE_SIZE_440_4;


    public static final String TAG_IMG_URL = "img_url";
    public static final String TAG_IMG_HIRES_URL = "img_hi_res_url";
    public static final String TAG_IMG_ID = "img_id";
    public static final String TAG_IMG_NAME = "img_name";
    public static final String TAG_IMAGE_POSITION = "image_position";
    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_USER_NAME = "user_name";
    public static final String TAG_USER_PIC_URL = "user_pic_url";
    public static final String TAG_OFFSET = "offset";

    public static final String OB_GET_PHOTOS = "get_photo_bucket";
    public static final String OB_GET_PHOTOS_WITH_OFFSET = "get_photos_with_offset";
    public static final String OB_GET_PHOTOS_DETAIL = "get_photo_details";

    public static final String FRAGMENT_INDEX = "com.alvinhx.endlessImageGridView.FRAGMENT_INDEX";

}
