package booknest.app.feature.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import booknest.app.R
import booknest.app.feature.login.LogInActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var registrationButton: Button
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        Log.d("RegisterActivity", "onCreate: RegisterActivity started")

        // Initializing the views
        email = findViewById(R.id.EmailBox)
        password = findViewById(R.id.editTextTextPassword)
        confirmPassword = findViewById(R.id.editTextTextPassword2)
        registrationButton = findViewById(R.id.RegisterButton)
        loginButton = findViewById(R.id.LogInButton)
        progressBar = findViewById(R.id.progressBar)

        auth = FirebaseAuth.getInstance()

        // Handle registration button click (example)
        registrationButton.setOnClickListener {
            Log.d("RegisterActivity", "Register button clicked")
            // Handle registration logic (e.g., Firebase registration)
        }

        // Handle login link click (go to login screen)
        loginButton.setOnClickListener {
            Log.d("RegisterActivity", "Login link clicked")
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            Log.d("RegisterActivity", "Navigating to LogInActivity")
            finish() // Optional, if you want to close the registration screen
        }
    }
}