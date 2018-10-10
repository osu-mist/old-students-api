package edu.oregonstate.mist.students

import edu.oregonstate.mist.students.core.AcademicStatus
import edu.oregonstate.mist.students.core.AccountBalance
import edu.oregonstate.mist.students.core.AccountTransactions
import edu.oregonstate.mist.students.core.ClassSchedule
import edu.oregonstate.mist.students.core.DualEnrollment
import edu.oregonstate.mist.students.core.GPALevels
import edu.oregonstate.mist.students.core.Grade
import edu.oregonstate.mist.students.core.Hold
import edu.oregonstate.mist.students.core.Holds
import edu.oregonstate.mist.students.core.WorkStudyObject
import edu.oregonstate.mist.students.db.HttpStudentsDAO
import edu.oregonstate.mist.students.db.InvalidTermException
import edu.oregonstate.mist.students.db.StudentNotFoundException
import edu.oregonstate.mist.students.db.StudentsDAO
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import groovy.mock.interceptor.MockFor
import org.junit.Test

import java.time.LocalDate

import static org.junit.Assert.assertEquals

class StudentsDAOWrapperTest {
    private static final String endpoint = "https://www.example.com"

    /**
     * Check that the DAO wrapper returns null if the DAO returns null.
     */
    @Test(expected = StudentNotFoundException.class)
    void getWorkStudyThrowsExceptionIfStudentNotFound() {
        def mockDAO = getMockDAO()
        mockDAO.demand.getPersonID() { null }

        def daoWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance(), null)

