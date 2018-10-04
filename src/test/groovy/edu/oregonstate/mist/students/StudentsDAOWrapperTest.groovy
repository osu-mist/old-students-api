package edu.oregonstate.mist.students

import edu.oregonstate.mist.students.core.AcademicStatus
import edu.oregonstate.mist.students.core.AccountBalance
import edu.oregonstate.mist.students.core.AccountTransactions
import edu.oregonstate.mist.students.core.Award
import edu.oregonstate.mist.students.core.ClassSchedule
import edu.oregonstate.mist.students.core.DualEnrollment
import edu.oregonstate.mist.students.core.Faculty
import edu.oregonstate.mist.students.core.GPA
import edu.oregonstate.mist.students.core.GPALevels
import edu.oregonstate.mist.students.core.Grade
import edu.oregonstate.mist.students.core.Hold
import edu.oregonstate.mist.students.core.Holds
import edu.oregonstate.mist.students.core.MeetingTime
import edu.oregonstate.mist.students.core.Transaction
import edu.oregonstate.mist.students.core.WorkStudyObject
import edu.oregonstate.mist.students.db.HttpStudentsDAO
import edu.oregonstate.mist.students.db.InvalidTermException
import edu.oregonstate.mist.students.db.StudentNotFoundException
import edu.oregonstate.mist.students.db.StudentsDAO
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import groovy.mock.interceptor.MockFor
import org.junit.Test

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

import static org.junit.Assert.assertEquals

