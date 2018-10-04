package edu.oregonstate.mist.students

import edu.oregonstate.mist.api.ErrorResultObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.students.core.DualEnrollment
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
                    TestHelperObjects.fakeID, "201801"), 400, null)
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
            responseChecker(response, testDualEnrollment)
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
            responseChecker(response, workStudyObject)
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
            responseChecker(response, TestHelperObjects.fakeAccountBalance)
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
            responseChecker(response, TestHelperObjects.fakeAccountTransactions)
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
            responseChecker(response, TestHelperObjects.fakeGPALevels)
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
                    TestHelperObjects.fakeID, "201801"), 400, "Term is invalid.")
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
            responseChecker(response, TestHelperObjects.fakeAcademicStatus[0])
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
    private void responseChecker(Response response, def expectedData) {
        assertNotNull(response)
        assertEquals(response.status, 200)
        assertEquals(response.getEntity().class, ResultObject.class)

        def responseData = response.getEntity()["data"]

        if (responseData instanceof List) {
            assertEquals(responseData[0]["attributes"], expectedData)
        } else {
            assertEquals(responseData["attributes"], expectedData)
        }
    }

    private MockFor getMockDAOWrapper() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)
    }

    private StudentsResource getStudentsResource() {
        new StudentsResource(new StudentsDAOWrapper(null, null), endpointUri)
    }
}
