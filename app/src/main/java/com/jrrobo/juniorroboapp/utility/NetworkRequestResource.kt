package com.jrrobo.juniorroboapp.utility

// Network resource class to wrap the network request and responses as Success or Error messages
sealed class NetworkRequestResource<T>(val data: T?, val message: String?) {
    class Success<T>(data: T) : NetworkRequestResource<T>(data, null)
    class Error<T>(message: String) : NetworkRequestResource<T>(null, message)
}