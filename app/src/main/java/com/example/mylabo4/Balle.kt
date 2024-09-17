package com.example.mylabo4

class Balle(var x: Float, var y: Float, var vitesseX: Float, var vitesseY: Float, val rayon: Float) {

    private var precedentX: Float = x
    private var precedentY: Float = y

    fun mettreAJourPosition(accelerationX: Float, accelerationY: Float) {
        // Sauvegarder la position actuelle avant de déplacer la balle
        precedentX = x
        precedentY = y

        x += accelerationX
        y += accelerationY
    }

    fun annulerDernierMouvement() {
        x = precedentX
        y = precedentY
    }

    fun resetPosition() {
        x = 900f
        y = 100f
    }
}

