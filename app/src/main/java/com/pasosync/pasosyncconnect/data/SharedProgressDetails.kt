package com.pasosync.pasosyncconnect.data

data class SharedProgressDetails(
    var progressTitle: String? = "",
    var progressDescription: String? = "",
    var progressImage: String? = "",
    var date: String? = "",
    var timestamp: Long=0L,
     var userName:String?="",
    var userEmail:String?="",
    var userPicUri:String?=""


)