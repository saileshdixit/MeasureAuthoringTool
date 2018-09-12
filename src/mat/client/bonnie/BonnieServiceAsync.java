package mat.client.bonnie;

import com.google.gwt.user.client.rpc.AsyncCallback;

import mat.client.umls.service.VsacTicketInformation;
import mat.shared.BonnieOAuthResult;
import mat.shared.bonnie.result.BonnieUserInformationResult;

public interface BonnieServiceAsync {
	
	public void getBonnieAccessLink(AsyncCallback<String> asyncCallback);

	public void exchangeCodeForTokens(String code, AsyncCallback<BonnieOAuthResult> asyncCallback);
	
	public void getBonnieUserInformationForUser(String userId, AsyncCallback<BonnieUserInformationResult> asyncCallback);
	
	public void getUpdateOrUploadMeasureToBonnie(String measureId, String userId, VsacTicketInformation vsacTicket, AsyncCallback<String> asyncCallback);

	public void revokeBonnieAccessTokenForUser(String matUserId, AsyncCallback<Void> asyncCallback);

	public void revokeAllBonnieAccessTokens(AsyncCallback<Void> asyncCallback);
}
