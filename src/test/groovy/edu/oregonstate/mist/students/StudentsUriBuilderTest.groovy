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
        String expectedUriWithTerm =
                "${endpointUri.toString()}/students/${osuID}/dual-enrollment?term=${term}"

        URI builderUriWithTerm = studentsUriBuilder.dualEnrollmentUri(osuID, term)

        assertEquals(expectedUriWithTerm, builderUriWithTerm.toString())

        String expectedUriWithoutTerm =
                "${endpointUri.toString()}/students/${osuID}/dual-enrollment"

        URI builderUriWithoutTerm = studentsUriBuilder.dualEnrollmentUri(osuID, null)

        assertEquals(expectedUriWithoutTerm, builderUriWithoutTerm.toString())
    }

    /**
     * Check the expected URI is returned for a workstudy URI.
     */
    @Test
    void testWorkStudyUriBuilder() {
        String expectedUri = "${endpointUri.toString()}/students/${osuID}/work-study"

        URI builderUri = studentsUriBuilder.workStudyUri(osuID)

        assertEquals(expectedUri, builderUri.toString())
    }
}
