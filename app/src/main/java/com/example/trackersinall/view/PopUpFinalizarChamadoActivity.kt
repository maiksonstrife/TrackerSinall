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
import com.example.trackersinall.interfaces.IFinalizarChamado
import kotlinx.android.synthetic.main.activity_pop_up_alterar_serie.popup_window_view_with_border
import kotlinx.android.synthetic.main.activity_pop_up_finalizar_chamado.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopUpFinalizarChamadoActivity : AppCompatActivity() {
    private var darkStatusBar = false
    private var extraprotocolo : String?=null
    private var extracliente : String?=null
    private var extrastatuschamado : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_pop_up_finalizar_chamado)
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
            popup_window_backgroundf.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()


        // Fade animation for the Popup Window
        popup_window_view_with_border.alpha = 0f
        popup_window_view_with_border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()
        //endregion

        val extras = intent.extras
        if (extras != null) {
            extraprotocolo = extras.getString("protocolo")
            extracliente = extras.getString("cliente")
            extrastatuschamado = extras.getString("idstatuschamado")
        }
        protocolof!!.setText(extraprotocolo)
        cliente!!.setText(extracliente)

        popup_window_buttonf.setOnClickListener {
            finalizarChamado()
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
            popup_window_backgroundf.setBackgroundColor(
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

    fun finalizarChamado(){
        progressBarf.visibility = View.VISIBLE
        val idchamado = RequestBody.create(MediaType.parse("text/plain"), protocolof!!.text.toString())
        val idstatuschamado = RequestBody.create(MediaType.parse("text/plain"), extrastatuschamado)
        val contadorpb = RequestBody.create(MediaType.parse("text/plain"), contadorPB!!.text.toString())
        val contadorcl = RequestBody.create(MediaType.parse("text/plain"), contadorCL!!.text.toString())
        val obsproxchamado = RequestBody.create(MediaType.parse("text/plain"), obsProxChamado!!.text.toString())

        val apiInterface = IFinalizarChamado.create().getSeparados(idchamado, idstatuschamado, contadorpb, contadorcl, obsproxchamado)

        apiInterface.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("finalizarResposta", response.body().toString())
                if(response != null){
                    val jsonObject = JSONObject(response.body().toString())
                    jsonObject.toString().replace("\\\\", "")

                    if (jsonObject.getString("status") == "true"){
                        progressBarf.visibility = View.GONE
                        Toast.makeText(this@PopUpFinalizarChamadoActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                        onBackPressed()
                    }else{
                        Toast.makeText(this@PopUpFinalizarChamadoActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                        Log.d("alterarResposta", response.body().toString())
                        progressBarf.visibility = View.GONE
                        onBackPressed()
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("finalizarRespostaError", t.toString())
                Toast.makeText(this@PopUpFinalizarChamadoActivity, "FALHA ao Alterar", Toast.LENGTH_LONG).show()
                progressBarf.visibility = View.GONE
                onBackPressed()
            }
        })
    }
}

