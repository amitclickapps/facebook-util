package fbconnect.controller;

/**
 * Created by amit on 4/2/17.
 */

public class WebEndPoint {
    public static final String USER_ID_KEY = "user_id";
    public static final String ALBUM_ID_KEY = "album_id";


    protected static String ACCESS_TOKEN_KEY = "access_token";
    protected static String FIELD = "fields";
    protected static final String ID = "id";
    protected static String BASE = "https://graph.facebook.com/v2.10/";
    protected static final String PROFILE = "{" + ID + "}";
    protected static final String FRIEND = "{" + ID + "}/friends";
    protected static final String ALBUM = "{" + ID + "}/albums";
    protected static final String PHOTO = "{" + ID + "}/photos";

    protected static int PROFILE_TASK_ID = 1;
    protected static int FRIEND_TASK_ID = 2;
    protected static int ALBUM_TASK_ID = 3;
    protected static int PHOTO_TASK_ID = 4;
}
