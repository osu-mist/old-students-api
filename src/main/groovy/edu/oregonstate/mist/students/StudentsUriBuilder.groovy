package edu.oregonstate.mist.students

import javax.ws.rs.core.UriBuilder

class StudentsUriBuilder {
    URI endpointUri

    StudentsUriBuilder(URI endpointUri) {
        this.endpointUri = endpointUri
    }

    URI genericUri(String endpoint, String osuID, String term = null) {
        UriBuilder builder = UriBuilder.fromUri(this.endpointUri).path("students/{osuID}/$endpoint")

        if (term) {
            builder.queryParam("term", term)
        }

        builder.build(osuID)
    }
}
