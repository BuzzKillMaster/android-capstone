package com.example.littlelemoncapstone

import kotlinx.serialization.Serializable

/*
* In this file, create the MenuNetworkdata class and MenuItemNetwork data class with @Serializable and @SerialName annotations.
* These classes contain data classes that are used to decode the object received from the server.
* In the MainActivityclass, create the instance of the Ktor httpClient and install ContentNegotiation with JSON.
*/

@Serializable
data class MenuNetwork(
    val menu: List<MenuItemNetwork>
)

@Serializable
data class MenuItemNetwork(
    val id: Int,
    val title: String,
    val description: String,
    val price: String,
    val image: String,
    val category: String
)
