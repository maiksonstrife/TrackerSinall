package com.example.trackersinall.view

import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.trackersinall.R
import com.example.trackersinall.util.CooldownClick
import com.example.trackersinall.util.MyLocation
import com.example.trackersinall.util.SharedPreference
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.activity_menu_selection.*


class MenuSelectionActivity : AppCompatActivity() {
    private var sharedPreference: SharedPreference?=null
    private var nomeFuncionario: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_selection)
        sharedPreference = SharedPreference(this)
        nomeFuncionario = findViewById(R.id.nomeFuncionario)

        //Carrega apelido
        if (sharedPreference!!.getValueString("apelido")!=null) {
            val savedString = sharedPreference!!.getValueString("apelido")!!
            nomeFuncionario!!.text = savedString
        }else{
            nomeFuncionario!!.hint="Não Encontrado"
        }

        val separadosButtonClickListener = CooldownClick {
            val intent = Intent(this@MenuSelectionActivity, ChamadosActivity::class.java)
            intent.putExtra("Chave", "SP")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startActivity(this@MenuSelectionActivity, intent, null)
                finish()
            } else {
                startActivity(intent)
                finish()
            }
        }
        separadosButton!!.setOnClickListener(separadosButtonClickListener)

        val pendenciasButtonClickListener = CooldownClick {
            val intent = Intent(this@MenuSelectionActivity, ChamadosActivity::class.java)
            intent.putExtra("Chave", "PD")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startActivity(this@MenuSelectionActivity, intent, null)
                finish()
            } else {
                startActivity(intent)
                finish()
            }
        }
        pendenciasButton!!.setOnClickListener(pendenciasButtonClickListener)

        val naoIniciadosButtonClickListener = CooldownClick {
            val intent = Intent(this@MenuSelectionActivity, ChamadosActivity::class.java)
            intent.putExtra("Chave", "NI")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startActivity(this@MenuSelectionActivity, intent, null)
                finish()
            } else {
                startActivity(intent)
                finish()
            }
        }
        naoIniciadosButton!!.setOnClickListener(naoIniciadosButtonClickListener)

        val aguardandoButtonClickListener = CooldownClick {
            val intent = Intent(this@MenuSelectionActivity, ChamadosActivity::class.java)
            intent.putExtra("Chave", "AP")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startActivity(this@MenuSelectionActivity, intent, null)
                finish()
            } else {
                startActivity(intent)
                finish()
            }
        }
        aguardandoButton!!.setOnClickListener(aguardandoButtonClickListener)

        val fechadosButtonClickListener = CooldownClick {
            val intent = Intent(this@MenuSelectionActivity, ChamadosActivity::class.java)
            intent.putExtra("Chave", "FN")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startActivity(this@MenuSelectionActivity, intent, null)
                finish()
            } else {
                startActivity(intent)
                finish()
            }
        }
        fechadosDiaButton!!.setOnClickListener(fechadosButtonClickListener)

        val sairButtonClickListener = CooldownClick {
            val intent = Intent(this@MenuSelectionActivity, MainActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startActivity(this@MenuSelectionActivity, intent, null)
                finish()
            } else {
                startActivity(intent)
                finish()
            }
        }
        logout!!.setOnClickListener(sairButtonClickListener)

        //testMyLocation()
    }

    //Usa a classe myLocation pra pegar latitude e longitude
    fun testMyLocation(){
        val locationResult = object : MyLocation.LocationResult() {

            override fun gotLocation(location: Location?) {

                val lat = location!!.latitude
                val lon = location.longitude

                Toast.makeText(this@MenuSelectionActivity, "$lat --SLocRes-- $lon", Toast.LENGTH_SHORT).show()
                compareDistance(lat, lon)
            }
        }

        val myLocation = MyLocation()
        myLocation.getLocation(this@MenuSelectionActivity, locationResult)
    }
    //Compara distancia usando Api do google maps "SphericalUtil"
    fun compareDistance(userLatitude : Double, userLongitude : Double){
        var posicaoInicial = LatLng(userLatitude, userLongitude)
        var posicaiFinal =  LatLng(-23.5706947,-46.3049527)
        var distance : Double? =null

        distance = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);
        Log.d("Distancia", formatNumber(distance))
        Toast.makeText(this@MenuSelectionActivity, "A Distancia é = "+ formatNumber(distance), Toast.LENGTH_SHORT).show()
    }
    //transforma metros em kilometros caso passe de mil metros
    fun formatNumber(distance : Double) : String{
        var distanceConverted = distance
        var unit = "m"

        if (distance >= 1000){
            distanceConverted /= 1000
            unit = "km"
            return String.format("%4.3f%s", distanceConverted, unit)
        }else{
            return String.format("%4.3f%s", distance, unit)
        }
    }
}

