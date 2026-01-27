package com.teamconfused.planmyplate.network

import com.teamconfused.planmyplate.model.AuthResponse
import com.teamconfused.planmyplate.model.ForgotPasswordRequest
import com.teamconfused.planmyplate.model.ForgotPasswordResponse
import com.teamconfused.planmyplate.model.ResetPasswordRequest
import com.teamconfused.planmyplate.model.ResetPasswordResponse
import com.teamconfused.planmyplate.model.SigninRequest
import com.teamconfused.planmyplate.model.SignupRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/signup")
    suspend fun signup(@Body request: SignupRequest): AuthResponse

    @POST("api/auth/signin")
    suspend fun signin(@Body request: SigninRequest): AuthResponse

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse
}