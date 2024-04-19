package com.guhaejo.codeblack.viewmodel

import androidx.lifecycle.ViewModel
import com.guhaejo.codeblack.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

}