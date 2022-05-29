package com.biz.bizom.presentation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biz.bizom.data.Repository.HomeRepository
import com.biz.bizom.data.sources.Resource
import com.biz.bizom.domain.DataResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    private val _mycustomUi: MutableLiveData<Resource<DataResponse>> = MutableLiveData()

    val myCustomUi: LiveData<Resource<DataResponse>>
        get() = _mycustomUi

    fun getCustomUi() = viewModelScope.launch {
        _mycustomUi.value = Resource.Loading
        _mycustomUi.value = repository.fetchCustomUi()
    }
}