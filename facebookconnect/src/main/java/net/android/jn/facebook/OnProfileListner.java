package net.android.jn.facebook;

import net.android.jn.model.ErrorModel;
import net.android.jn.model.ProfileModel;

/**
 * Created by clickapps on 16/6/15.
 */
public interface OnProfileListner extends OnErrorListener {

    public void onUserProfile(ProfileModel profile);

    @Override
    void onError(ErrorModel e);

}
