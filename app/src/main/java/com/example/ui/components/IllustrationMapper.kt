package com.example.ui.components

import androidx.annotation.DrawableRes
import com.example.R

object IllustrationMapper {
    @DrawableRes
    fun category(categoryId: String): Int = when (categoryId) {
        "cat_hardware" -> R.drawable.category_hardware
        "cat_software" -> R.drawable.category_software
        "cat_internet" -> R.drawable.category_internet
        "cat_performance" -> R.drawable.category_performance
        "cat_display" -> R.drawable.category_display
        "cat_peripheral" -> R.drawable.category_peripheral
        else -> R.drawable.category_other
    }

    @DrawableRes
    fun result(causeId: String, categoryId: String): Int = when (causeId) {
        "cause_ram" -> R.drawable.img_ram_repair_1785497686389
        "cause_psu" -> R.drawable.img_psu_repair_1785497672881
        else -> category(categoryId)
    }

    @DrawableRes
    fun repairStep(drawableName: String?): Int? = when (drawableName) {
        "img_ram_repair_1785497686389" -> R.drawable.img_ram_repair_1785497686389
        "img_psu_repair_1785497672881" -> R.drawable.img_psu_repair_1785497672881
        else -> null
    }
}
