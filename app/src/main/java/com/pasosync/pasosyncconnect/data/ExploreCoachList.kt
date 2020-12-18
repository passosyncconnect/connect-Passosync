package com.pasosync.pasosyncconnect.data

import java.io.Serializable

data class ExploreCoachList(
    var coachName: String? = "",
    var coachEmail: String = "",
    var coachMobile: String? = "",
    var coachAbout: String? = "",
    var coachProfilePicUri: String? = "",
    var coachIntroVideoUri: String? = "",
    var title: Long = 0L,
    var free:Long=0L,
    var coachExperience:String?="",
    var coachType:String?=""

) : Serializable