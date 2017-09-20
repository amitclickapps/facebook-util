package net.android.jn.facebook;

import net.android.jn.model.ErrorModel;

/**
 * Created by clickapps on 18/6/15.
 */
public interface OnShareListener extends OnErrorListener {
    public void onSuccess();

    @Override
    void onError(ErrorModel e);

}
