package mat.client.login.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface SessionManagementServiceAsync.
 */
@RemoteServiceRelativePath("sessionService")
public interface SessionManagementServiceAsync {

    /**
     * Gets info about the current user.
     *
     * @param callback the callback
     * @return the current user role
     */
    void getCurrentUser(AsyncCallback<CurrentUserInfo> callback);

    /**
     * Get current release version.
     *
     * @param callback the callback
     */
    void getCurrentReleaseVersion(AsyncCallback<String> callback);
}
