package com.pasosync.pasosyncconnect.data

data class ProgressDetailsToCoach (
    var coachName: String? = "",
    var coachEmail: String = "",
    var coachProfilePicUri: String? = "",
    var progressTitle: String? = "",
    var progressDescription: String? = "",
    var progressImage: String? = "",
    var date: String? = "",
    var timestamp: Long=0L

)