class StudentsDAOWrapperTest {
    private static final String endpoint = "https://www.example.com"
    private static final String fakeID = "931234567"
    private static final GPA fakeGPA = new GPA(
            gpa: "4.00",
            gpaCreditHours: 173,
            gpaType: "Total",
            creditHoursAttempted: 173,
            creditHoursEarned: 173,
            creditHoursPassed: 173,
            level: "Undergrad",
            qualityPoints: "233.00"
    )
    private static final List<AcademicStatus> fakeAcademicStatus = [new AcademicStatus(
            academicStanding: "good",
            term: "201801",
            termDescription: "Fall 2017",
            gpa: [fakeGPA]
    )]
    private static final List<Grade> fakeGrades = [new Grade(
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
            classFormat: "Lecture",
            creditHours: 4,
            registrationStatus: "Registered",
            courseLevel: "Undergrad"
    )]
    private static final List<ClassSchedule> fakeSchedule = [new ClassSchedule(
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
            classFormat: "Lecture",
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
                    meetsSunday: false,
                    meetsMonday: true,
                    meetsTuesday: true,
                    meetsWednesday: true,
                    meetsThursday: true,
                    meetsFriday: true,
                    meetsSaturday: true
            )]
    )]
    /**
     * Check that the DAO wrapper returns null if the DAO returns null.
     */
    @Test(expected = StudentNotFoundException.class)
    void getWorkStudyThrowsExceptionIfStudentNotFound() {
        def mockDAO = getMockDAO()
        mockDAO.demand.getPersonID() { null }

        def daoWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance(), null)

        daoWrapper.getWorkStudy(fakeID)
    }

    /**
     * Check that a correct workstudy object is returned from the DAO wrapper
     */
    @Test
    void getWorkStudyTest() {
        List<Award> awards = [new Award(
                effectiveStartDate: new Date(),
                effectiveEndDate: new Date(),
                offerAmount: 2000,
                offerExpirationDate: new Date(),
                acceptedAmount: 1500,
                acceptedDate: new Date(),
                paidAmount: 1000,
                awardStatus: "Accepted"
        )]

        def mockDAO = getMockDAO()
        mockDAO.demand.getPersonID() { "foobar" }
        mockDAO.demand.getWorkStudy() { awards }
        def mockDAOWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance(), null)
        WorkStudyObject workStudy = mockDAOWrapper.getWorkStudy(fakeID)

        assertEquals(workStudy.awards, awards)
    }

    @Test(expected = StudentNotFoundException.class)
    void getDualEnrollmentThrowsExceptionIfStudentNotFound() {
        def mockDAO = getMockDAO()
        mockDAO.demand.getPersonID() { null }

        def daoWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance(), null)

        daoWrapper.getWorkStudy(fakeID)
    }

    @Test(expected = InvalidTermException.class)
    void getDualEnrollmentThrowsExceptionIfInvalidTerm() {
        def mockDAO = getMockDAO()
        mockDAO.demand.isValidTerm() { false }

        def daoWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance(), null)

        daoWrapper.getDualEnrollment(fakeID, "badTerm")
    }

    @Test
    void getCurrentTermIsCalledIfTermEqualsCurrent() {
        def mockDAO = getMockDAO()

        String currentTerm = "201901"
        String termUsedInCall

        mockDAO.demand.getCurrentTerm() { currentTerm }
        mockDAO.demand.isValidTerm() { true }
        mockDAO.demand.getPersonID() { fakeID }
        mockDAO.demand.getDualEnrollment() { String id, String term ->
            termUsedInCall = term
            [new DualEnrollment()]
        }


        def daoWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance(), null)

        daoWrapper.getDualEnrollment(fakeID, "current")

        assertEquals(termUsedInCall, currentTerm)
    }

    @Test
    void testAccountBalance() {
        AccountBalance accountBalance = new AccountBalance(currentBalance: 123.45)

        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getAccountBalance() { accountBalance }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            AccountBalance accountBalanceFromDAOWrapper = daoWrapper.getAccountBalance(fakeID)
            assertEquals(accountBalance, accountBalanceFromDAOWrapper)
        }
    }

    @Test
    void testAccountTransactions() {
        AccountTransactions accountTransactions = new AccountTransactions(
                transactions: [new Transaction(
                        amount: 32423.33,
                        description: "Some payment.",
                        entryDate: Instant.now()
                )]
        )

        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getAccountTransactions() { accountTransactions }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            AccountTransactions transactionsFromDAOWrapper =
                    daoWrapper.getAccountTransactions(fakeID)
            assertEquals(accountTransactions, transactionsFromDAOWrapper)
        }
    }

    @Test
    void testGPA() {
        GPALevels gpaLevels = new GPALevels(gpaLevels: [fakeGPA])

        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getGPA() { gpaLevels }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            GPALevels gpaLevelsFromDAOWrapper = daoWrapper.getGPA(fakeID)
            assertEquals(gpaLevels, gpaLevelsFromDAOWrapper)
        }
    }

    @Test
    void testAcademicStatus() {
        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getAcademicStatus() { String osuID, String term ->
            fakeAcademicStatus
        }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            List<AcademicStatus> academicStatusFromDAOWrapper = daoWrapper.getAcademicStatus(
                    fakeID, "201801")
            assertEquals(fakeAcademicStatus, academicStatusFromDAOWrapper)
        }
    }

    @Test
    void academicStatusUsesCurrentTerm() {
        String currentTerm = "201901"
        String termUsedInCall

        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getAcademicStatus() { String osuID, String term ->
            termUsedInCall = term
            fakeAcademicStatus
        }

        def mockDAO = getMockDAO()

        mockDAO.demand.getCurrentTerm() { currentTerm }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO(mockDAO)
            List<AcademicStatus> academicStatusFromDAOWrapper = daoWrapper.getAcademicStatus(
                    fakeID, "current")
            assertEquals(fakeAcademicStatus, academicStatusFromDAOWrapper)
            assertEquals(currentTerm, termUsedInCall)
        }
    }

    @Test
    void testGrades() {
        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getGrades() {String osuID, String term ->
            fakeGrades
        }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            List<Grade> gradesFromDAOWrapper = daoWrapper.getGrades(fakeID, "201901")
            assertEquals(fakeGrades, gradesFromDAOWrapper)
        }
    }

    @Test
    void gradesUseCurrentTerm() {
        String currentTerm = "201901"
        String termUsedInCall

        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getGrades() { String osuID, String term ->
            termUsedInCall = term
            fakeGrades
        }

        def mockDAO = getMockDAO()

        mockDAO.demand.getCurrentTerm() { currentTerm }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO(mockDAO)
            List<Grade> gradesFromDAOWrapper = daoWrapper.getGrades(fakeID, "current")
            assertEquals(fakeGrades, gradesFromDAOWrapper)
            assertEquals(currentTerm, termUsedInCall)
        }
    }

    @Test
    void testClassSchedule() {
        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getClassSchedule() { String osuID, String term->
            fakeSchedule
        }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            List<ClassSchedule> classScheduleFromDAOWrapper = daoWrapper.getClassSchedule(
                    fakeID, "201801")
            assertEquals(fakeSchedule, classScheduleFromDAOWrapper)
        }
    }

    @Test
    void classScheduleUsesCurrentTerm() {
        String currentTerm = "201901"
        String termUsedInCall

        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getClassSchedule() { String osuID, String term ->
            termUsedInCall = term
            fakeSchedule
        }

        def mockDAO = getMockDAO()

        mockDAO.demand.getCurrentTerm() { currentTerm }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO(mockDAO)
            List<ClassSchedule> classScheduleFromDAOWrapper = daoWrapper.getClassSchedule(fakeID, "current")
            assertEquals(fakeSchedule, classScheduleFromDAOWrapper)
            assertEquals(currentTerm, termUsedInCall)
        }
    }

    @Test
    void testHolds() {
        Holds holds = new Holds(
                holds: [new Hold(
                        fromDate: LocalDate.now(),
                        toDate: LocalDate.now().plusDays(1),
                        description: "Tuition",
                        processesAffected: ["Graduation"],
                        reason: "Never paid tuition!"
                )]
        )

        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getHolds() { holds }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            Holds holdsFromDAOWrapper = daoWrapper.getHolds(fakeID)
            assertEquals(holds, holdsFromDAOWrapper)
        }
    }

    private StudentsDAOWrapper getStudentsDAOWrapperWithHttpDAO(MockFor mockDAO = null) {
        HttpStudentsDAO httpStudentsDAO = new HttpStudentsDAO(null, endpoint)

        if (mockDAO) {
            def daoWrapper = new StudentsDAOWrapper(
                    mockDAO.proxyInstance(), httpStudentsDAO)
        } else {
            def daoWrapper = new StudentsDAOWrapper(null, httpStudentsDAO)
        }
    }

    private MockFor getMockDAO() {
        new MockFor(StudentsDAO)
    }

    private MockFor getMockHttpDAO() {
        new MockFor(HttpStudentsDAO)
    }
}

