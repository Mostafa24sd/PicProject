package com.example.picproject

import java.text.DecimalFormat

fun Double.calculate() : String {
    val format = DecimalFormat("0.#")
    return format.format(this)
}
