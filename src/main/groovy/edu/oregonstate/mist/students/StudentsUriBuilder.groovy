package edu.oregonstate.mist.students

import javax.ws.rs.core.UriBuilder

class StudentsUriBuilder {
    URI endpointUri

    StudentsUriBuilder(URI endpointUri) {
        this.endpointUri = endpointUri
    }

    URI dualEnrollmentUri(String osuID, String term) {
        UriBuilder builder = UriBuilder.fromUri(this.endpointUri)
                .path("students/{osuID}/dual-enrollment")

        if (term) {
            builder.queryParam("term", term)
        }

        builder.build(osuID)
    }

    URI workStudyUri(String osuID) {
        UriBuilder.fromUri(this.endpointUri)
                .path("students/{osuID}/work-study")
                .build(osuID)
    }

    URI accountBalanceUri(String osuID) {
        UriBuilder.fromUri(this.endpointUri)
                .path("students/{osuID}/account-balance")
                .build(osuID)
    }
}
