/**
 * 
 */
package mat.client.umls.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author jnarang
 *
 */
@RemoteServiceRelativePath("vsacapi")
public interface VSACAPIService extends RemoteService {

	boolean validateVsacUser(String userName, String password);

	VsacApiResult getValueSetBasedOIDAndVersion(String OID);

	void inValidateVsacUser();

}
