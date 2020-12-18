package com.pasosync.pasosyncconnect.data

import java.io.Serializable

data class AllLectureDetails(
    var lectureName: String? = "",
    var lectureContent: String? = "",
    var lectureImageUrl: String? = "",
    var lectureVideoUrl: String? = "",
    var lecturePdfUrl: String? = "",
    var date: String? = null,
    var seacrh: String? = "",
    var type: String? = "",
    var coachName: String? = "",
    var coachProfilePicUri: String? = "",
    var coachEmail:String?=""
) : Serializable