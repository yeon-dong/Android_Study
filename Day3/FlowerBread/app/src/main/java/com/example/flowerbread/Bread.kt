package com.example.flowerbread

abstract class Bread {
    lateinit var sauce: String
    lateinit var shape: String
    abstract val price: Int

    fun putSauce(sauce: String) {
        this.sauce = sauce
    }

    abstract fun setShape()
}

class FishBread : Bread() {
    override val price: Int = 1000

    override fun setShape() {
        shape = "Fish"
    }
}

class FlowerBread : Bread() {
    override val price: Int = 1500

    override fun setShape() {
        shape = "Flower"
    }
}