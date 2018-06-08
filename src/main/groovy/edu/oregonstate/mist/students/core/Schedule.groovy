package edu.oregonstate.mist.students.core

import com.fasterxml.jackson.annotation.JsonFormat

import java.time.LocalTime

class Schedule {
    List<Course> courses
}

class Course {
    String courseID
    Integer credits
    Boolean withdrawn
    String courseSubject
    String courseNumber
    String courseSection
    String finalLetterGrade
    Integer finalGrade

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    Date courseStartDate

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    Date courseEndDate

    List<MeetingTime> meetingTimes
}

class MeetingTime {
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    Date startDate

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    Date endDate

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss")
    LocalTime beginTime

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss")
    LocalTime endTime

    String buildingCode
    String roomCode
    Boolean meetsSunday
    Boolean meetsMonday
    Boolean meetsTuesday
    Boolean meetsWednesday
    Boolean meetsThursday
    Boolean meetsFriday
    Boolean meetsSaturday
}

