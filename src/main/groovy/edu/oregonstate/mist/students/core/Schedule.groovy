package edu.oregonstate.mist.students.core

import com.fasterxml.jackson.annotation.JsonFormat

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

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    Date courseStartDate

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    Date courseEndDate

    List<MeetingTime> meetingTimes
}

class MeetingTime {
    String foo
}

