@file:Suppress("UNCHECKED_CAST")

package com.example.chapter06.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chapter06.Repository

class ViewModelFactory(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(TemperatureViewModel::class.java))
            TemperatureViewModel(repository) as T
        else
            DistancesViewModel(repository) as T
}