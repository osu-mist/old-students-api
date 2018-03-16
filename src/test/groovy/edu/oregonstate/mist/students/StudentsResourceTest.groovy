package edu.oregonstate.mist.students

import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.students.core.AcademicStatusObject
import edu.oregonstate.mist.students.core.Award
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
     * Check that not including the term query parameter returns a 400 with an expected message.
     */
    @Test
    void nullTermShouldReturnBadRequest() {
        StudentsResource studentsResource = new StudentsResource(null, endpointUri)
        checkErrorResponse(studentsResource.getAcademicStatus("1234", null),
                400, "term (query parameter) is required.")
    }

    /**
     * Check that the resource returns a 404 if MockDAOWrapper.getPersonID() returns null.
     */
    @Test
    void badOsuIDShouldReturnNotFound() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)
        mockDAOWrapper.demand.getPersonID(2..2) { null }
        def studentsResource = new StudentsResource(mockDAOWrapper.proxyInstance(), endpointUri)
        checkErrorResponse(studentsResource.getAcademicStatus("1234", "201801"), 404, null)
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
        assertEquals(response.getEntity().class, Error.class)

        if (expectedDeveloperMessage) {
            assertEquals(response.getEntity()["developerMessage"], expectedDeveloperMessage)
        }
    }

    /**
     * Check that a good request returns a good response.
     */
    @Test
    void testValidAcademicStatusResponse() {
        def mockDAOWrapper = getMockDAOWrapper()

        AcademicStatusObject testAcademicStatus = new AcademicStatusObject(
                osuHours: 12,
                dualEnrollmentHours: 3,
                academicStanding: "Good",
                registrationBlocked: false,
                academicProbation: false
        )

        mockDAOWrapper.demand.getAcademicStatus() { String id, String term -> testAcademicStatus }

        def studentsResource = new StudentsResource(mockDAOWrapper.proxyInstance(), endpointUri)
        Response response = studentsResource.getAcademicStatus("912345678", "201801")

        responseChecker(response, testAcademicStatus)
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
        assertEquals(response.getEntity()["data"]["attributes"], expectedData)
    }

    private MockFor getMockDAOWrapper() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)
        mockDAOWrapper.demand.getPersonID() { "123456" }

        mockDAOWrapper
    }
}
