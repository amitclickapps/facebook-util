package net.android.jn.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import net.android.jn.model.ErrorModel;
import net.android.jn.model.FriendModel;
import net.android.jn.model.ProfileModel;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by clickapps on 16/6/15.
 */
public final class FacebookConnect {

    private static FacebookConnect mInstance;
    private CallbackManager callbackManager;
    private boolean isLogEnabled = false;
    private AccessToken token;
    private static final int TOKEN_EXPIRE = 700;
    private static final int SHARE_ERROR = 100;

    private FacebookConnect() {

    }

    /**
     * Initialize Facebook Sdk & Generate HashKey
     *
     * @param context
     */
    private FacebookConnect(Context context) {
        try {
            FacebookSdk.sdkInitialize(context);
            callbackManager = CallbackManager.Factory.create();
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                if (isLogEnabled) {
                    log("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    public static FacebookConnect getInstance(Context context) {
        return mInstance == null ? mInstance = new FacebookConnect(context) : mInstance;
    }

    /**
     * Login to Facebook
     *
     * @param activityContext set {@linkplain Activity} context
     * @param callback        set {@link OnProfileListner} fbconnect.callback
     */
    public void login(final Activity activityContext, final OnProfileListner callback) {
        LoginManager.getInstance().logInWithReadPermissions(activityContext, Arrays.asList("public_profile", "user_friends", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
                getUserProfile(callback);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                String msg = "";
                if (e.getCause() instanceof UnknownHostException) {
                    msg = activityContext.getString(R.string.error_internet_connection);
                } else if (e.getCause() instanceof TimeoutException
                        || e.getCause() instanceof SocketTimeoutException) {
                    msg = activityContext.getString(R.string.error_server_connection);
                } else {
                    msg = activityContext.getString(R.string.error_server_connection);
                }
                callback.onError(new ErrorModel(500, "FacebookException", msg));
            }
        });
    }

    /**
     * Login to Facebook with LoginButton
     *
     * @param loginButton
     * @param callback
     */
    public void login(final LoginButton loginButton, final OnProfileListner callback) {
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
                getUserProfile(callback);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                String msg = "";
                if (e.getCause() instanceof UnknownHostException) {
                    msg = loginButton.getContext().getString(R.string.error_internet_connection);
                } else if (e.getCause() instanceof TimeoutException
                        || e.getCause() instanceof SocketTimeoutException) {
                    msg = loginButton.getContext().getString(R.string.error_server_connection);
                } else {
                    msg = loginButton.getContext().getString(R.string.error_server_connection);
                }
                callback.onError(new ErrorModel(500, "FacebookException", msg));
            }
        });
    }

    public void logout() {
        LoginManager.getInstance().logOut();
    }

    /**
     * Get LoggedIn User Profile
     *
     * @param callback set {@link OnProfileListner} fbconnect.callback
     *                 Note, even if you request the email permission it is not guaranteed you will get an email address.
     *                 For example, if someone signed up for Facebook with a phone number instead of an email address, the email field may be empty.
     */
    public void getUserProfile(final OnProfileListner callback) {
        if (!isSessionCheck()) {
            callback.onError(setTokenExpire());
            return;
        }
        GraphRequest request = GraphRequest.newMeRequest(
                token, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        if (response.getError() != null) {
                            callback.onError(setError(response.getError()));
                        } else {
                            if (isLogEnabled) {
                                log("getUserProfile:", me.toString());
                            }

                            ProfileModel model = new Gson().fromJson(me.toString(), ProfileModel.class);
                            if (model != null)
                                callback.onUserProfile(model);
                        }
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,gender,about,email,picture.type(large),cover");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * Get Friend(s)
     *
     * @param callback set {@link OnFriendListener} fbconnect.callback
     *                 Provides access the list of friends that also use your app. These friends can be found on the friends edge on the user object.
     *                 <p/>
     *                 In order for a person to show up in one person's friend list, both people must have decided to share their list of friends with
     *                 your app and not disabled that permission during login. Also both friends must have been asked for user_friends during the login process.
     */
    public void getUserFriends(final OnFriendListener callback) {
        if (!isSessionCheck()) {
            callback.onError(setTokenExpire());
            return;
        }
        GraphRequest request = GraphRequest.newMyFriendsRequest(token, new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {

                if (graphResponse.getError() != null) {
                    callback.onError(setError(graphResponse.getError()));
                } else {
                    if (isLogEnabled) {
                        log("getUserFriends", jsonArray.toString());
                    }
                    List<FriendModel> mList = Arrays.asList(new Gson().fromJson(jsonArray.toString(), FriendModel[].class));
                    callback.onFriendList(mList);
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void getTagableFriends(String id, final OnFriendListener callback) {
        if (!isSessionCheck()) {
            callback.onError(setTokenExpire());
            return;
        }
        GraphRequest.newGraphPathRequest(token, "/" + id + "/taggable_friends", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                if (graphResponse.getError() != null) {
                    callback.onError(setError(graphResponse.getError()));
                } else {
                    if (isLogEnabled) {
                        log("getTagableFriends", graphResponse.toString());
                    }
//                    fbconnect.callback.
                }

            }
        }).executeAsync();

    }

    /**
     * Invite friend(s)
     *
     * @param activityContext set {@linkplain Activity} context
     * @param appLinkUrl      set appLinkUrl(generate aapLinkUrl @generate)(help @reference)
     * @param previewImageUrl set previewImageUrl that show on InviteDialog
     * @param callback        set {@link OnShareListener} fbconnect.callback
     * @reference http://stackoverflow.com/questions/30276800/invite-users-with-appinvitedialog
     * @generate https://developers.facebook.com/quickstarts/853332588093736/?platform=app-links-host
     */
    public void sendAppRequest(Activity activityContext, String appLinkUrl, String previewImageUrl, final OnShareListener callback) {
        if (AppInviteDialog.canShow()) {
            AppInviteDialog dialog = new AppInviteDialog(activityContext);
            dialog.registerCallback(callbackManager, new FacebookCallback<AppInviteDialog.Result>() {
                @Override
                public void onSuccess(AppInviteDialog.Result result) {
                    callback.onSuccess();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException e) {
                    callback.onError(new ErrorModel(SHARE_ERROR, e.getMessage(), e.getMessage()));
                }
            });
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            dialog.show(activityContext, content);
        }
    }

    /**
     * Share text to wall
     *
     * @param activityContext set {@linkplain Activity} context
     * @param title           set title
     * @param desc            set description
     * @param url             set url
     * @param imageUrl        set imageUrl
     * @param callback        set {@link OnShareListener} fbconnect.callback
     */
    public void share(Activity activityContext, String title, String desc, String url, String imageUrl, final OnShareListener callback) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareDialog dialog = new ShareDialog(activityContext);
            dialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    callback.onSuccess();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException e) {
                    callback.onError(new ErrorModel(SHARE_ERROR, e.getMessage(), e.getMessage()));
                }
            });
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(desc)
                    .setContentUrl(Uri.parse(url))
                    .setImageUrl(Uri.parse(imageUrl))
                    .build();
            dialog.show(activityContext, linkContent);
        }
    }

    /**
     * Share text to wall
     *
     * @param title
     * @param desc
     * @param url
     * @param imageUrl
     * @param callback set {@link OnShareListener} fbconnect.callback
     */
    public void share(String title, String desc, String url, String imageUrl, final OnShareListener callback) {
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(title)
                .setContentDescription(desc)
                .setContentUrl(Uri.parse(url))
                .setImageUrl(Uri.parse(imageUrl))
                .build();
        if (isLogEnabled) {
            log("share", "has permission = " + hasPublishPermission());
        }
        ShareApi.share(linkContent, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                if (result.getPostId() != null) {
                    String id = result.getPostId();
                    callback.onSuccess();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                callback.onError(new ErrorModel(SHARE_ERROR, e.getMessage(), e.getMessage()));
            }
        });
    }

    /**
     * Share image to wall
     *
     * @param activityContext set {@linkplain Activity} context
     * @param image           set {@link Bitmap}
     * @param callback        set {@link OnShareListener} fbconnect.callback
     */
    public void shareImage(Activity activityContext, Bitmap image, final OnShareListener callback) {
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            ShareDialog dialog = new ShareDialog(activityContext);
            dialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    callback.onSuccess();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException e) {
                    callback.onError(new ErrorModel(SHARE_ERROR, e.getMessage(), e.getMessage()));
                }
            });
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(image)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            dialog.show(activityContext, content);
        }
    }

    /**
     * onActivityResult in your Activity, pass the result to the CallbackManager:
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Check for Session and token
     *
     * @return
     */
    public boolean isSessionCheck() {
        token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            return false;
        } else if (token.isExpired()) {
            return false;
        }
        return true;
    }

    /**
     * Return the current accesstoken
     *
     * @return
     */
    public String getAccessToken() {
        return token.getToken();
    }

    /**
     * Log Enable/Disable
     *
     * @param isLogEnabled set enable/disable
     */
    public void setLogEnabled(boolean isLogEnabled) {
        this.isLogEnabled = isLogEnabled;
    }

    public boolean isLogEnabled() {
        return isLogEnabled;
    }

    private void log(String TAG, String msg) {
        if (isLogEnabled)
            Log.i(TAG, msg);
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    private ErrorModel setError(FacebookRequestError e) {
        return new ErrorModel(e.getErrorCode(), e.getErrorType(), e.getErrorMessage());
    }

    private ErrorModel setTokenExpire() {
        return new ErrorModel(TOKEN_EXPIRE, "Token Expire", "Token Expire");
    }
}
