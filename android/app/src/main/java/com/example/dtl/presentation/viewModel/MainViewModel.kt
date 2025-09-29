package com.example.dtl.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dtl.data.database.model.Request
import com.example.dtl.data.network.model.AnalysisResult
import com.example.dtl.data.network.model.OrderResultData
import com.example.dtl.data.network.model.PlantDetails
import com.example.dtl.domain.DataRepository
import com.example.dtl.domain.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    private val _analysisDetails = mutableStateOf<ResourceState<AnalysisResult>>(ResourceState.Loading)
    val analysisDetails: State<ResourceState<AnalysisResult>> = _analysisDetails

    private val _plantDetails = mutableStateOf<ResourceState<PlantDetails>>(ResourceState.Loading)
    val plantDetails: State<ResourceState<PlantDetails>> = _plantDetails

    private val _requestDetails = mutableStateOf<ResourceState<OrderResultData>>(ResourceState.Loading)
    val requestDetails: State<ResourceState<OrderResultData>> = _requestDetails

    private val _isAscending = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val allRequests = _isAscending.flatMapLatest { isAscending ->
        if (isAscending) {
            repository.getAllRequestsAsc()
        } else {
            repository.getAllRequests()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun changeAscendingSort(isAscending: Boolean) {
        _isAscending.value = isAscending
    }
    
    fun addRequest(filepath: String, title: String) = viewModelScope.launch {
        repository.addRequest(
            filepath = filepath,
            title = title
        )
        repository.scheduleSync()
    }

    fun loadRequestDetails(id: Int) = viewModelScope.launch {
        viewModelScope.launch {
            _requestDetails.value = ResourceState.Loading
            try {
                val request = repository.getOrderById(id)
                _requestDetails.value = ResourceState.Success(request.data!!)
            } catch (e: Exception) {
                Log.e("MainViewModel", e.message.orEmpty())
            }
        }
    }

    fun loadPlantDetails(order_id: Int, result_id: Int) = viewModelScope.launch {
        viewModelScope.launch {
            _plantDetails.value = ResourceState.Loading
            try {
                val result = repository.getPlantById(order_id, result_id)
                _plantDetails.value = ResourceState.Success(result.data!!)
            } catch (e: Exception) {
                Log.e("MainViewModel", e.message.orEmpty())
            }
        }
    }

    fun loadAnalysisDetails(order_id: Int) = viewModelScope.launch {
        viewModelScope.launch {
            _analysisDetails.value = ResourceState.Loading
            try {
                val result = repository.getAnalysisById(order_id)
                _analysisDetails.value = ResourceState.Success(result.data!!)
            } catch (e: Exception) {
                Log.e("MainViewModel", e.message.orEmpty())
            }
        }
    }

    fun deleteRequest(request: Request) {
        viewModelScope.launch {
            try {
                repository.deleteRequest(request = request)
            } catch (e: Exception) {
                Log.e("MainViewModel", e.message.orEmpty())
            }
        }
    }
    
    fun manualSync(onProcessingFinished: () -> Unit) = viewModelScope.launch {
        repository.processPendingRequests(onProcessingFinished = onProcessingFinished)
    }
}