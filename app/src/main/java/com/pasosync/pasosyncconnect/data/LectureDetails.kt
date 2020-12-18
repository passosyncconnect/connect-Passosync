package com.pasosync.pasosyncconnect.data

import java.io.Serializable

data class LectureDetails(
    var lectureName: String? = "",
    var lectureContent: String? = "",
    var lectureImageUrl: String? = "",
    var lectureVideoUrl: String? = "",
    var lecturePdfUrl: String? = "",
    var date: String? = null,
    var seacrh: String? = "",
    var type: String? = ""
) : Serializable