package edu.oregonstate.mist.students

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

class StudentsUriBuilderTest {
    StudentsUriBuilder studentsUriBuilder
    URI endpointUri = new URI("https://foo.oregonstate.edu/v1")

    final String osuID = "912345678"
    final String endpoint = "gpa"

    @Before
    void setup() {
        studentsUriBuilder = new StudentsUriBuilder(endpointUri)
    }

    /**
     * Check the expected URI structure with a term is returned.
     */
    @Test
    void testGenericUriBuilderWithTerm() {
        String term = "201801"
        String expectedUriWithTerm =
                "${endpointUri.toString()}/students/${osuID}/${endpoint}?term=${term}"

        URI builderUriWithTerm = studentsUriBuilder.genericUri(endpoint, osuID, term)

        assertEquals(expectedUriWithTerm, builderUriWithTerm.toString())
    }

    /**
     * Check the expected URI structure without a term is returned.
     */
    @Test
    void testGenericUriBuilderWithoutTerm() {
        String expectedUriWithoutTerm =
                "${endpointUri.toString()}/students/${osuID}/$endpoint"

        URI builderUriWithoutTerm = studentsUriBuilder.genericUri(endpoint, osuID)

        assertEquals(expectedUriWithoutTerm, builderUriWithoutTerm.toString())
    }
}
