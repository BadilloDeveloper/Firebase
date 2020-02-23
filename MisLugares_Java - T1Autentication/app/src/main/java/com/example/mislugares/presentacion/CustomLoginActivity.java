package com.example.mislugares.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.mislugares.R;
import com.example.mislugares.modelo.Usuario;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class CustomLoginActivity extends FragmentActivity implements  GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String correo = "";
    private String contraseña = "";
    private ViewGroup contenedor;
    private EditText etCorreo, etContraseña;
    private TextInputLayout tilCorreo, tilContraseña;
    private ProgressDialog dialogo;
    //sdk google
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private GoogleApiClient googleApiClient;
    //sdk facebook
    private CallbackManager callbackManager;
    private LoginButton btnFacebook;
    //twitter
    private TwitterLoginButton btnTwitter;
    //unificar
    private boolean unificar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_login);
        etCorreo = (EditText) findViewById(R.id.correo);
        etContraseña = (EditText) findViewById(R.id.contraseña);
        tilCorreo = (TextInputLayout) findViewById(R.id.til_correo);
        tilContraseña = (TextInputLayout) findViewById(R.id.til_contraseña);
        contenedor = (ViewGroup) findViewById(R.id.contenedor);
        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Verificando usuario");
        dialogo.setMessage("Por favor espere...");



        //google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN) .requestIdToken(getString(R.string.default_web_client_id)) .requestEmail() .build();
        googleApiClient = new GoogleApiClient.Builder(this) .enableAutoManage(this , (GoogleApiClient.OnConnectionFailedListener) this) .addApi(Auth.GOOGLE_SIGN_IN_API, gso) .build();
        //facebook
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_custom_login);
        callbackManager = CallbackManager.Factory.create();
        btnFacebook = (LoginButton) findViewById(R.id.facebook);
        btnFacebook.setReadPermissions("email", "public_profile");
        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override public void onSuccess(LoginResult loginResult) {
                facebookAuth(loginResult.getAccessToken()); }
                @Override public void onCancel() {
                mensaje("Cancelada autentificación con facebook");
                }
                @Override public void onError(FacebookException error)
                {
                    mensaje(error.getLocalizedMessage());
                } });

        //twitterr
        Twitter.initialize(new TwitterConfig.Builder(this) .logger(new DefaultLogger(Log.DEBUG)) .twitterAuthConfig(new TwitterAuthConfig(getString(R.string. twitter_consumer_key),
                getString(R.string.twitter_consumer_secret))) .debug(true) .build());



        btnTwitter = (TwitterLoginButton) findViewById(R.id.twitter); btnTwitter.setCallback(new Callback<TwitterSession>() {
            @Override public void success(Result<TwitterSession> result) {
                twitterAuth(result.data);
            }
            @Override public void failure(TwitterException exception) {
                    mensaje(exception.getLocalizedMessage());
            }
        });

        unificar = getIntent().getBooleanExtra("unificar", false);


        verificaSiUsuarioValidado();


    }


//anonima
    public void autentificaciónAnonima(View v) {
        dialogo.show(); auth.signInAnonymously() .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override public void onComplete(@NonNull Task<AuthResult> task){
                if (task.isSuccessful()) {
                    verificaSiUsuarioValidado();
                } else {
                    dialogo.dismiss(); Log.w("MisLugares", "Error en signInAnonymously", task.getException()); mensaje( "ERROR al intentarentrar de forma anónima");
                }
            }
        });
    }

//tw
    private void twitterAuth(TwitterSession session) {
        AuthCredential credential = TwitterAuthProvider.getCredential( session.getAuthToken().token, session.getAuthToken().secret);
        auth.signInWithCredential(credential) .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    mensaje(task.getException().getLocalizedMessage());
                }
                else {
                    verificaSiUsuarioValidado();
                }
            }
        });
    }