        daoWrapper.getWorkStudy(TestHelperObjects.fakeID)
    }

    /**
     * Check that a correct workstudy object is returned from the DAO wrapper
     */
    @Test
    void getWorkStudyTest() {
        def mockDAO = getMockDAO()
        mockDAO.demand.getPersonID() { "foobar" }
        mockDAO.demand.getWorkStudy() { TestHelperObjects.fakeAwards }
        def mockDAOWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance(), null)
        WorkStudyObject workStudy = mockDAOWrapper.getWorkStudy(TestHelperObjects.fakeID)

        assertEquals(TestHelperObjects.fakeAwards, workStudy.awards)
    }

    @Test(expected = StudentNotFoundException.class)
    void getDualEnrollmentThrowsExceptionIfStudentNotFound() {
        def mockDAO = getMockDAO()
        mockDAO.demand.getPersonID() { null }

        def daoWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance(), null)

        daoWrapper.getWorkStudy(TestHelperObjects.fakeID)
    }

    @Test(expected = InvalidTermException.class)
    void getDualEnrollmentThrowsExceptionIfInvalidTerm() {
        def mockDAO = getMockDAO()
        mockDAO.demand.isValidTerm() { false }

        def daoWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance(), null)

        daoWrapper.getDualEnrollment(TestHelperObjects.fakeID, "badTerm")
    }

    @Test
    void getCurrentTermIsCalledIfTermEqualsCurrent() {
        def mockDAO = getMockDAO()

        String currentTerm = "201901"
        String termUsedInCall

        mockDAO.demand.getCurrentTerm() { currentTerm }
        mockDAO.demand.isValidTerm() { true }
        mockDAO.demand.getPersonID() { TestHelperObjects.fakeID }
        mockDAO.demand.getDualEnrollment() { String id, String term ->
            termUsedInCall = term
            [new DualEnrollment()]
        }

        def daoWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance(), null)

        daoWrapper.getDualEnrollment(TestHelperObjects.fakeID, "current")

        assertEquals(termUsedInCall, currentTerm)
    }

    @Test
    void testAccountBalance() {
        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getAccountBalance() { TestHelperObjects.fakeAccountBalance }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            AccountBalance accountBalanceFromDAOWrapper = daoWrapper.getAccountBalance(
                    TestHelperObjects.fakeID)
            assertEquals(TestHelperObjects.fakeAccountBalance, accountBalanceFromDAOWrapper)
        }
    }

    @Test
    void testAccountTransactions() {
        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getAccountTransactions() { TestHelperObjects.fakeAccountTransactions }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            AccountTransactions transactionsFromDAOWrapper =
                    daoWrapper.getAccountTransactions(TestHelperObjects.fakeID)
            assertEquals(TestHelperObjects.fakeAccountTransactions, transactionsFromDAOWrapper)
        }
    }

    @Test
    void testGPA() {
        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getGPA() { TestHelperObjects.fakeGPALevels }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            GPALevels gpaLevelsFromDAOWrapper = daoWrapper.getGPA(TestHelperObjects.fakeID)
            assertEquals(TestHelperObjects.fakeGPALevels, gpaLevelsFromDAOWrapper)
        }
    }

    @Test
    void testAcademicStatus() {
        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getAcademicStatus() { String osuID, String term ->
            TestHelperObjects.fakeAcademicStatus
        }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            List<AcademicStatus> academicStatusFromDAOWrapper = daoWrapper.getAcademicStatus(
                    TestHelperObjects.fakeID, "201801")
            assertEquals(TestHelperObjects.fakeAcademicStatus, academicStatusFromDAOWrapper)
        }
    }

    @Test
    void academicStatusUsesCurrentTerm() {
        String currentTerm = "201901"
        String termUsedInCall

        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getAcademicStatus() { String osuID, String term ->
            termUsedInCall = term
            TestHelperObjects.fakeAcademicStatus
        }

        def mockDAO = getMockDAO()

        mockDAO.demand.getCurrentTerm() { currentTerm }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO(mockDAO)
            List<AcademicStatus> academicStatusFromDAOWrapper = daoWrapper.getAcademicStatus(
                    TestHelperObjects.fakeID, "current")
            assertEquals(TestHelperObjects.fakeAcademicStatus, academicStatusFromDAOWrapper)
            assertEquals(currentTerm, termUsedInCall)
        }
    }

    @Test
    void testGrades() {
        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getGrades() {String osuID, String term ->
            TestHelperObjects.fakeGrades
        }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            List<Grade> gradesFromDAOWrapper = daoWrapper.getGrades(
                    TestHelperObjects.fakeID, "201901")
            assertEquals(TestHelperObjects.fakeGrades, gradesFromDAOWrapper)
        }
    }

    @Test
    void gradesUseCurrentTerm() {
        String currentTerm = "201901"
        String termUsedInCall

        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getGrades() { String osuID, String term ->
            termUsedInCall = term
            TestHelperObjects.fakeGrades
        }

        def mockDAO = getMockDAO()

        mockDAO.demand.getCurrentTerm() { currentTerm }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO(mockDAO)
            List<Grade> gradesFromDAOWrapper = daoWrapper.getGrades(
                    TestHelperObjects.fakeID, "current")
            assertEquals(TestHelperObjects.fakeGrades, gradesFromDAOWrapper)
            assertEquals(currentTerm, termUsedInCall)
        }
    }

    @Test
    void testClassSchedule() {
        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getClassSchedule() { String osuID, String term->
            TestHelperObjects.fakeSchedule
        }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            List<ClassSchedule> classScheduleFromDAOWrapper = daoWrapper.getClassSchedule(
                    TestHelperObjects.fakeID, "201801")
            assertEquals(TestHelperObjects.fakeSchedule, classScheduleFromDAOWrapper)
        }
    }

    @Test
    void classScheduleUsesCurrentTerm() {
        String currentTerm = "201901"
        String termUsedInCall

        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getClassSchedule() { String osuID, String term ->
            termUsedInCall = term
            TestHelperObjects.fakeSchedule
        }

        def mockDAO = getMockDAO()

        mockDAO.demand.getCurrentTerm() { currentTerm }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO(mockDAO)
            List<ClassSchedule> classScheduleFromDAOWrapper = daoWrapper.getClassSchedule(
                    TestHelperObjects.fakeID, "current")
            assertEquals(TestHelperObjects.fakeSchedule, classScheduleFromDAOWrapper)
            assertEquals(currentTerm, termUsedInCall)
        }
    }

    @Test
    void testHolds() {
        def mockHttpDAO = getMockHttpDAO()

        mockHttpDAO.demand.getHolds() { TestHelperObjects.fakeHolds }

        mockHttpDAO.use {
            def daoWrapper = getStudentsDAOWrapperWithHttpDAO()
            Holds holdsFromDAOWrapper = daoWrapper.getHolds(TestHelperObjects.fakeID)
            assertEquals(TestHelperObjects.fakeHolds, holdsFromDAOWrapper)
        }
    }

    private StudentsDAOWrapper getStudentsDAOWrapperWithHttpDAO(MockFor mockDAO = null) {
        HttpStudentsDAO httpStudentsDAO = new HttpStudentsDAO(null, endpoint)

        if (mockDAO) {
            new StudentsDAOWrapper(mockDAO.proxyInstance(), httpStudentsDAO)
        } else {
            new StudentsDAOWrapper(null, httpStudentsDAO)
        }
    }

    private MockFor getMockDAO() {
        new MockFor(StudentsDAO)
    }

    private MockFor getMockHttpDAO() {
        new MockFor(HttpStudentsDAO)
    }
}

