package net.android.jn.facebook;

import net.android.jn.model.ErrorModel;
import net.android.jn.model.FriendModel;
import net.android.jn.model.ProfileModel;

import java.util.List;

/**
 * Created by clickapps on 29/6/15.
 */
public abstract class Callback implements OnErrorListener, OnFriendListener, OnProfileListner, OnShareListener {

    @Override
    public void onFriendList(List<FriendModel> list) {

    }

    @Override
    public void onUserProfile(ProfileModel profile) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(ErrorModel e) {

    }
}