//fb
    private void facebookAuth(AccessToken accessToken) {
        final AuthCredential credential = FacebookAuthProvider.getCredential( accessToken.getToken());
        auth.signInWithCredential(credential) .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        LoginManager.getInstance().logOut();
                    }
                    mensaje(task.getException().getLocalizedMessage());
                }
                else {
                    verificaSiUsuarioValidado();
                }
            }
        });
    }


    //autentificar google
    public void autentificarGoogle(View v) {
        Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(i, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK ) {
                GoogleSignInResult result = Auth.GoogleSignInApi .getSignInResultFromIntent(data); if (result.isSuccess())
                {
                    googleAuth(result.getSignInAccount());
                }

            else {
                mensaje("Error de autentificación con Google");
            }
            }
        }
        //facebook
        else if (requestCode == btnFacebook.getRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        //twitter
        else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) { btnTwitter.onActivityResult(requestCode, resultCode, data); }

    }

    private void googleAuth(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential( acct.getIdToken(), null);
        if (unificar) { unificarCon(credential); } else { auth.signInWithCredential(credential);}


        auth.signInWithCredential(credential) .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                mensaje(task.getException().getLocalizedMessage());
            }   else
                {
                    verificaSiUsuarioValidado();
                }
            }

        });
    }

    private void unificarCon(AuthCredential credential) {
        auth.getCurrentUser().linkWithCredential(credential) .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    unificar = false; verificaSiUsuarioValidado();
                }
                else {
                    Log.w("MisLugares", "Error en linkWithCredential", task.getException()); mensaje("Error al unificar cuentas.");
                }
            }
        });
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result)
    {
        mensaje("Error de autentificación con Google");
    }


    private void verificaSiUsuarioValidado() {
        if (!unificar && auth.getCurrentUser() != null) {
            Usuario.guardarUsuario(auth.getCurrentUser());
            if (auth.getCurrentUser() != null) {
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i); finish();
            }
        }
    }




    public void inicioSesiónCorreo(View v) {
        if (verificaCampos()) {
            dialogo.show();
            auth.signInWithEmailAndPassword(correo, contraseña) .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override public void onComplete(@NonNull Task<AuthResult> task) { if (task.isSuccessful()) { verificaSiUsuarioValidado(); } else {
                    dialogo.dismiss();
                    mensaje(task.getException().getLocalizedMessage()); } } });
        }
    }




    public void registroCorreo(View v) {
        if (verificaCampos()) {
            dialogo.show();
            auth.createUserWithEmailAndPassword(correo, contraseña) .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) { verificaSiUsuarioValidado(); }
                    else { dialogo.dismiss(); mensaje(task.getException().getLocalizedMessage());
                    } }
            });
        }
    }


    private void mensaje(String mensaje) {
        Snackbar.make(contenedor, mensaje, Snackbar.LENGTH_LONG).show();
    }

    private boolean verificaCampos() {
        correo = etCorreo.getText().toString();
        contraseña = etContraseña.getText().toString();
        tilCorreo.setError(""); tilContraseña.setError("");
        if (correo.isEmpty()) { tilCorreo.setError("Introduce un correo"); }
        else if (!correo.matches(".+@.+[.].+")) { tilCorreo.setError("Correo no válido"); }
        else if (contraseña.isEmpty()) { tilContraseña.setError("Introduce una contraseña"); }
        else if (contraseña.length()<6) { tilContraseña.setError("Ha de contener al menos 6 caracteres"); }
        else if (!contraseña.matches(".*[0-9].*")) { tilContraseña.setError("Ha de contener un número"); }
        else if (!contraseña.matches(".*[A-Z].*")) { tilContraseña.setError("Ha de contener una letra mayúscula");
    } else {
            return true;
        }
        return false;
    }


    public void firebaseUI(View v) {
        startActivity(new Intent(this, LoginActivity.class));
    }



    public void reestablecerContraseña(View v) {
        correo = etCorreo.getText().toString();
        tilCorreo.setError(""); if (correo.isEmpty()) {
            tilCorreo.setError("Introduce un correo");
        }
        else if (!correo.matches(".+@.+[.].+")) {
            tilCorreo.setError("Correo no válido"); }
        else {

        dialogo.show(); auth.sendPasswordResetEmail(correo) .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override public void onComplete(@NonNull Task<Void> task) {
        dialogo.dismiss(); if (task.isSuccessful()) {
            mensaje("Verifica tu correo para cambiar contraseña.");
        }
        else {
            mensaje("ERROR al mandar correo para cambiar contraseña");
        }
            }
        });
        }
    }

}

