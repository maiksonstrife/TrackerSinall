package com.example.trackersinall.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import com.example.trackersinall.R
import com.example.trackersinall.interfaces.IAlterarNumeroSerie
import com.example.trackersinall.util.SharedPreference
import kotlinx.android.synthetic.main.activity_pop_up_alterar_serie.*
import kotlinx.android.synthetic.main.activity_pop_up_alterar_serie.progressBar
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopUpAlterarSerieActivity : AppCompatActivity() {
    private var darkStatusBar = false
    var sharedPreference: SharedPreference?=null
    private var extraidcontrato : String?=null
    private var extraprotocolo : String?=null
    private var extranserie : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_pop_up_alterar_serie)
        sharedPreference = SharedPreference(this)

        //region Animation 1
        // Set the Status bar appearance (transparent) for different API levels
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // If you want dark status bar, set darkStatusBar to true
                if (darkStatusBar) {
                    this.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                this.window.statusBarColor = Color.TRANSPARENT
                setWindowFlag(this, false)
            }
        }

        // Fade animation for the background of Popup Window
        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()


        // Fade animation for the Popup Window
        popup_window_view_with_border.alpha = 0f
        popup_window_view_with_border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()
        //endregion

        //Pegando chave da Intent.putExtra()
        val extras = intent.extras
        if (extras != null) {
            extraidcontrato = extras.getString("idcontrato")
            extraprotocolo = extras.getString("idchamado")
            extranserie = extras.getString("serieanterior")
        }
        idcontrato!!.setText(extraidcontrato)
        protocolo!!.setText(extraprotocolo)
        serieanterior!!.setText(extranserie)
        // Código do botão
        popup_window_button.setOnClickListener {
            alterarSerie()
        }
    }

    //region Animation 1
    private fun setWindowFlag(activity: Activity, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        } else {
            winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        win.attributes = winParams
    }


    override fun onBackPressed() {
        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        // Fade animation for the Popup Window when you press the back button
        popup_window_view_with_border.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }
    //endregion

    fun alterarSerie () {
        progressBar.visibility = View.VISIBLE
        val idcontrato = RequestBody.create(MediaType.parse("text/plain"), idcontrato!!.text.toString())
        val protocolo = RequestBody.create(MediaType.parse("text/plain"), protocolo!!.text.toString())
        val serieanterior = RequestBody.create(MediaType.parse("text/plain"), serieanterior!!.text.toString())
        val serieatual = RequestBody.create(MediaType.parse("text/plain"), serieatual!!.text.toString())
        val apiInterface = IAlterarNumeroSerie.create().getSeparados(idcontrato, protocolo, serieanterior, serieatual)

        apiInterface.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("alterarResposta", response.body().toString())
                if(response != null){
                    val jsonObject = JSONObject(response.body().toString())
                    jsonObject.toString().replace("\\\\", "")

                    if (jsonObject.getString("status") == "true"){
                        sharedPreference!!.save("serieanterior", serieanterior!!.toString())
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@PopUpAlterarSerieActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                        onBackPressed()
                    }else{
                        Toast.makeText(this@PopUpAlterarSerieActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                        Log.d("alterarResposta", response.body().toString())
                        progressBar.visibility = View.GONE
                        onBackPressed()
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("alterarRespostaError", t.toString())
                Toast.makeText(this@PopUpAlterarSerieActivity, "FALHA ao Alterar", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                onBackPressed()
            }
        })
    }
}