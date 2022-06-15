package com.morato.crypto.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinAboutItem (
    var coinWebsite : String? = "NO-Data",
    var coinGithub : String? = "NO-Data",
    var coinTwitter : String? = "NO-Data",
    var coinDesc : String? = "NO-Data",
    var coinReddit : String? = "NO-Data"

 ):Parcelable