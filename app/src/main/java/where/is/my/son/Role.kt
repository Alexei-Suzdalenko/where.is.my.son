package where.`is`.my.son
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_role.*
import where.`is`.my.son.utils.services.App.Companion.editor
import where.`is`.my.son.utils.services.App.Companion.sharedPreferences
import where.`is`.my.son.utils.services.Common
class Role : AppCompatActivity() {
    private var role: String = ""
    private var textRole: String = ""
    private var key: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role)

        role = sharedPreferences.getString(Common.ROLE, Common.NONE).toString()
        if(role == Common.NONE){
            textRole = resources.getString(R.string.titleRole) + " " + resources.getString(R.string.indifined)
            titleRole.text = textRole
        } else {
            textRole = resources.getString(R.string.titleRole) + " " + sharedPreferences.getString(Common.ROLE, Common.NONE).toString()
            titleRole.text = textRole
        }
        key = sharedPreferences.getString(Common.KEY, Common.NONE).toString()
        if(key != Common.NONE){
            editRole.setText(key)
        }


        if(sharedPreferences.getString(Common.ROLE, Common.FATHER).toString() == Common.FATHER){
            roleImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.father))
        }
        if(sharedPreferences.getString(Common.ROLE, Common.SON).toString() == Common.SON){
            roleImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.son))
        }

        roleImageView.setOnClickListener {
            role = sharedPreferences.getString(Common.ROLE, Common.NONE).toString()
            if(role == Common.FATHER){
                roleImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.son))
                editor.putString(Common.ROLE, Common.SON); editor.apply()
                textRole = resources.getString(R.string.titleRole) + " " + Common.SON
            } else {
                roleImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.father))
                editor.putString(Common.ROLE, Common.FATHER); editor.apply()
                textRole = resources.getString(R.string.titleRole) + " " + Common.FATHER
            }
            titleRole.text = textRole
        }

        saveRole.setOnClickListener {
            val password = editRole.text.toString().trim()
            val mission = sharedPreferences.getString(Common.ROLE, Common.NONE).toString()
            if( password.isNotEmpty() && password.isNotBlank() && mission != Common.NONE && mission != "" && password != ""){
                editor.putString(Common.KEY, password);
                editor.putString(Common.ROLE, mission);
                editor.apply()
                startActivity(Intent(this, MapsActivity::class.java)); finish()
            } else {
                Toast.makeText(this, resources.getString(R.string.set_password_role), Toast.LENGTH_LONG).show()
            }
        }


    }
}