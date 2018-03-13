package edu.oregonstate.mist.students

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

class StudentsUriBuilderTest {
    StudentsUriBuilder studentsUriBuilder
    URI endpointUri = new URI("https://foo.oregonstate.edu/v1")

    final String osuID = "912345678"

    @Before
    void setup() {
        studentsUriBuilder = new StudentsUriBuilder(endpointUri)
    }

    /**
     * Check the expected URI structure is returned, given the method arguments.
     */
    @Test
    void testAcademicStatusUriBuilder() {
        String term = "201801"
        String expectedUri =
                "${endpointUri.toString()}/students/${osuID}/academicstatus?term=${term}"

        URI builderUri = studentsUriBuilder.academicStatusUri(osuID, term)

        assertEquals(expectedUri, builderUri.toString())
    }

    /**
     * Check the expected URI is returned for a workstudy URI.
     */
    @Test
    void testWorkStudyUriBuilder() {
        String expectedUri = "${endpointUri.toString()}/students/${osuID}/workstudy"

        URI builderUri = studentsUriBuilder.workStudyUri(osuID)

        assertEquals(expectedUri, builderUri.toString())
    }
}
