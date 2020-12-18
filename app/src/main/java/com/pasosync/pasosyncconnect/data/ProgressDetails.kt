package com.pasosync.pasosyncconnect.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ProgressDetails(
    var progressTitle: String? = "",
    var progressDescription: String? = "",
    var progressImage: String? = "",
    var date: String? = "",
    var timestamp: Long=0L


)