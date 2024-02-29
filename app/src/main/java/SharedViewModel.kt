package com.tirsankardan.tirsanuygulama

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var isAdmin = MutableLiveData<Boolean>()
}