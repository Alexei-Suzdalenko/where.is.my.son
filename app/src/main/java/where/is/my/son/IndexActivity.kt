package where.`is`.my.son
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import where.`is`.my.son.utils.logins_firebase.LoginFirebase
import where.`is`.my.son.utils.services.App.Companion.sharedPreferences
import where.`is`.my.son.utils.services.Common

class IndexActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var authStateListener: FirebaseAuth.AuthStateListener

    lateinit var googleButton: SignInButton
    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var facebookButton: LoginButton
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        val isSon = sharedPreferences.getString(Common.ROLE, Common.NONE).toString()
        if (isSon == Common.SON || isSon == Common.FATHER) { startActivity(Intent(this, MapsActivity::class.java)); finish() }

        // if (BuildConfig.DEBUG) {
        //     FacebookSdk.setIsDebugEnabled(true);
        //     FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        // }

        firebaseAuth = FirebaseAuth.getInstance()
        (this as AppCompatActivity).supportActionBar?.hide()
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null && isSon != Common.SON) {
                val intent = Intent(this, Permisson::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Google
        googleButton = findViewById<SignInButton>(R.id.sign_in_button)
        googleButton.setOnClickListener{ //  Firebase.auth.signOut()
          val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestIdToken(getString(R.string.default_web_client_id))
              .requestEmail()
              .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 1)
        }

        // Facebook
        callbackManager = CallbackManager.Factory.create()
        facebookButton = findViewById(R.id.login_button1)
        facebookButton.setPermissions("email", "public_profile")
        facebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.d("tag", "facebook:onSuccess:$loginResult")
                if (loginResult != null) {}
            }
            override fun onCancel() {}
            override fun onError(exception: FacebookException) { }
        })



       LoginManager.getInstance().registerCallback(callbackManager,
           object : FacebookCallback<LoginResult?> {
               override fun onSuccess(loginResult: LoginResult?) {
                   if (loginResult != null) {
                       LoginFirebase.handleFacebookAccessToken(loginResult.accessToken, this@IndexActivity)
                   }
               }
               override fun onCancel() {}
               override fun onError(exception: FacebookException) {}
           })
    }


        //     val db = Firebase.firestore
        //     val user = hashMapOf(
        //         "first" to "Ada",
        //         "last" to "Lovelace",
        //         "born" to 1815
        //     )

// //      new document with a generated ID
        //     db.collection("users")
        //         .add(user)
        //         .addOnSuccessListener { documentReference ->
        //             Log.d("tag", "DocumentSnapshot added with ID: ${documentReference.id}")
        //         }
        //         .addOnFailureListener { e ->
        //             Log.w("tag", "Error adding document", e)
        //         }
        //


    override fun onStart() {
        super.onStart()
        firebaseAuth!!.addAuthStateListener(this.authStateListener!!)
        val currentUser = Firebase.auth.currentUser
        // val account = GoogleSignIn.getLastSignedInAccount(this) if (account != null) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Google
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            LoginFirebase.handleSignInResult(task, this)
        } else {
            // Facebook
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }


}