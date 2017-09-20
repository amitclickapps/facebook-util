package net.android.jn.facebook;

import net.android.jn.model.ErrorModel;
import net.android.jn.model.FriendModel;

import java.util.List;

/**
 * Created by clickapps on 16/6/15.
 */
public interface OnFriendListener extends OnErrorListener {

    public void onFriendList(List<FriendModel> list);

    @Override
    void onError(ErrorModel e);

}
