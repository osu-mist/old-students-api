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

    private URI genericUri(String endpoint, String osuID, String term = null) {
        UriBuilder builder = UriBuilder.fromUri(this.endpointUri).path("students/{osuID}/$endpoint")

        if (term) {
            builder.queryParam("term", term)
        }

        builder.build(osuID)
    }
}
