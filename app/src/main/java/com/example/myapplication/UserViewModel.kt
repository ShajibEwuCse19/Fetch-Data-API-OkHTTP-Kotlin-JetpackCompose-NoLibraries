package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users //users is a copy of _user so that it can use
                                            // outside of the class without changing the real data [establish immutability]

    init {
        fetchUsersData()
    }

    private fun fetchUsersData() {
        //fetchUsers() is a suspend fun, so it need a coroutine scope or another suspend fun
        viewModelScope.launch {
            val response = fetchUsers()
            if (response != null) {
                _users.value = response.data
            }
        }
    }
}
