package com.github.tedblair2.payment

import com.github.tedblair2.model.*

interface MpesaService {
    suspend fun initiateLipaNaMpesa():ApiResult<MpesaResponse>
    suspend fun queryMpesaPayment():ApiResult<QueryResponse>
}