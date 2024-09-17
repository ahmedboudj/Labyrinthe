package com.example.mylabo4

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.mylabyrinthe.Mur

class LabyrintheView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val ligneArriveeY = 1400f

    private val paintBalle = Paint().apply {
        color = Color.GREEN
        isAntiAlias = true
    }

    private val paintTrou = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
    }
    private val paintMur = Paint().apply {
        color = Color.RED
        isAntiAlias = true
    }
    private val balleRadius = 20f
    private var balle: Balle? = null
    private val trous = mutableListOf<Troue>()
    private val murs = mutableListOf<Mur>()

    fun dessinerBalle(b: Balle) {
        balle = b
        invalidate()
    }

    fun ajouterTrou(t: Troue) {
        trous.add(t)
        invalidate()
    }

    fun dessinerMur(m: Mur) {
        murs.add(m)
        invalidate()
    }

    fun reinitialiserMurs(mursReinitialises: List<Mur>) {
        murs.clear()
        murs.addAll(mursReinitialises)
        invalidate()
    }

    fun reinitialiserTrous(trousReinitialises: List<Troue>) {
        trous.clear()
        trous.addAll(trousReinitialises)
        invalidate()
    }

    fun checkCollision(balle: Balle): Boolean {
        for (trou in trous) {
            val distance = calculateDistance(balle.x, balle.y, trou.x, trou.y)
            if (distance <= balleRadius + trou.rayon) {
                return true
            }
        }
        return false
    }

    fun checkCollisionMur(balle: Balle, mur: Mur): Boolean {

        val balleGauche = balle.x - balleRadius
        val balleDroite = balle.x + balleRadius
        val balleHaut = balle.y - balleRadius
        val balleBas = balle.y + balleRadius

        val murGauche = mur.x
        val murDroite = mur.x + mur.largeur
        val murHaut = mur.y
        val murBas = mur.y + mur.hauteur

        if (balleDroite >= murGauche && balleGauche <= murDroite && balleBas >= murHaut && balleHaut <= murBas) {

            return true
        }
        return false
    }

    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return Math.sqrt(Math.pow((x2 - x1).toDouble(), 2.0) + Math.pow((y2 - y1).toDouble(), 2.0)).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paintLigneArrivee = Paint().apply {
            color = Color.BLACK
            strokeWidth = 6f
        }
        canvas.drawLine(0f, ligneArriveeY, width.toFloat(), ligneArriveeY, paintLigneArrivee)

        // Dessiner les trous, murs, balle
        trous.forEach { trou ->
            canvas.drawCircle(trou.x, trou.y, trou.rayon, paintTrou)
        }

        murs.forEach { mur ->
            canvas.drawRect(mur.x, mur.y, mur.x + mur.largeur, mur.y + mur.hauteur, paintMur)
        }

        balle?.let { b ->
            canvas.drawCircle(b.x, b.y, b.rayon, paintBalle)
        }
    }


}
