package com.example.trackersinall.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackersinall.util.CooldownClick
import com.example.trackersinall.util.SharedPreference
import android.net.Uri
import com.example.trackersinall.R
import com.example.trackersinall.interfaces.ILogin
import org.json.JSONObject
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    var username: EditText? = null
    var passwordUser: EditText? = null
    var pbbar: ProgressBar? = null
    var cnpj: EditText? = null
    var button: Button? = null
    var cadastrarButton: Button? = null
    var senhaButton: Button?= null
    var sharedPreference: SharedPreference?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cnpj = findViewById(R.id.cnpjEditText)
        passwordUser = findViewById(R.id.senhaEditText)
        username = findViewById(R.id.cpfEditText)
        pbbar = findViewById(R.id.progressBar)
        pbbar!!.visibility = View.GONE
        button  = findViewById(R.id.loginButton)
        cadastrarButton = findViewById(R.id.buttonCadastrar)
        senhaButton = findViewById(R.id.buttonSenha)
        sharedPreference = SharedPreference(this)

        //Pegando SharedePreferences
        if (sharedPreference!!.getValueString("numero")!=null) {
            val savedString = sharedPreference!!.getValueString("numero")!!
            username!!.setText(savedString)
        }else{
            username!!.hint="12234456678"
        }
        if (sharedPreference!!.getValueString("password")!=null) {
            val savedString = sharedPreference!!.getValueString("password")!!
            cnpj!!.setText(savedString)
        }else{
            cnpj!!.hint="05555555000555"
        }

        //region Animação
        var rellay1 = RelativeLayout(this)
        var rellay2 = RelativeLayout(this)
        var handler = Handler()
        var runnable = Runnable {
            rellay1.visibility = View.VISIBLE
            rellay2.visibility = View.VISIBLE
        }
        rellay1 = findViewById(R.id.rellay1)
        rellay2 = findViewById(R.id.rellay2)
        handler.postDelayed(runnable, 2000) //2000 is the timeout for the splash
        // endregion

        checkPermissions()

        val buttonClickListener = CooldownClick { //adiciona cooldown no botão
            pbbar!!.visibility = View.VISIBLE
            checkLogin()
        }

        val buttonCadastrarClickListener = CooldownClick { //adiciona cooldown no botão
            val recipient = "maiksonstrife@gmail.com"
            val subject = "Cadastro TrackerSinall"
            val message = "Olá, sou o funcionario  ___________ e gostaria de me cadastrar"
            sendEmail(recipient, subject, message)
        }

        val buttonSenhaClickListener = CooldownClick { //adiciona cooldown no botão
            val recipient = "maiksonstrife@gmail.com"
            val subject = "Esqueci a Senha TackerSinall "
            val message = "Por favor enviar a senha"
            sendEmail(recipient, subject, message)
        }

        cadastrarButton!!.setOnClickListener(buttonCadastrarClickListener)
        senhaButton!!.setOnClickListener(buttonSenhaClickListener)
        button!!.setOnClickListener(buttonClickListener) //login
    }

    fun checkLogin () {
        //RequestBody necessário pra envios @POST
        val userid = RequestBody.create(MediaType.parse("text/plain"), username!!.text.toString())
        val password = RequestBody.create(MediaType.parse("text/plain"), cnpj!!.text.toString())
        val apiInterface = ILogin.create().getSeparados(userid, password)

            apiInterface.enqueue(object : Callback<String>{

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("loginResposta", response.body().toString())

                    val jsonObject = JSONObject(response.body().toString())
                    jsonObject.toString().replace("\\\\", "")

                    if (jsonObject.getString("status") == "true"){
                        //Salvar dados recebidos
                        sharedPreference!!.save("numero",username!!.text.toString())
                        sharedPreference!!.save("password",cnpj!!.text.toString())
                        sharedPreference!!.save("idfuncionario",jsonObject.getString("idfuncionario"))
                        sharedPreference!!.save("nomefuncionario",jsonObject.getString("nomefuncionario"))
                        sharedPreference!!.save("apelido",jsonObject.getString("apelido"))
                        sharedPreference!!.save("idsuperior",jsonObject.getString("idsuperior"))
                        pbbar!!.visibility = View.GONE

                        //preparo a intent pra chamar outra activity
                        val intent = Intent(this@MainActivity, MenuSelectionActivity::class.java)
                        //Essa é uma chamada caso o android seja uma versão antiga demais
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ContextCompat.startActivity(this@MainActivity, intent, null)
                            finish()
                        } else { //chamada recente
                            startActivity(intent)
                            finish()
                        }
                    }else{
                        Toast.makeText(this@MainActivity,"Login Invalido",Toast.LENGTH_SHORT).show()
                        Log.d("loginResposta", response.body().toString())
                        pbbar!!.visibility = View.GONE
                    }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@MainActivity,"Login Invalido",Toast.LENGTH_SHORT).show()
                Log.d("loginRespostaError", t.toString())
                pbbar!!.visibility = View.GONE
            }
        })
    }

    fun checkPermissions(){

        var permissions1: Array<String> = emptyArray()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autorize o aplicativo para acesso ao GPS", Toast.LENGTH_SHORT).show()
            permissions1 += (Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autorize o aplicativo para acesso ao GPS", Toast.LENGTH_SHORT).show()
            permissions1 += (Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autorize o aplicativo para acesso ao GPS", Toast.LENGTH_SHORT).show()
            permissions1 += (Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autorize o aplicativo para acesso ao GPS", Toast.LENGTH_SHORT).show()
            permissions1 += (Manifest.permission.CAMERA)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autorize o aplicativo para acesso a INTERNET", Toast.LENGTH_SHORT).show()
            permissions1 += (Manifest.permission.INTERNET)
        }

        try {
            ActivityCompat.requestPermissions(this, permissions1, 0)
        }catch(e : java.lang.Exception){
            Toast.makeText(this, "Permissões Aceitas", Toast.LENGTH_SHORT).show()
        }

    }

    private fun sendEmail(recipient: String, subject: String, message: String) {
        /*ACTION_SEND chama Cliente de email instalado no android*/
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        /* recipient é um array pois pode haver necessidade de enviar multiplos emails
           insira virgulça(,) para separar emails */
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        //Assunto
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //Mensagem
        mIntent.putExtra(Intent.EXTRA_TEXT, message)


        try {
            startActivity(Intent.createChooser(mIntent, "Selecione cliente de E-mail..."))
        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
