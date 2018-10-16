package edu.oregonstate.mist.students.core

import edu.oregonstate.mist.students.db.BackendGrade

class Grade {
    String courseReferenceNumber
    String gradeFinal
    String gradeAcademicHistoryFinal
    String courseSubject
    String courseSubjectDescription
    String courseNumber
    String courseTitle
    String sectionNumber
    String term
    String termDescription
    String scheduleDescription
    String scheduleType
    Integer creditHours
    String registrationStatus
    String courseLevel

    static Grade fromBackendGrade(BackendGrade backendGrade) {
        backendGrade.with {
            new Grade(
                    courseReferenceNumber: crn,
                    gradeFinal: gradeFinal,
                    gradeAcademicHistoryFinal: gradeInAcadHistory,
                    courseSubject: courseSubject,
                    courseSubjectDescription: courseSubjectDescription,
                    courseNumber: courseNumber,
                    courseTitle: courseTitle,
                    sectionNumber: sequenceNumber,
                    term: term,
                    termDescription: termDescription,
                    scheduleDescription: classFormatDescription,
                    scheduleType: classFormat,
                    creditHours: creditHour,
                    registrationStatus: registrationStatus,
                    courseLevel: courseLevel
            )
        }
    }
}
