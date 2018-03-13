package edu.oregonstate.mist.students

import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.students.core.AcademicStatusObject
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
        mockDAOWrapper.demand.getPersonID() { null }
        def studentsResource = new StudentsResource(mockDAOWrapper.proxyInstance(), endpointUri)
        checkErrorResponse(studentsResource.getAcademicStatus("1234", "201801"), 404, null)
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
    void testValidResponse() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)
        mockDAOWrapper.demand.getPersonID() { "123456" }

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

        assertNotNull(response)
        assertEquals(response.status, 200)
        assertEquals(response.getEntity().class, ResultObject.class)
        assertEquals(response.getEntity()["data"]["attributes"], testAcademicStatus)
    }
}
