package com.example.doggo_ourapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var edt_email: EditText
    private lateinit var edt_password: EditText
    private lateinit var btn_login:Button
   // private lateinit var btn_signUp:Button

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonNavigate: Button = findViewById(R.id.btn_continue)

        buttonNavigate.setOnClickListener {
            val intent = Intent(this, MainApp::class.java)
            startActivity(intent)
            finish()
        }

        setUpRegistrationText()

        edt_email=findViewById(R.id.edtTextEmail)
        edt_password=findViewById(R.id.edtTextPassword)
        btn_login=findViewById(R.id.btn_login)

        mAuth= FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            val intent = Intent(this, MainApp::class.java)
            startActivity(intent)
            finish()
            return
        }
        btn_login.setOnClickListener()
        {
            val email = edt_email.text.toString()
            val password = edt_password.text.toString()

            login(email,password)
        }

    }

    private fun setUpRegistrationText() {
        val textView = findViewById<TextView>(R.id.btn_registration_section)
        val fullText = getString(R.string.loginPage_registrationButton)
        val clickablePart = getString(R.string.loginPage_registrationButton_clickable)

        val start = fullText.indexOf(clickablePart)
        val end = start + clickablePart.length

        if (start != -1) {
            val spannable = SpannableString(fullText)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(widget.context,Registration::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.color = Color.BLACK // puoi personalizzare il colore
                }
            }

            spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            textView.text = spannable
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.highlightColor = Color.TRANSPARENT
        } else {
            Log.e("RegistrationText", "clickablePart non trovato in fullText")
            textView.text = fullText
        }
    }


    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this,MainApp::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

}
