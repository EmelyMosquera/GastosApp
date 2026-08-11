package com.emely.gastosapp

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Interfaz de Retrofit para definir el Endpoint de consulta asíncrona a internet (Punto 4d)
interface TipoCambioApiService {
    @GET("v6/latest/USD")
    suspend fun obtenerTipoCambio(): Response<TipoCambioRespuesta>
}

// Objeto conector (Singleton) encargado de construir la instancia del cliente Retrofit
object RetrofitCliente {
    private const val BASE_URL = "https://exchangerate-api.com"

    val apiService: TipoCambioApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TipoCambioApiService::class.java)
    }
}
