package com.example.trackersinall.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import com.example.trackersinall.R
import com.example.trackersinall.interfaces.IFinalizarRetorno
import com.example.trackersinall.interfaces.IObterPecas
import com.example.trackersinall.model.PecasModelModel
import com.example.trackersinall.util.SharedPreference
import kotlinx.android.synthetic.main.activity_pop_up_alterar_serie.popup_window_view_with_border
import kotlinx.android.synthetic.main.activity_pop_up_finalizar_chamado_retorno.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PopUpFinalizarChamadoRetornoActivity : AppCompatActivity() {
    private var sharedPreference: SharedPreference?=null
    private var darkStatusBar = false
    private var preferenceusuario : String?=null
    private var extraidchamado : String?=null
    private var extracliente : String?=null
    var list : List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_pop_up_finalizar_chamado_retorno)
        sharedPreference = SharedPreference(this)
        //Carrega apelido
        if (sharedPreference!!.getValueString("apelido")!=null) {
            preferenceusuario = sharedPreference!!.getValueString("apelido")!!
        }else{
            Toast.makeText(this@PopUpFinalizarChamadoRetornoActivity, "FALHA username não detectado", Toast.LENGTH_LONG).show()
        }


        //region Animation 1
        // Set the Status bar appearance (transparent) for different API levels
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, false)
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
            popup_window_backgroundr.setBackgroundColor(animator.animatedValue as Int)
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
            extraidchamado = extras.getString("protocolo")
            extracliente = extras.getString("cliente")
        }
        protocolor!!.setText(extraidchamado)
        clienter!!.setText(extracliente)

        getPecas()

        popup_window_buttonr.setOnClickListener {
            finalizarChamadoRetorno()
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
            popup_window_backgroundr.setBackgroundColor(
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

    fun getPecas(){

        val apiInterfaceObterPecas = IObterPecas.create().getSeparados()

        apiInterfaceObterPecas.enqueue( object : Callback<List<PecasModelModel>> {
            override fun onResponse(call: Call<List<PecasModelModel>>?, response: Response<List<PecasModelModel>>?) {
                if(response != null){
                    Log.d("pecasResponse", response.body()!![0].descricao)
                    list = response.body()!!.map { it.descricao } //map é uma função do método List, que permite itinerar uma variavel de um objeto
                    spinner.setItems(list)
                }
            }

            override fun onFailure(call: Call<List<PecasModelModel>>?, t: Throwable?) {
                Log.d("pecasResponseError", t.toString()) //apresenta resultado no logcat se falhar
            }
        })
    }

    fun finalizarChamadoRetorno(){
        progressBarr.visibility = View.VISIBLE
        val nomeusu = RequestBody.create(MediaType.parse("text/plain"), preferenceusuario)
        val idchamado = RequestBody.create(MediaType.parse("text/plain"), protocolor!!.text.toString())
        val contadorpb = RequestBody.create(MediaType.parse("text/plain"), contadorPBr!!.text.toString())
        val contadorcl = RequestBody.create(MediaType.parse("text/plain"), contadorCLr!!.text.toString())
        val motivo = RequestBody.create(MediaType.parse("text/plain"), motivor!!.text.toString())
        val obsproxchamado = RequestBody.create(MediaType.parse("text/plain"), obsProxChamador!!.text.toString())
        val pecas = RequestBody.create(MediaType.parse("text/plain"), spinner.selectedItemsAsString)

        val apiInterface = IFinalizarRetorno.create().getSeparados(nomeusu, idchamado, contadorpb, contadorcl, motivo, obsproxchamado, pecas)

        apiInterface.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("finalizarRetorno", response.body().toString())
                if(response != null){
                    val jsonObject = JSONObject(response.body().toString())
                    jsonObject.toString().replace("\\\\", "")

                    if (jsonObject.getString("status") == "true"){
                        progressBarr.visibility = View.GONE
                        Toast.makeText(this@PopUpFinalizarChamadoRetornoActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                        onBackPressed()
                    }else{
                        Toast.makeText(this@PopUpFinalizarChamadoRetornoActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                        Log.d("finalizarRetorno", response.body().toString())
                        progressBarr.visibility = View.GONE
                        onBackPressed()
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("finalizarRetornoError", t.toString())
                Toast.makeText(this@PopUpFinalizarChamadoRetornoActivity, "FALHA ao Alterar", Toast.LENGTH_LONG).show()
                progressBarr.visibility = View.GONE
                onBackPressed()
            }
        })
    }
}