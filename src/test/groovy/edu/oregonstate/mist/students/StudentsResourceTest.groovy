package edu.oregonstate.mist.students

import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import groovy.mock.interceptor.MockFor
import org.junit.Before
import org.junit.Test

import javax.ws.rs.core.Response

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class StudentsResourceTest {
    @Test
    void nullTermShouldReturnBadRequest() {
        StudentsResource studentsResource = new StudentsResource(null, null)
        checkErrorResponse(studentsResource.getAcademicStatus("1234", null),
                400, "term (query parameter) is required.")
    }

    @Test
    void badOsuIDShouldReturnNotFound() {
        def mockDAOWrapper = new MockFor(StudentsDAOWrapper)
        mockDAOWrapper.demand.getPersonID() { null }
        def studentsResource = new StudentsResource(mockDAOWrapper.proxyInstance(), null)
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
}
