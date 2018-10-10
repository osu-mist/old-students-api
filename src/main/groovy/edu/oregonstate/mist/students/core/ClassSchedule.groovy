package edu.oregonstate.mist.students.core

import com.fasterxml.jackson.annotation.JsonFormat
import edu.oregonstate.mist.students.db.BackendClassSchedule
import edu.oregonstate.mist.students.db.BackendFaculty
import edu.oregonstate.mist.students.db.BackendMeetingTime

import java.time.LocalDate
import java.time.LocalTime

class ClassSchedule {
    String academicYear
    String academicYearDescription
    String courseReferenceNumber
    String courseSubject
    String courseSubjectDescription
    String courseNumber
    String courseTitle
    String sectionNumber
    String term
    String termDescription
    String classFormat
    Integer creditHours
    String registrationStatus
    String gradingMode
    Boolean continuingEducation
    List<Faculty> faculty
    List<MeetingTime> meetingTimes

    static fromBackendClassSchedule(BackendClassSchedule backendClassSchedule) {
        backendClassSchedule.with {
            new ClassSchedule(
                    academicYear: academicYear,
                    academicYearDescription: academicYearDescription,
                    courseReferenceNumber: courseReferenceNumber,
                    courseSubject: subject,
                    courseSubjectDescription: subjectDescription,
                    courseNumber: courseNumber,
                    courseTitle: courseTitle,
                    sectionNumber: sequenceNumber,
                    term: term,
                    termDescription: termDescription,
                    classFormat: scheduleDescription,
                    creditHours: creditHour,
                    registrationStatus: registrationStatus,
                    gradingMode: gradingModeDescription,
                    continuingEducation: continuingEducation,
                    faculty: faculty.collect { Faculty.fromBackendFaculty(it) },
                    meetingTimes: meetingTimes.collect { MeetingTime.fromBackendMeetingTime(it) }
            )
        }
    }
}

class Faculty {
    String osuID
    String name
    String email
    Boolean primary

    static fromBackendFaculty(BackendFaculty backendFaculty) {
        backendFaculty.with {
            new Faculty(
                    osuID: bannerId,
                    name: displayName,
                    email: emailAddress,
                    primary: primaryIndicator
            )
        }
    }
}

class MeetingTime {
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalDate beginDate
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalTime beginTime
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalDate endDate
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalTime endTime
    String room
    String building
    String buildingDescription
    String campus
    BigDecimal hoursPerWeek
    Integer creditHourSession
    Boolean meetsSunday
    Boolean meetsMonday
    Boolean meetsTuesday
    Boolean meetsWednesday
    Boolean meetsThursday
    Boolean meetsFriday
    Boolean meetsSaturday

    static fromBackendMeetingTime(BackendMeetingTime backendMeetingTime) {
        backendMeetingTime.with {
            new MeetingTime(
                    beginDate: startDate,
                    beginTime: beginTime,
                    endDate: endDate,
                    endTime: endTime,
                    room: room,
                    building: building,
                    buildingDescription: buildingDescription,
                    campus: campusDescription,
                    hoursPerWeek: hoursWeek,
                    creditHourSession: creditHourSession,
                    meetsSunday: sunday,
                    meetsMonday: monday,
                    meetsTuesday: tuesday,
                    meetsWednesday: wednesday,
                    meetsThursday: thursday,
                    meetsFriday: friday,
                    meetsSaturday: saturday
            )
        }
    }
}