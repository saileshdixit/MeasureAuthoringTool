package mat.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
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

public class Login extends MainLayout implements EntryPoint {

    private final Logger logger = Logger.getLogger("MAT");

    private Panel content;

    private ForgottenLoginIdPresenter forgottenLoginIdNewPresenter;
    private ForgottenPasswordPresenter forgottenPwdPresenter;

    private LoginPresenter loginNewPresenter;

    private FirstLoginPresenter securityQuestionsPresenter;

    private TempPwdLoginPresenter tempPwdLogingPresenter;

    private void callSignOut() {
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

    @Override
    protected void initEntryPoint() {
        MatContext.get().setCurrentModule(ConstantMessages.LOGIN_MODULE);
        showLoadingMessage();
        content = getContentPanel();
        initPresenters();
        loginNewPresenter.go(content);
        MatContext.get().getEventBus().addHandler(PasswordEmailSentEvent.TYPE, new PasswordEmailSentEvent.Handler() {

            @Override
            public void onPasswordEmailSent(final PasswordEmailSentEvent event) {
                content.clear();
                loginNewPresenter.go(content);
                loginNewPresenter.displayForgottenPasswordMessage();
            }
        });

        MatContext.get().getEventBus().addHandler(ForgotLoginIDEmailSentEvent.TYPE, new ForgotLoginIDEmailSentEvent.Handler() {

            @Override
            public void onForgotLoginIdEmailSent(final ForgotLoginIDEmailSentEvent event) {
                content.clear();
                loginNewPresenter.go(content);
                loginNewPresenter.displayForgottenLoginIDMessage();
            }
        });
        MatContext.get().getEventBus().addHandler(ForgottenPasswordEvent.TYPE, new ForgottenPasswordEvent.Handler() {

            @Override
            public void onForgottenPassword(final ForgottenPasswordEvent event) {
                content.clear();
                forgottenPwdPresenter.go(content);
            }
        });

        MatContext.get().getEventBus().addHandler(ForgotLoginIDEvent.TYPE, new ForgotLoginIDEvent.Handler() {

            @Override
            public void onForgottenLoginID(final ForgotLoginIDEvent event) {
                content.clear();
                forgottenLoginIdNewPresenter.go(content);
            }
        });

        MatContext.get().getEventBus().addHandler(SuccessfulLoginEvent.TYPE, new SuccessfulLoginEvent.Handler() {

            @Override
            public void onSuccessfulLogin(final SuccessfulLoginEvent event) {
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
            }
        });

        MatContext.get().getEventBus().addHandler(ReturnToLoginEvent.TYPE, new ReturnToLoginEvent.Handler() {

            @Override
            public void onReturnToLogin(final ReturnToLoginEvent event) {
                content.clear();
                loginNewPresenter.go(content);
            }
        });

        MatContext.get().getEventBus().addHandler(BackToLoginPageEvent.TYPE, new BackToLoginPageEvent.Handler() {

            @Override
            public void onLoginFailure(final BackToLoginPageEvent event) {
                MatContext.get().redirectToHtmlPage(ClientConstants.HTML_LOGIN);
            }
        });

        MatContext.get().getEventBus().addHandler(FirstLoginPageEvent.TYPE, new FirstLoginPageEvent.Handler() {

            @Override
            public void onFirstLogin(final FirstLoginPageEvent event) {
                content.clear();
                securityQuestionsPresenter.go(content);
            }
        });

        MatContext.get().getEventBus().addHandler(TemporaryPasswordLoginEvent.TYPE, new TemporaryPasswordLoginEvent.Handler() {

            @Override
            public void onTempPasswordLogin(final TemporaryPasswordLoginEvent event) {
                content.clear();
                tempPwdLogingPresenter.go(content);
            }


        });

        MatContext.get().getEventBus().addHandler(LogoffEvent.TYPE, new LogoffEvent.Handler() {

            @Override
            public void onLogoff(final LogoffEvent event) {
                callSignOut();
            }
        });

        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable caught) {
                logger.log(Level.SEVERE, "UncaughtException: " + caught.getMessage(), caught);
                hideLoadingMessage();
                Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
                MatContext.get().recordTransactionEvent(null, null, null, "Unhandled Exception: " + caught.getLocalizedMessage(), 0);
            }
        });
    }

    /**
     * Inits the presenters.
     */
    private void initPresenters() {
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
