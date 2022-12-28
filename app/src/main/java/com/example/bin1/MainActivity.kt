package com.example.bin1

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bin1.databinding.ActivityMainBinding
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var bindingClass: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)


        bindingClass.apply {

            insert.onRightDrawableClicked {
                it.text.clear()
                schemeText.text = ""
                brandText.text = ""
                lengthText.text = ""
                luhnText.text = ""
                typeText.text = ""
                prepaidText.text = ""
                countryText.text = ""
                latitudeText.text = ""
                longitudeText.text = ""
                bankName.text = ""
                bankCity.text = ""
                bankPhone.text = ""
                bankUrl.text = ""

            }
            insert.setOnKeyListener { _, keyCode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    fun isEmpty(): Boolean {
                        if (insert.text.isNullOrEmpty()) insert.error =
                            getString(R.string.empty_field)
                        return insert.text.isNullOrEmpty()
                    }

                    fun isDigit(): Boolean {
                        if (insert.text.isDigitsOnly()) return true
                        else {
                            insert.error = getString(R.string.not_number)
                        }
                        return false
                    }
                    if (!isEmpty() && isDigit()) {
                        val edText = insert.text.toString()
                        getResult(edText.toInt())
                        // if (edText.length%4 == 0){ insert.append(" ") }
                    }

                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }


    private fun getResult(binNumber: Int) {
        val url = "https://lookup.binlist.net/$binNumber"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET,
            url,
            { response ->
                parseData(response)

            }, {
                  if (it.message == null ) Toast.makeText(this,  R.string.error_404, Toast.LENGTH_LONG).show()
            })
        queue.add(stringRequest)
    }

    @SuppressLint("SetTextI18n")
    private fun parseData(result: String?) {
        val mainObject = result?.let { JSONObject(it) }
        val item = Data(
            mainObject?.optJSONObject("number")?.optInt("length"),
            mainObject?.optJSONObject("number")?.optBoolean("luhn"),
            mainObject?.optString("scheme"),
            mainObject?.optString("type"),
            mainObject?.optString("brand"),
            mainObject?.optBoolean("prepaid"),
            mainObject?.optJSONObject("country")?.optString("name"),
            mainObject?.optJSONObject("country")?.optInt("latitude"),
            mainObject?.optJSONObject("country")?.optInt("longitude"),
            mainObject?.optJSONObject("bank")?.optString("name"),
            mainObject?.optJSONObject("bank")?.optString("url"),
            mainObject?.optJSONObject("bank")?.optString("phone"),
            mainObject?.optJSONObject("bank")?.optString("city")
        )
        bindingClass.apply {

            val no_color = "<font color=#C3C2C2>Yes / </font> <font color=#FFFFFFFF>No</font>"
            val yes_color = "<font color=#FFFFFFFF>Yes</font> <font color=#C3C2C2> / No</font>"
            val credit_color =
                "<font color=#C3C2C2>Debit / </font> <font color=#FFFFFFFF>Credit</font>"
            val debit_color =
                "<font color=#FFFFFFFF>Debit</font> <font color=#C3C2C2> / Credit</font>"

            lengthText.text = item.number_length.toString()

            when (item.number_luhn) {
                true -> luhnText.text = check_sdk(yes_color)
                false -> luhnText.text = check_sdk(no_color)
                else -> luhnText.text = ""
            }

            brandText.text = item.brand
            if (item.scheme != null) {
                schemeText.text = item.scheme.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                    else it.toString()
                }
            } else {
                schemeText.text = ""
            }

            when (item.type) {
                "debit" -> typeText.text = check_sdk(debit_color)
                "credit" -> typeText.text = check_sdk(credit_color)
                else -> typeText.text = ""
            }

            when (item.prepaid) {
                true -> prepaidText.text = check_sdk(yes_color)
                false -> prepaidText.text = check_sdk(no_color)
                else -> prepaidText.text = ""
            }

            countryText.text = item.country_name
            latitudeText.text = item.country_latitude.toString()
            longitudeText.text = item.country_longitude.toString()

            bankName.text = item.bank_name + ","
            bankCity.text = item.bank_city
            when (bankCity.text) {
                null -> bankName.text = item.bank_name
                "" -> bankName.text = item.bank_name
            }
            bankUrl.text = item.bank_url
            bankPhone.text = item.bank_phone
        }
    }

    private fun check_sdk(color: String): CharSequence? {
        val version = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(color, Html.FROM_HTML_MODE_LEGACY)
        } else Html.fromHtml(color)
        return version
    }
}