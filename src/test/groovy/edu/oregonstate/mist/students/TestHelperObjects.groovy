package edu.oregonstate.mist.students

import edu.oregonstate.mist.students.core.AcademicStatus
import edu.oregonstate.mist.students.core.AccountBalance
import edu.oregonstate.mist.students.core.AccountTransactions
import edu.oregonstate.mist.students.core.Award
import edu.oregonstate.mist.students.core.ClassSchedule
import edu.oregonstate.mist.students.core.Faculty
import edu.oregonstate.mist.students.core.GPA
import edu.oregonstate.mist.students.core.GPALevels
import edu.oregonstate.mist.students.core.Grade
import edu.oregonstate.mist.students.core.Hold
import edu.oregonstate.mist.students.core.Holds
import edu.oregonstate.mist.students.core.MeetingTime
import edu.oregonstate.mist.students.core.Transaction

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class TestHelperObjects {
    static final String fakeID = "931234567"

    static final AccountBalance fakeAccountBalance = new AccountBalance(currentBalance: 123.45)

    static final AccountTransactions fakeAccountTransactions = new AccountTransactions(
            transactions: [new Transaction(
                    amount: 32423.33,
                    description: "Some payment.",
                    entryDate: Instant.now()
            )]
    )

    static final List<Award> fakeAwards = [new Award(
            effectiveStartDate: new Date(),
            effectiveEndDate: new Date(),
            offerAmount: 2000,
            offerExpirationDate: new Date(),
            acceptedAmount: 1500,
            acceptedDate: new Date(),
            paidAmount: 1000,
            awardStatus: "Accepted"
    )]

    static final Holds fakeHolds = new Holds(
            holds: [new Hold(
                    fromDate: LocalDate.now(),
                    toDate: LocalDate.now().plusDays(1),
                    description: "Tuition",
                    processesAffected: ["Graduation"],
                    reason: "Never paid tuition!"
            )]
    )

    static final fakeGPALevels = new GPALevels(gpaLevels: [fakeGPA])

    static final GPA fakeGPA = new GPA(
            gpa: "4.00",
            gpaCreditHours: 173,
            gpaType: "Total",
            creditHoursAttempted: 173,
            creditHoursEarned: 173,
            creditHoursPassed: 173,
            level: "Undergrad",
            qualityPoints: "233.00"
    )

    static final List<AcademicStatus> fakeAcademicStatus = [new AcademicStatus(
            academicStanding: "good",
            term: "201801",
            termDescription: "Fall 2017",
            gpa: [fakeGPA]
    )]

    static final List<Grade> fakeGrades = [new Grade(
            courseReferenceNumber: "23423",
            gradeFinal: "A",
            gradeAcademicHistoryFinal: "A",
            courseSubject: "MTH",
            courseSubjectDescription: "Math",
            courseNumber: "101",
            courseTitle: "Basic math",
            sectionNumber: "001",
            term: "201901",
            termDescription: "Fall 2018",
            scheduleDescription: "Lecture",
            scheduleType: "A",
            creditHours: 4,
            registrationStatus: "Registered",
            courseLevel: "Undergrad"
    )]

    static final List<ClassSchedule> fakeSchedule = [new ClassSchedule(
            academicYear: "1819",
            academicYearDescription: "2018-2019",
            courseReferenceNumber: "23423",
            courseSubject: "MTH",
            courseSubjectDescription: "Math",
            courseNumber: "101",
            courseTitle: "Basic math",
            sectionNumber: "001",
            term: "201901",
            termDescription: "Fall 2018",
            scheduleDescription: "Lecture",
            scheduleType: "A",
            creditHours: 4,
            registrationStatus: "Registered",
            gradingMode: "hard",
            continuingEducation: false,
            faculty: [new Faculty(
                    osuID: "933333333",
                    name: "Professor Chalkboard",
                    email: "theprofessor@mathdepartment.com",
                    primary: true
            )],
            meetingTimes: [new MeetingTime(
                    beginDate: LocalDate.now(),
                    beginTime: LocalTime.now(),
                    endDate: LocalDate.now(),
                    endTime: LocalTime.now(),
                    room: "3",
                    building: "OLDB",
                    buildingDescription: "Some old building",
                    campus: "main campus",
                    hoursPerWeek: 1.33,
                    creditHourSession: 4,
                    scheduleType: "A",
                    weeklySchedule: ['M', 'W', 'F']
            )]
    )]
}
