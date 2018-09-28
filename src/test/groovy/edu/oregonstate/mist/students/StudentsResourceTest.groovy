package edu.oregonstate.mist.students

import edu.oregonstate.mist.api.ErrorResultObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.students.core.Award
import edu.oregonstate.mist.students.core.DualEnrollment
import edu.oregonstate.mist.students.core.WorkStudyObject
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import groovy.mock.interceptor.MockFor
import org.junit.Test

import javax.ws.rs.core.Response

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class StudentsResourceTest {
    private final URI endpointUri = new URI("https://www.foo.com/")

    /**
     * Check that the resource returns a 404 if MockDAOWrapper.getPersonID() returns null.
     */
    @Test
    void badOsuIDShouldReturnNotFound() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)
        mockDAOWrapper.demand.getPersonID(2..2) { null }
        def studentsResource = new StudentsResource(mockDAOWrapper.proxyInstance(), endpointUri)
        checkErrorResponse(studentsResource.getDualEnrollment("1234", "201801"), 404, null)
        checkErrorResponse(studentsResource.getWorkStudy("1234"), 404, null)
    }

    /**
     * Helper method to check an error response.
     * @param response
     * @param expectedResponseCode
     * @param expectedDeveloperMessage
     */
    private void checkErrorResponse(Response response,
                                    Integer expectedResponseCode,
                                    String expectedDeveloperMessage) {
        assertNotNull(response)
        assertEquals(response.status, expectedResponseCode)
        assertEquals(response.getEntity().class, ErrorResultObject.class)

        if (expectedDeveloperMessage) {
            assertEquals(response.getEntity()["developerMessage"], expectedDeveloperMessage)
        }
    }

    /**
     * Check that a good request returns a good response.
     */
    @Test
    void testValidDualEnrollmentResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        String testTerm = "201801"

        DualEnrollment testDualEnrollment = new DualEnrollment(
                term: testTerm,
                creditHours: 5
        )

        mockDAOWrapper.demand.getDualEnrollment() { String id, String term -> [testDualEnrollment] }

        def studentsResource = new StudentsResource(mockDAOWrapper.proxyInstance(), endpointUri)
        Response response = studentsResource.getDualEnrollment("912345678", testTerm)

        responseChecker(response, testDualEnrollment)
    }

    @Test
    void testValidWorkStudyResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        WorkStudyObject workStudyObject = new WorkStudyObject(awards: [new Award(
                effectiveStartDate: new Date(),
                effectiveEndDate: new Date(),
                offerAmount: 2000,
                offerExpirationDate: new Date(),
                acceptedAmount: 1500,
                acceptedDate: new Date(),
                paidAmount: 1000,
                awardStatus: "Accepted"
        )])

        mockDAOWrapper.demand.getWorkStudy() { workStudyObject }

        def studentsResource = new StudentsResource(mockDAOWrapper.proxyInstance(), endpointUri)
        Response response = studentsResource.getWorkStudy("912345642")

        responseChecker(response, workStudyObject)
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
        mockDAOWrapper.demand.getPersonID() { "123456" }

        mockDAOWrapper
    }
}
