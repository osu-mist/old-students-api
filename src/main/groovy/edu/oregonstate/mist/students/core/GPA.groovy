package edu.oregonstate.mist.students.core

import edu.oregonstate.mist.students.db.BackendGPA

class GPALevels {
    List<GPA> gpaLevels

    static GPALevels fromBackendGPA(List<BackendGPA> backendGPAs) {
        new GPALevels(
                gpaLevels: backendGPAs.collect {
                    new GPA(
                            gpa: it.gpa,
                            gpaCreditHours: it.gpaHours,
                            gpaType: it.gpaTypeIndicatorDescription,
                            creditHoursAttempted: it.hoursAttempted,
                            creditHoursEarned: it.hoursEarned,
                            creditHoursPassed: it.hoursPassed,
                            level: it.levelDescription,
                            qualityPoints: it.qualityPoints
                    )
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
}