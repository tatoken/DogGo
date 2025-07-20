

package com.example.doggo_ourapp

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Registration : AppCompatActivity() {

    private lateinit var edtName: TextInputLayout
    private lateinit var edtSurname: TextInputLayout
    private lateinit var edtBirthDate: TextInputLayout
    private lateinit var edtEmail: TextInputLayout
    private lateinit var edtPassword: TextInputLayout
    private lateinit var edtCheckPassword: TextInputLayout

    private var birthDate:LocalDate? =null

    private lateinit var btnSignUp:Button

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
        edtCheckPassword=findViewById(R.id.edtTextCheckPassword)
        edtBirthDate=findViewById(R.id.edtBirthDate)

        btnSignUp=findViewById(R.id.btnSignUp)

        val showDatePicker = {
            val today = LocalDate.now()

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    birthDate = LocalDate.of(year, month + 1, dayOfMonth)
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    edtBirthDate.editText?.setText(birthDate?.format(formatter))
                },
                today.year,
                today.monthValue - 1,
                today.dayOfMonth
            )

            datePickerDialog.show()
        }

        edtBirthDate.editText?.setOnClickListener { showDatePicker() }
        edtBirthDate.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showDatePicker()
        }


        btnSignUp.setOnClickListener()
        {

            val name = edtName.editText?.text.toString()
            val surname = edtSurname.editText?.text.toString()
            val email = edtEmail.editText?.text.toString()
            val password = edtPassword.editText?.text.toString()
            val checkPassword = edtCheckPassword.editText?.text.toString()

            if(checkRegisterFields(name,surname,birthDate,email,password,checkPassword))
            {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formattedDate = birthDate?.format(formatter)
                signup(name,surname,formattedDate!!,"",email,password)
            }

        }

    }

    private fun signup(name: String, surname: String, birthDate: String, bio: String, email: String, password: String) {

        UserFirebase.signup(name,surname,birthDate,bio,email,password) { success ->
            if(success)
            {
                Toast.makeText(
                    baseContext,
                    "Registration successful. Now log in",
                    Toast.LENGTH_SHORT,
                ).show()

                val intent = Intent(this,Login::class.java)
                startActivity(intent)
                finish()

            }
            else
            {
                Toast.makeText(
                    baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun checkRegisterFields(name: String, surname: String, birthDate: LocalDate?, email: String, password: String, checkPassword: String):Boolean {
        edtName.error = null
        edtSurname.error = null
        edtBirthDate.error = null
        edtEmail.error = null
        edtPassword.error = null
        edtCheckPassword.error = null

        var error: Boolean = true

        if (name.isBlank()) {
            edtName.error = "Name is required"
            error = false
        }

        if (surname.isBlank()) {
            edtSurname.error = "Surname is required"
            error = false
        }

        if (birthDate == null) {
            edtBirthDate.error = "Birth date is required"
            error = false
        } else {
            val today = LocalDate.now()
            val minBirthDate = today.minusYears(13)
            if (birthDate.isAfter(minBirthDate)) {
                edtBirthDate.error = "You must be at least 13 years old"
                error = false
            }
        }

        if (email.isBlank()) {
            edtEmail.error = "Email is required"
            error = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = "Invalid email address"
            error = false
        }

        val passwordPattern = Regex(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!?.*(){}\\[\\]\\-_/\\\\|<>,:;\"']).{8,}$"
        )
        if (password.isBlank()) {
            edtPassword.error = "Password is required"
            error = false
        } else if (!password.matches(passwordPattern)) {
            edtPassword.error =
                "Password must be at least 8 characters long, include uppercase, lowercase, number, and special character."
            error = false
        }
        if (checkPassword.isBlank()) {
            edtCheckPassword.error = "Check password is required"
            error = false
        } else if (password != checkPassword) {
            edtCheckPassword.error = "Passwords do not match"
            error = false
        }

        return error
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
}
