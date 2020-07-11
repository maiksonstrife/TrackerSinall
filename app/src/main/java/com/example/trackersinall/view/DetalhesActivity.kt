package com.example.trackersinall.view

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersinall.R
import com.example.trackersinall.adapters.DetalhesAdapter
import com.example.trackersinall.interfaces.IAceitarRecusarChamado
import com.example.trackersinall.interfaces.IBaixarReabrir
import com.example.trackersinall.interfaces.IDetalhesChamados
import com.example.trackersinall.interfaces.IIniciarChamado
import com.example.trackersinall.model.DetalhesModel
import com.example.trackersinall.util.MyLocation
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_chamados.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetalhesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    //variaveis Api e Recycleview
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: DetalhesAdapter
    lateinit var apiInterfaceDetalhesModel: Call<List<DetalhesModel>>
    lateinit var apiInterfaceString: Call<String>
    private var idchamado : String?=null
    private var chave : String?=null
    //variaveis navigation menu
    var drawerLayout: DrawerLayout? = null
    var navigationView : NavigationView?=null //variavel pra deixar item do menu invisivel
    //variaveis que controlarão a visibilidade de botões
    private var inicioatend : String?=null
    private var fimatend : String?=null
    private var classificacao : String?=null
    private var sendnserie : String?=null
    private var sendidchamado : String?=null
    private var sendidcontrato : String?=null
    private var sendcliente : String?=null
    private var sendstatuschamado : String?=null
    private var sendusuario : String?=null
    private var latitude : String?=null
    private var longitude : String?=null
    private var distanciaMinima : String?=null
    private var noPerimetro : Boolean ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes)
        //colocando items da view nas variaveis
        navigationView = findViewById(R.id.navView)
        recyclerView = findViewById(R.id.listFilesDetalhes)
        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.navView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        //colocando a toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Detalhes"
        //setando lista e adaptador
        recyclerAdapter = DetalhesAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerAdapter
        //pegando items enviados pra essa janela
        val extras = intent.extras
        if (extras != null) {
            idchamado = extras.getString("idchamado")
            chave = extras.getString("chave")
        }
        //Setando o menu lateral
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        //chamando métodos
        navMenuInvisible() //deixa todos botoes invisiveis
        apiDetalhesChamados()
    }

    //Metodos Navigation Menu
    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //inserir codigo em cada botão
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.aceitar -> {
                apiInterfaceString = IAceitarRecusarChamado.create().getSeparados(sendidchamado!!)
                apiAlterarChamados()
            }
            R.id.recusar -> {
                apiInterfaceString = IAceitarRecusarChamado.create().getSeparados(sendidchamado!!)
                apiAlterarChamados()
            }
            R.id.iniciar -> {
//              Teste -> Não checa perimetro
                apiInterfaceString = IIniciarChamado.create().getSeparados(sendidchamado!!)
                apiAlterarChamados()

//                Produção -> Checa Perimetro
//                if(noPerimetro!!){
//                    apiInterfaceString = IIniciarChamado.create().getSeparados(sendidchamado!!)
//                    apiAceitarRecusar()
//                }else{
//                    Toast.makeText(this@DetalhesActivity, "Erro 100: Informe o seu supervisor,", Toast.LENGTH_LONG).show()
//                }
            }
            R.id.finalizar -> {
//              Teste -> Não checa perimetro
                val intent = Intent(this, PopUpFinalizarChamadoActivity::class.java)
                intent.putExtra("protocolo", sendidchamado)
                intent.putExtra("cliente", sendcliente)
                intent.putExtra("idstatuschamado", sendstatuschamado)
                startActivity(intent)

//                Produção -> Checa Perimetro
//                if(noPerimetro!!){
//                    val intent = Intent(this, PopUpFinalizarChamadoActivity::class.java)
//                    intent.putExtra("protocolo", sendidchamado)
//                    intent.putExtra("cliente", sendcliente)
//                    intent.putExtra("idstatuschamado", sendstatuschamado)
//                    startActivity(intent)
//                }else{
//                    Toast.makeText(this@DetalhesActivity, "Erro 100: Informe o seu supervisor,", Toast.LENGTH_LONG).show()
//                }
            }
            R.id.finalizarRetorno -> {
//              Teste -> Não checa perimetro
                val intent = Intent(this, PopUpFinalizarChamadoRetornoActivity::class.java)
                    intent.putExtra("usuario", sendusuario)
                    intent.putExtra("cliente", sendcliente)
                    intent.putExtra("protocolo", sendidchamado)
                    startActivity(intent)

//                Produção -> Checa Perimetro
//                if(noPerimetro!!){
//                    val intent = Intent(this, PopUpFinalizarChamadoRetornoActivity::class.java)
//                    intent.putExtra("protocolo", sendidchamado)
//                    intent.putExtra("cliente", sendcliente)
//                    intent.putExtra("idstatuschamado", sendstatuschamado)
//
//                    startActivity(intent)
//                }else{
//                    Toast.makeText(this@DetalhesActivity, "Erro 100: Informe o seu supervisor,", Toast.LENGTH_LONG).show()
//                }
            }
            R.id.reabrir -> {
//              Teste -> Não checa perimetro
                apiInterfaceString = IBaixarReabrir.create().getSeparados(sendidchamado!!)
                apiAlterarChamados()

//                Produção -> Checa Perimetro
//                if(noPerimetro!!){
//                    apiInterfaceString = IBaixarReabrir.create().getSeparados(sendidchamado!!)
//                    apiAceitarRecusar()
//                }else{
//                    Toast.makeText(this@DetalhesActivity, "Erro 100: Informe o seu supervisor,", Toast.LENGTH_LONG).show()
//                }
            }
            R.id.baixar -> {
//              Teste -> Não checa perimetro
                apiInterfaceString = IBaixarReabrir.create().getSeparados(sendidchamado!!)
                apiAlterarChamados()

//                Produção -> Checa Perimetro
//                if(noPerimetro!!){
//                    apiInterfaceString = IBaixarReabrir.create().getSeparados(sendidchamado!!)
//                    apiAceitarRecusar()
//                }else{
//                    Toast.makeText(this@DetalhesActivity, "Erro 100: Informe o seu supervisor,", Toast.LENGTH_LONG).show()
//                }
            }
            R.id.alterarserie -> {
                val intent = Intent(this, PopUpAlterarSerieActivity::class.java)
                intent.putExtra("idcontrato", sendidcontrato)
                intent.putExtra("idchamado", sendidchamado)
                intent.putExtra("serieanterior", sendnserie)
                startActivity(intent)
            }
            R.id.voltarchamado -> {
                Toast.makeText(this@DetalhesActivity, "Rodar método Menu 9", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this@DetalhesActivity, "Rodar método Menu Default", Toast.LENGTH_SHORT).show()
            }
        }

        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    //quais items do menu ficam visiveis de acordo com a chave
    private fun navMenuVisible(){
        val navMenu: Menu = navigationView!!.menu
        when (classificacao) {
            "SP" -> {
                navMenu.findItem(R.id.iniciar).isVisible = true
                navMenu.findItem(R.id.reabrir).isVisible = true
            }
            "AT" -> {
                navMenu.findItem(R.id.finalizar).isVisible = true
                navMenu.findItem(R.id.finalizarRetorno).isVisible = true
                navMenu.findItem(R.id.baixar).isVisible = true
            }
            "FN" -> {
                navMenu.findItem(R.id.voltarchamado).isVisible = true
            }
            "AP" -> {
                navMenu.findItem(R.id.alterarserie).isVisible = true
            }
            "NI" -> {
                navMenu.findItem(R.id.aceitar).isVisible = true
                navMenu.findItem(R.id.recusar).isVisible = true
            }
            "PD" -> {
                navMenu.findItem(R.id.aceitar).isVisible = true
                navMenu.findItem(R.id.recusar).isVisible = true
            }
        }
    }

    private fun navMenuInvisible(){
        val navMenu: Menu = navigationView!!.menu
        navMenu.findItem(R.id.aceitar).isVisible = false
        navMenu.findItem(R.id.recusar).isVisible = false
        navMenu.findItem(R.id.iniciar).isVisible = false
        navMenu.findItem(R.id.finalizar).isVisible = false
        navMenu.findItem(R.id.finalizarRetorno).isVisible = false
        navMenu.findItem(R.id.reabrir).isVisible = false
        navMenu.findItem(R.id.baixar).isVisible = false
        navMenu.findItem(R.id.voltarchamado).isVisible = true
    }

    //chamadas api sinall
    fun apiDetalhesChamados () {
        progressBar.visibility = View.VISIBLE
        apiInterfaceDetalhesModel = IDetalhesChamados.create().getSeparados(idchamado!!)

        apiInterfaceDetalhesModel.enqueue( object : Callback<List<DetalhesModel>> {
            override fun onResponse(call: Call<List<DetalhesModel>>?, response: Response<List<DetalhesModel>>?) {
                if(response != null){
                    recyclerAdapter.setSeparadosListItems(response.body()!!, "#3F51B5")
                    //Salvando variaveis
                    inicioatend = response.body()!![0].inicioatend
                    fimatend = response.body()!![0].fimatend
                    classificacao = response.body()!![0].classificacao
                    latitude = response.body()!![0].latitude
                    longitude = response.body()!![0].longitude
                    distanciaMinima = response.body()!![0].distanciaminima
                    sendidcontrato = response.body()!![0].idcontrato
                    sendnserie =  response.body()!![0].nrserie
                    sendcliente = response.body()!![0].cliente
                    sendidchamado = response.body()!![0].idchamado
                    sendstatuschamado = response.body()!![0].IdStatusChamado
                    sendusuario = response.body()!![0].usuario
                    progressBar.visibility = View.GONE
                    Log.d("detalhesResponse", response.body().toString())
                    getMyDistance()
                    navMenuVisible()
                }
            }

            override fun onFailure(call: Call<List<DetalhesModel>>?, t: Throwable?) {
                Log.d("detalhesResponseError", t.toString()) //apresenta resultado no logcat se falhar
                progressBar.visibility = View.GONE
                Toast.makeText(this@DetalhesActivity,"Dados não encontrados", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@DetalhesActivity, MenuSelectionActivity::class.java)
                startActivity(intent)
                finish() //destroy this activity
            }
        })
    }

    fun apiAlterarChamados(){
        progressBar.visibility = View.VISIBLE

        apiInterfaceString.enqueue(object : Callback<String>{
        override fun onResponse(call: Call<String>, response: Response<String>) {
            Log.d("AceitarResposta", response.body().toString())
            val jsonObject = JSONObject(response.body().toString())
            jsonObject.toString().replace("\\\\", "")

            if (jsonObject.getString("status") == "true"){
                Toast.makeText(this@DetalhesActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                chamadosActivity() //retorna pra tela de chamados
            }else{
                Toast.makeText(this@DetalhesActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                onBackPressed()
            }
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            Log.d("AceitarRespostaError", t.toString())
            progressBar.visibility = View.GONE
            }
        })
    }

    fun chamadosActivity(){
        val intent = Intent(this@DetalhesActivity, ChamadosActivity::class.java)
        intent.putExtra("Chave", chave)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startActivity(this@DetalhesActivity, intent, null)
        } else {
            startActivity(intent)
        }
    }

    //Usa a classe myLocation pra pegar latitude e longitude
    fun getMyDistance(){

        val locationResult = object : MyLocation.LocationResult() {
            override fun gotLocation(location: Location?) {
                val lat = location!!.latitude
                val lon = location.longitude
                compareDistance(lat, lon)
            }
        }

        val myLocation = MyLocation()
        myLocation.getLocation(this@DetalhesActivity, locationResult)
    }

    //Compara distancia usando Api do google maps "SphericalUtil"
    fun compareDistance(userLatitude : Double, userLongitude : Double){

//        Teste
//        val results = FloatArray(1)
//        Location.distanceBetween(userLatitude, userLongitude,
//            -23.4576738,-46.5032245,  //Insira: latitudeCliente, longitudeCliente
//            results)
//        val distance2 = results[0]

//        Produção
        val results = FloatArray(1)
        Location.distanceBetween(userLatitude, userLongitude,
            latitude!!.split(",")[0].toDouble(),
            longitude!!.split(",")[0].toDouble(),
            results)
        val distance2 = results[0]

        Log.d("Distancia", distance2!!.toString()) //deu certo, margem de erro de 5 metros
        Log.d("distanciaMinima", distanciaMinima!!.toString())
        Log.d("userlatitude", userLatitude!!.toString())
        Log.d("userlongitude", userLongitude!!.toString())
        Log.d("clientelatitude", latitude!!.toString())
        Log.d("clientelongitude", longitude!!.toString())

        noPerimetro = distance2 < distanciaMinima!!.toDouble()
    }
}