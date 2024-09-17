package com.example.mylabo4

import android.app.AlertDialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mylabyrinthe.Mur
import kotlin.random.Random

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var labyrintheView: LabyrintheView
    private lateinit var balle: Balle
    private var gameOver = false
    private lateinit var murs: List<Mur>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mylabo4.R.layout.activity_main)

        labyrintheView = findViewById(com.example.mylabo4.R.id.labyrintheView)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        balle = Balle(400f, 100f, 0f, 0f, 45f)

        // murs fixes
//        murs = genererMursFixes()

        // murs aléatoires
        murs = genererMursAleatoires()

        labyrintheView.dessinerBalle(balle)

        // Ajouter les trous
        val trous = genererTrous()
        trous.forEach { labyrintheView.ajouterTrou(it) }

        murs.forEach { labyrintheView.dessinerMur(it) }

        //capteur d'accéléromètre
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            if (!gameOver) {
                moveBall(event.values[0], event.values[1])
            }
            labyrintheView.invalidate()
        }
    }

    private fun moveBall(accelerationX: Float, accelerationY: Float) {
        val newPosX = balle.x + accelerationX
        val newPosY = balle.y + accelerationY

        val viewWidth = labyrintheView.width.toFloat()
        val viewHeight = labyrintheView.height.toFloat()

        val balleRadius = balle.rayon
        if (newPosX >= balleRadius && newPosX <= viewWidth - balleRadius && newPosY >= balleRadius && newPosY <= viewHeight - balleRadius) {
            balle.mettreAJourPosition(accelerationX, accelerationY)
        } else {
            balle.annulerDernierMouvement()
        }
        // arrêter le mouvement de la balle en collision avec le mur
        murs.forEach { mur ->
            if (labyrintheView.checkCollisionMur(balle, mur)) {
                balle.annulerDernierMouvement()
            }
        }

        if (balle.y > 1400f) {
            Toast.makeText(this, "Gagné, la balle a traversé le labyrinthe!", Toast.LENGTH_SHORT).show()
            resetGame()
        }

        if (labyrintheView.checkCollision(balle)) {
            gameOver = true
            showGameOverMsg()
            Handler().postDelayed({
                resetGame()
            }, 2000)
        }

        labyrintheView.invalidate()
    }

    private fun resetGame() {
        balle.resetPosition()
        gameOver = false

       // murs = genererMursFixes()
        murs = genererMursAleatoires()

        labyrintheView.reinitialiserMurs(murs)
        val trous = genererTrous()
        labyrintheView.reinitialiserTrous(trous)
    }

    private fun showGameOverMsg() {
        Toast.makeText(this, "Game Over :(", Toast.LENGTH_SHORT).show()

        AlertDialog.Builder(this)
            .setTitle("Rejouer ?")
            .setPositiveButton("Oui") { dialog, which ->
                resetGame()
            }
            .setNegativeButton("Non") { dialog, which ->
                finish()
            }
            .show()
    }

//    private fun genererMursFixes(): List<Mur> {
//        return listOf(
//            // Murs verticaux
//            Mur(100f, 200f, 60f, 200f, 0f),
//            Mur(300f, 400f, 60f, 200f, 0f),
//            Mur(500f, 600f, 60f, 200f, 0f),
//            Mur(700f, 800f, 60f, 200f, 0f),
//            Mur(900f, 1000f, 60f, 200f, 0f),
//            // Murs horizontaux
//            Mur(200f, 100f, 200f, 60f, 0f),
//            Mur(400f, 300f, 200f, 60f, 0f),
//            Mur(600f, 500f, 200f, 60f, 0f),
//            Mur(800f, 700f, 200f, 60f, 0f),
//            Mur(1000f, 900f, 200f, 60f, 0f)
//        )
//    }
                        ////////////////////////////////////

    private fun genererMursAleatoires(): List<Mur> {
        val murs = mutableListOf<Mur>()
        val largeurMur = 60f
        val hauteurMur = 200f
        val nombreDeMurs = 5

        fun collisionMur(x: Float, y: Float, largeur: Float, hauteur: Float): Boolean {
            for (mur in murs) {
                if (x < mur.x + mur.largeur && x + largeur > mur.x &&
                    y < mur.y + mur.hauteur && y + hauteur > mur.y
                ) {
                    return true
                }
            }
            return false
        }

        for (i in 0 until nombreDeMurs) {
            var x = Random.nextFloat() * 1000f
            var y = Random.nextFloat() * 1400f
            val orientation = Random.nextBoolean()

            // pour la vérification des collisions
            while (collisionMur(x, y, if (orientation) largeurMur else hauteurMur, if (orientation) hauteurMur else largeurMur)) {
                x = Random.nextFloat() * 1000f
                y = Random.nextFloat() * 1400f
            }

            if (orientation) {

                murs.add(Mur(x, y, largeurMur, hauteurMur, 0f))
            } else {
                murs.add(Mur(x, y, hauteurMur, largeurMur, 0f))
            }
        }

        return murs
    }

    private fun genererTrous(): List<Troue> {
        val trous = mutableListOf<Troue>()
        val nombreDeTrous = 5

        for (i in 0 until nombreDeTrous) {
            val x = Random.nextFloat() * 1000f
            val y = Random.nextFloat() * 1400f
            val tailleTrou = 60f

            trous.add(Troue(x, y, tailleTrou))
        }

        return trous
    }
}
