package edu.oregonstate.mist.students

import edu.oregonstate.mist.api.ErrorResultObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.students.core.ClassSchedule
import edu.oregonstate.mist.students.core.Classification
import edu.oregonstate.mist.students.core.DualEnrollment
import edu.oregonstate.mist.students.core.Grade
import edu.oregonstate.mist.students.core.WorkStudyObject
import edu.oregonstate.mist.students.db.InvalidTermException
import edu.oregonstate.mist.students.db.StudentNotFoundException
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import groovy.mock.interceptor.MockFor
import org.junit.Test

import javax.ws.rs.core.Response

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNotNull

class StudentsResourceTest {
    private final URI endpointUri = new URI("https://www.foo.com/")
    private final String invalidTermErrorMessage = "Term is invalid."

    @Test
    void badOsuIDShouldReturnNotFoundClassification() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getClassification() {
            String osuID -> throw new StudentNotFoundException()
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getClassification(
                TestHelperObjects.fakeID), 404, null)
        }
    }

    @Test
    void testValidClassification() {
        def mockDAOWrapper = getMockDAOWrapper()

        Classification testClassification = new Classification(
            level: "Undergraduate",
            classification: "Sophomore"
        )

        mockDAOWrapper.demand.getClassification() { String id -> testClassification }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            Response response = studentsResource.getClassification(TestHelperObjects.fakeID)
            responseChecker(
                response, testClassification, "${TestHelperObjects.fakeID}", "classification"
            )
        }
    }

    @Test
    void badOsuIDShouldReturnNotFoundDualEnrollment() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getDualEnrollment() { String osuID, String term ->
            throw new StudentNotFoundException()
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getDualEnrollment(
                    TestHelperObjects.fakeID, "201801"), 404, null)
        }
    }

    @Test
    void badTermShouldReturnBadRequestDualEnrollment() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getDualEnrollment() { String osuID, String term ->
            throw new InvalidTermException()
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getDualEnrollment(
                    TestHelperObjects.fakeID, "201801"), 400, invalidTermErrorMessage)
        }
    }

    @Test
    void testValidDualEnrollmentResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        String testTerm = "201801"

        DualEnrollment testDualEnrollment = new DualEnrollment(
                term: testTerm,
                creditHours: 5
        )

        mockDAOWrapper.demand.getDualEnrollment() { String id, String term -> [testDualEnrollment] }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            Response response = studentsResource.getDualEnrollment(
                    TestHelperObjects.fakeID, testTerm)
            responseChecker(response, testDualEnrollment,
                    "${TestHelperObjects.fakeID}-$testTerm", "dual-enrollment")
        }
    }

    @Test
    void badOsuIDShouldReturnNotFoundWorkStudy() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)

        mockDAOWrapper.demand.getWorkStudy() { throw new StudentNotFoundException() }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getWorkStudy(TestHelperObjects.fakeID), 404, null)
        }
    }

    @Test
    void testValidWorkStudyResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        WorkStudyObject workStudyObject = new WorkStudyObject(awards: TestHelperObjects.fakeAwards)

        mockDAOWrapper.demand.getWorkStudy() { workStudyObject }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            Response response = studentsResource.getWorkStudy(TestHelperObjects.fakeID)
            responseChecker(response, workStudyObject, TestHelperObjects.fakeID, "work-study")
        }
    }

    @Test
    void badOsuIDShouldReturnNotFoundAccountBalance() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)

        mockDAOWrapper.demand.getAccountBalance() { throw new StudentNotFoundException() }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getAccountBalance(
                    TestHelperObjects.fakeID), 404, null)
        }
    }

    @Test
    void testValidAccountBalanceResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getAccountBalance() { TestHelperObjects.fakeAccountBalance }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            Response response = studentsResource.getAccountBalance(TestHelperObjects.fakeID)
            responseChecker(response, TestHelperObjects.fakeAccountBalance,
                    TestHelperObjects.fakeID, "account-balance")
        }
    }

    @Test
    void badOsuIDShouldReturnNotFoundAccountTransactions() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)

        mockDAOWrapper.demand.getAccountTransactions() { throw new StudentNotFoundException() }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getAccountTransactions(
                    TestHelperObjects.fakeID), 404, null)
        }
    }

    @Test
    void testValidAccountTransactionsResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getAccountTransactions() { TestHelperObjects.fakeAccountTransactions }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            Response response = studentsResource.getAccountTransactions(TestHelperObjects.fakeID)
            responseChecker(response, TestHelperObjects.fakeAccountTransactions,
                    TestHelperObjects.fakeID, "account-transactions")
        }
    }

    @Test
    void badOsuIDShouldReturnNotFoundGPA() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)

        mockDAOWrapper.demand.getGPA() { throw new StudentNotFoundException() }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getGPA(
                    TestHelperObjects.fakeID), 404, null)
        }
    }

    @Test
    void testGPAResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getGPA() { TestHelperObjects.fakeGPALevels }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            Response response = studentsResource.getGPA(TestHelperObjects.fakeID)
            responseChecker(response, TestHelperObjects.fakeGPALevels,
                    TestHelperObjects.fakeID, "gpa")
        }
    }

    @Test
    void badOsuIDShouldReturnNotFoundAcademicStatus() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getAcademicStatus() { String osuID, String term ->
            throw new StudentNotFoundException()
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getAcademicStatus(
                    TestHelperObjects.fakeID, "201801"), 404, null)
        }
    }

    @Test
    void badTermShouldReturnBadRequestAcademicStatus() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getAcademicStatus() { String osuID, String term ->
            throw new InvalidTermException()
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getAcademicStatus(
                    TestHelperObjects.fakeID, "201801"), 400, invalidTermErrorMessage)
        }
    }

    @Test
    void testValidAcademicStatusResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        String testTerm = "201801"

        mockDAOWrapper.demand.getAcademicStatus() { String id, String term ->
            TestHelperObjects.fakeAcademicStatus
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            Response response = studentsResource.getAcademicStatus(
                    TestHelperObjects.fakeID, testTerm)
            responseChecker(response, TestHelperObjects.fakeAcademicStatus[0],
                    "${TestHelperObjects.fakeID}-$testTerm", "academic-status")
        }
    }

    @Test
    void badOsuIDShouldReturnNotFoundGrades() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getGrades() { String osuID, String term ->
            throw new StudentNotFoundException()
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getGrades(
                    TestHelperObjects.fakeID, "201801"), 404, null)
        }
    }

    @Test
    void badTermShouldReturnBadRequestGrades() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getGrades() { String osuID, String term ->
            throw new InvalidTermException()
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getGrades(
                    TestHelperObjects.fakeID, "201801"), 400, invalidTermErrorMessage)
        }
    }

    @Test
    void testValidGradesResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        String testTerm = "201801"

        mockDAOWrapper.demand.getGrades() { String id, String term ->
            TestHelperObjects.fakeGrades
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            Response response = studentsResource.getGrades(
                    TestHelperObjects.fakeID, testTerm)

            Grade grade = TestHelperObjects.fakeGrades[0]
            String expectedID = "${TestHelperObjects.fakeID}-${grade.term}" +
                    "-${grade.courseReferenceNumber}"

            responseChecker(response, grade, expectedID, "grades")
        }
    }

    @Test
    void badOsuIDShouldReturnNotFoundClassSchedule() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getClassSchedule() { String osuID, String term ->
            throw new StudentNotFoundException()
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getClassSchedule(
                    TestHelperObjects.fakeID, "201801"), 404, null)
        }
    }

    @Test
    void badTermShouldReturnBadRequestClassSchedule() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getClassSchedule() { String osuID, String term ->
            throw new InvalidTermException()
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getClassSchedule(
                    TestHelperObjects.fakeID, "201801"), 400, invalidTermErrorMessage)
        }
    }

    @Test
    void noTermShouldReturnBadRequestClassSchedule() {
        StudentsResource studentsResource = getStudentsResource()
        checkErrorResponse(studentsResource.getClassSchedule(
                TestHelperObjects.fakeID, null), 400, "Term (query parameter) is required.")
    }

    @Test
    void testValidClassScheduleResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        String testTerm = "201801"

        mockDAOWrapper.demand.getClassSchedule() { String id, String term ->
            TestHelperObjects.fakeSchedule
        }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            Response response = studentsResource.getClassSchedule(
                    TestHelperObjects.fakeID, testTerm)

            ClassSchedule schedule = TestHelperObjects.fakeSchedule[0]
            String expectedID = "${TestHelperObjects.fakeID}-${schedule.term}" +
                    "-${schedule.courseReferenceNumber}"

            responseChecker(response, schedule, expectedID, "class-schedule")
        }
    }

    @Test
    void badOsuIDShouldReturnNotFoundHolds() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)

        mockDAOWrapper.demand.getHolds() { throw new StudentNotFoundException() }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            checkErrorResponse(studentsResource.getHolds(
                    TestHelperObjects.fakeID), 404, null)
        }
    }

    @Test
    void testValidHoldsResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        mockDAOWrapper.demand.getHolds() { TestHelperObjects.fakeHolds }

        mockDAOWrapper.use {
            StudentsResource studentsResource = getStudentsResource()
            Response response = studentsResource.getHolds(TestHelperObjects.fakeID)
            responseChecker(response, TestHelperObjects.fakeHolds,
                    TestHelperObjects.fakeID, "holds")
        }
    }

    /**
     * Helper method to check an error response.
     * @param response
     * @param expectedResponseCode
     * @param expectedDeveloperMessage
     */
    private void checkErrorResponse(Response response,
                                    Integer expectedResponseCode,
                                    String expectedErrorMessage) {
        assertNotNull(response)
        assertEquals(response.status, expectedResponseCode)
        assertEquals(response.getEntity().class, ErrorResultObject.class)

        if (expectedErrorMessage) {
            List<String> errorMessages = response.getEntity()["errors"].collect { it["detail"] }
            assertTrue(errorMessages.contains(expectedErrorMessage))
        }
    }

    /**
     * Helper method to test a response object
     * @param response
     * @param expectedData
     */
    private void responseChecker(Response response,
                                 def expectedData,
                                 String expectedID,
                                 String expectedType) {
        assertNotNull(response)
        assertEquals(response.status, 200)
        assertEquals(response.getEntity().class, ResultObject.class)

        def responseData = response.getEntity()["data"]

        def resourceObject

        if (responseData instanceof List) {
            resourceObject = responseData[0]
        } else {
            resourceObject = responseData
        }

        assertEquals(resourceObject["attributes"], expectedData)
        assertEquals(resourceObject["id"], expectedID)
        assertEquals(resourceObject["type"], expectedType)
    }

    private MockFor getMockDAOWrapper() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)
    }

    private StudentsResource getStudentsResource() {
        new StudentsResource(new StudentsDAOWrapper(null, null), endpointUri)
    }
}
