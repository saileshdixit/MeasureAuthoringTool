package mat.client;

import mat.client.event.BackToLoginPageEvent;
import mat.client.event.FirstLoginPageEvent;
import mat.client.event.ForgotLoginIDEmailSentEvent;
import mat.client.event.ForgotLoginIDEvent;
import mat.client.event.ForgottenPasswordEvent;
import mat.client.event.LogoffEvent;
import mat.client.event.PasswordEmailSentEvent;
import mat.client.event.ReturnToLoginEvent;
import mat.client.event.SuccessfulLoginEvent;
import mat.client.event.TemporaryPasswordLoginEvent;
import mat.client.login.FirstLoginPresenter;
import mat.client.login.FirstLoginView;
import mat.client.login.ForgottenLoginIdPresenter;
import mat.client.login.ForgottenLoginIdView;
import mat.client.login.ForgottenPasswordPresenter;
import mat.client.login.ForgottenPasswordView;
import mat.client.login.LoginPresenter;
import mat.client.login.LoginView;
import mat.client.login.SecurityBannerModal;
import mat.client.login.TempPwdLoginPresenter;
import mat.client.login.TempPwdView;
import mat.client.shared.MatContext;
import mat.client.util.ClientConstants;
import mat.shared.ConstantMessages;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;

public class Login extends MainLayout implements EntryPoint {
	
	private Panel content;
	
	private ForgottenLoginIdPresenter forgottenLoginIdNewPresenter;
	private ForgottenPasswordPresenter forgottenPwdPresenter;

	private LoginPresenter loginNewPresenter;
	
	private FirstLoginPresenter securityQuestionsPresenter;
	
	private TempPwdLoginPresenter tempPwdLogingPresenter;
	
	private void callSignOut(){
		MatContext.get().setUMLSLoggedIn(false);
		MatContext.get().getLoginService().signOut(new AsyncCallback<Void>() {
			
			@Override
			public void onFailure(final Throwable arg0) {
				redirectToLogin();
			}
			
			@Override
			public void onSuccess(final Void arg0) {
				redirectToLogin();
			}
		});
	}

	public static native void console(String message) /*-{ console.log(message); }-*/;

	private static native String getAppUserId() /*-{
		$wnd.oktaSignIn.authClient.session.get().then(function (res) {
			if (res.status === 'ACTIVE') {
				console.log('res.login, ' + res.login);
				$wnd.oktaSignIn.authClient.tokenManager.get('idToken').then(function (idToken) {
					console.log('GWT token claims, ' + idToken.claims.name + ' ' + idToken.claims.email);
					console.log('MAT User: ' + idToken.claims.preferred_username);
					return idToken.claims.email;
				});
			}});
	}-*/;
	
	/* (non-Javadoc)
	 * @see mat.client.MainLayout#initEntryPoint()
	 */
	@Override
	protected void initEntryPoint() {
		MatContext.get().setCurrentModule(ConstantMessages.LOGIN_MODULE);
//		showLoadingMessage();
		content = getContentPanel();
		initPresenters();
		loginNewPresenter.go(content);
		MatContext.get().getEventBus().addHandler(PasswordEmailSentEvent.TYPE, event -> {
			content.clear();
			loginNewPresenter.go(content);
			loginNewPresenter.displayForgottenPasswordMessage();
		});

		MatContext.get().getEventBus().addHandler(ForgotLoginIDEmailSentEvent.TYPE, event -> {
			content.clear();
			 loginNewPresenter.go(content);
			 loginNewPresenter.displayForgottenLoginIDMessage();
		});
		MatContext.get().getEventBus().addHandler(ForgottenPasswordEvent.TYPE, event -> {
			content.clear();
			forgottenPwdPresenter.go(content);
		});

		MatContext.get().getEventBus().addHandler(ForgotLoginIDEvent.TYPE, event -> {
			content.clear();
			forgottenLoginIdNewPresenter.go(content);
		});

		MatContext.get().getEventBus().addHandler(SuccessfulLoginEvent.TYPE, event -> {
			SecurityBannerModal securityBanner = new SecurityBannerModal();
			securityBanner.show();
			securityBanner.getAcceptButton().addClickHandler(e -> {
				securityBanner.hide();
				MatContext.get().redirectToHtmlPage(ClientConstants.HTML_MAT);
			});
			securityBanner.getDeclineButton().addClickHandler(e -> {
				securityBanner.hide();
				callSignOut();
			});
		});

		MatContext.get().getEventBus().addHandler(ReturnToLoginEvent.TYPE, event -> {
			content.clear();
			loginNewPresenter.go(content);
		});

		MatContext.get().getEventBus().addHandler(BackToLoginPageEvent.TYPE, event ->
				MatContext.get().redirectToHtmlPage(ClientConstants.HTML_LOGIN));

		MatContext.get().getEventBus().addHandler(FirstLoginPageEvent.TYPE, event -> {
			content.clear();
			securityQuestionsPresenter.go(content);
		});

		MatContext.get().getEventBus().addHandler(TemporaryPasswordLoginEvent.TYPE, event -> {
			content.clear();
			tempPwdLogingPresenter.go(content);
		});

		MatContext.get().getEventBus().addHandler(LogoffEvent.TYPE, event -> callSignOut());
	}
	
	/**
	 * Inits the presenters.
	 */
	private void initPresenters() {
		String appUserId = getAppUserId();
		MatContext.get().setAppUserId(appUserId);
		console(appUserId);

		LoginView loginView = new LoginView();
		loginNewPresenter = new LoginPresenter(loginView);
		
		final FirstLoginView securityQuesView = new FirstLoginView();
		securityQuestionsPresenter = new FirstLoginPresenter(securityQuesView);
		
		final ForgottenPasswordView forgottenPwdView = new ForgottenPasswordView();
		forgottenPwdPresenter = new ForgottenPasswordPresenter(forgottenPwdView);
		
		ForgottenLoginIdView forgottenLoginIdNewView = new ForgottenLoginIdView();
		forgottenLoginIdNewPresenter = new ForgottenLoginIdPresenter(forgottenLoginIdNewView);
		final TempPwdView temPwdview = new TempPwdView();
		tempPwdLogingPresenter = new TempPwdLoginPresenter(temPwdview);
	}

	/**
	 * Redirects to the Login.html
	 */
	private void redirectToLogin() {
		/*
		 * Added a timer to have a delay before redirect since
		 * this was causing the firefox javascript exception.
		 */
		final Timer timer = new Timer() {
			@Override
			public void run() {
				MatContext.get().redirectToHtmlPage(ClientConstants.HTML_LOGIN);
			}
		};
		timer.schedule(1000);
	}
}
