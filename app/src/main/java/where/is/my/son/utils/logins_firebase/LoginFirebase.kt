package where.`is`.my.son.utils.logins_firebase
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import where.`is`.my.son.utils.models.UserApp

object LoginFirebase {
    // Google
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, context: Activity) {
        try {
            val account : GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

         //   Log.d("tag", "account " + account.email.toString())
         //   Log.d("tag", "account " + account.photoUrl.toString())
         //   Log.d("tag", "account " + account.id.toString())
         //   Log.d("tag", "account " + account.idToken.toString())
         //   Log.d("tag", "account " + account.displayName.toString())
         //   Log.d("tag", "account " + account.givenName.toString())

            Log.d("tag", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account, context)

        } catch (e: ApiException) {
            Log.d("tag", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, context: Activity) {
        val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
        Firebase.auth.signInWithCredential(credential).addOnCompleteListener(context) { task ->
            if (task.isSuccessful) {
                val uid =  Firebase.auth.currentUser!!.uid
                val database = Firebase.database.reference
                val userApp = UserApp(uid, account.displayName!!, account.photoUrl!!.toString(), account.email!! )
                database.child("users").child(uid).setValue(userApp)
            }
        }
    }


    // Facebook
    fun handleFacebookAccessToken(token: AccessToken, activity: Activity) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        Firebase.auth.signInWithCredential(credential).addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = Firebase.auth.currentUser
                    val database = Firebase.database.reference
                    val userApp = UserApp(user!!.uid, user.displayName!!, user.photoUrl!!.toString(), user.email!! )
                    database.child("users").child(user.uid).setValue(userApp)
                } else {
                    Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }






}