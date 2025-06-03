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
import com.example.chatapplication.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Registration : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtSurname: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText

    private lateinit var btnSignUp:Button

    private lateinit var mDbRef: DatabaseReference

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpLoginText()

        edtName=findViewById(R.id.edtTextName)
        edtSurname=findViewById(R.id.edtTextSurname)
        edtEmail=findViewById(R.id.edtTextEmail)
        edtPassword=findViewById(R.id.edtTextPassword)

        btnSignUp=findViewById(R.id.btnSignUp)

        mAuth= FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener()
        {
            val name = edtName.text.toString()
            val surname = edtSurname.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            signup(name,surname,email,password)
        }


    }

    private fun setUpLoginText() {
        val textView = findViewById<TextView>(R.id.btn_login_section)
        val fullText = getString(R.string.registrationPage_loginButton)
        val clickablePart = getString(R.string.registrationPage_loginButton_clickable)

        val start = fullText.indexOf(clickablePart)
        val end = start + clickablePart.length

        if (start != -1) {
            val spannable = SpannableString(fullText)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(widget.context,Login::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.color = Color.BLACK
                }
            }

            spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            textView.text = spannable
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.highlightColor = Color.TRANSPARENT
        } else {
            Log.e("LoginText", "clickablePart non trovato in fullText")
            textView.text = fullText
        }
    }

    private fun signup(name: String,surname:String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    addUserToDatabase(name,surname,email,mAuth.currentUser?.uid!!)

                    Toast.makeText(
                        baseContext,
                        "Registration successful. Now log in",
                        Toast.LENGTH_SHORT,
                    ).show()

                    val intent = Intent(this,Login::class.java)
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

    private fun addUserToDatabase(name: String,surname:String, email: String, uid: String) {
        mDbRef= FirebaseDatabase.getInstance("https://doggo-6c19f-default-rtdb.europe-west1.firebasedatabase.app").getReference()
        mDbRef.child("user").child(uid).setValue(User(name,surname,email,uid))
    }

}

/*
package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edt_name: EditText
    private lateinit var edt_email: EditText
    private lateinit var edt_password: EditText
    private lateinit var btn_login: Button
    private lateinit var btn_signUp: Button
    private lateinit var mDbRef: DatabaseReference

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        edt_name=findViewById(R.id.edt_name)
        edt_email=findViewById(R.id.edt_email)
        edt_password=findViewById(R.id.edt_password)
        btn_login=findViewById(R.id.btnLogin)
        btn_signUp=findViewById(R.id.btnSignUp)

        mAuth= FirebaseAuth.getInstance()

        btn_login.setOnClickListener()
        {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }

        btn_signUp.setOnClickListener()
        {
            val name = edt_name.text.toString()
            val email = edt_email.text.toString()
            val password = edt_password.text.toString()

            signup(name,email,password)
        }
    }

    private fun signup(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!)

                    val intent = Intent(this,MainActivity::class.java)
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

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDbRef=FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name,email,uid))
    }
}
 */