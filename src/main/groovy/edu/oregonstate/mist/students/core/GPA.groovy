package edu.oregonstate.mist.students.core

import edu.oregonstate.mist.students.db.BackendGPA

class GPALevels {
    List<GPA> gpaLevels

    static GPALevels fromBackendGPA(List<BackendGPA> backendGPAs) {
        new GPALevels(
                gpaLevels: backendGPAs.collect {
                    GPA.fromBackendGPA(it)
                }
        )
    }
}

class GPA {
    String gpa
    Integer gpaCreditHours
    String gpaType
    Integer creditHoursAttempted
    Integer creditHoursEarned
    Integer creditHoursPassed
    String level
    String qualityPoints

    static GPA fromBackendGPA(BackendGPA backendGPA) {
        new GPA(
                gpa: backendGPA.gpa,
                gpaCreditHours: backendGPA.gpaHours,
                gpaType: backendGPA.gpaTypeIndicatorDescription,
                creditHoursAttempted: backendGPA.hoursAttempted,
                creditHoursEarned: backendGPA.hoursEarned,
                creditHoursPassed: backendGPA.hoursPassed,
                level: backendGPA.levelDescription,
                qualityPoints: backendGPA.qualityPoints
        )
    }
}