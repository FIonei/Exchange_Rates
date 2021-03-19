package com.example.exchangerates

import com.example.exchangerates.Valutes.*
import com.google.gson.annotations.SerializedName

data class ValutesList(
        @SerializedName("AUD")        val aud: AUD,
        @SerializedName("AZN")        val azn: AZN,
        @SerializedName("GBP")        val gbp: GBP,
        @SerializedName("AMD")        val amd: AMD,
        @SerializedName("BYN")        val byn: BYN,
        @SerializedName("BGN")        val bgn: BGN,
        @SerializedName("BRL")        val brl: BRL,
        @SerializedName("HUF")        val huf: HUF,
        @SerializedName("HKD")        val hkd: HKD,
        @SerializedName("DKK")        val dkk: DKK,
        @SerializedName("USD")        val usd: USD,
        @SerializedName("EUR")        val eur: EUR,
        @SerializedName("INR")        val inr: INR,
        @SerializedName("KZT")        val kzt: KZT,
        @SerializedName("CAD")        val cad: CAD,
        @SerializedName("KGS")        val kgs: KGS,
        @SerializedName("CNY")        val cny: CNY,
        @SerializedName("MDL")        val mdl: MDL,
        @SerializedName("NOK")        val nok: NOK,
        @SerializedName("PLN")        val pln: PLN,
        @SerializedName("RON")        val ron: RON,
        @SerializedName("XDR")        val xdr: XDR,
        @SerializedName("SGD")        val sgd: SGD,
        @SerializedName("TJS")        val tjs: TJS,
        @SerializedName("TRY")        val tryr: TRY,
        @SerializedName("TMT")        val tmt: TMT,
        @SerializedName("UZS")        val uzs: UZS,
        @SerializedName("UAH")        val auh: UAH,
        @SerializedName("CZK")        val czk: CZK,
        @SerializedName("SEK")        val sek: SEK,
        @SerializedName("CHF")        val chf: CHF,
        @SerializedName("ZAR")        val zar: ZAR,
        @SerializedName("KRW")        val krw: KRW,
        @SerializedName("JPY")        val jpy: JPY
        )