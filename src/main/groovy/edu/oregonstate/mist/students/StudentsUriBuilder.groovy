package edu.oregonstate.mist.students

import javax.ws.rs.core.UriBuilder

class StudentsUriBuilder {
    URI endpointUri

    StudentsUriBuilder(URI endpointUri) {
        this.endpointUri = endpointUri
    }

    URI dualEnrollmentUri(String osuID, String term) {
        genericUri("dual-enrollment", osuID, term)
    }

    URI workStudyUri(String osuID) {
        genericUri("work-study", osuID)
    }

    URI accountBalanceUri(String osuID) {
        genericUri("account-balance", osuID)
    }

    URI accountTransactionsUri(String osuID) {
        genericUri("account-transactions", osuID)
    }

    URI gpaUri(String osuID) {
        genericUri("gpa", osuID)
    }

    URI academicStatusUri(String osuID, String term) {
        genericUri("academic-status", osuID, term)
    }

    URI gradesUri(String osuID, String term) {
        genericUri("grades", osuID, term)
    }

    URI classScheduleUri(String osuID, String term) {
        genericUri("class-schedule", osuID, term)
    }

    URI holdsUri(String osuID) {
        genericUri("holds", osuID)
    }

    private URI genericUri(String endpoint, String osuID, String term = null) {
        UriBuilder builder = UriBuilder.fromUri(this.endpointUri).path("students/{osuID}/$endpoint")

        if (term) {
            builder.queryParam("term", term)
        }

        builder.build(osuID)
    }
}
