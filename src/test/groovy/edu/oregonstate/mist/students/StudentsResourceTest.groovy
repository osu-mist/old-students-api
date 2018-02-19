package edu.oregonstate.mist.students

import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import groovy.mock.interceptor.MockFor
import org.junit.Test

import javax.ws.rs.core.Response

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class StudentsResourceTest {
    private final URI endpointUri = new URI("https://www.foo.com/")

    @Test
    void nullTermShouldReturnBadRequest() {
        StudentsResource studentsResource = new StudentsResource(null, endpointUri)
        checkErrorResponse(studentsResource.getAcademicStatus("1234", null),
                400, "term (query parameter) is required.")
    }

    @Test
    void badOsuIDShouldReturnNotFound() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)
        mockDAOWrapper.demand.getPersonID() { null }
        def studentsResource = new StudentsResource(mockDAOWrapper.proxyInstance(), endpointUri)
        checkErrorResponse(studentsResource.getAcademicStatus("1234", "201801"), 404, null)
    }

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
