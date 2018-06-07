package edu.oregonstate.mist.students

import javax.ws.rs.core.UriBuilder

class StudentsUriBuilder {
    URI endpointUri

    StudentsUriBuilder(URI endpointUri) {
        this.endpointUri = endpointUri
    }

    URI academicStatusUri(String osuID, String term) {
        UriBuilder.fromUri(this.endpointUri)
                .path("students/{osuID}/academicstatus")
                .queryParam("term", "{term}")
                .build(osuID, term)
    }

    URI workStudyUri(String osuID) {
        UriBuilder.fromUri(this.endpointUri)
                .path("students/{osuID}/workstudy")
                .build(osuID)
    }

    URI scheduleUri(String osuID, String term) {
        UriBuilder.fromUri(this.endpointUri)
                .path("students/{osuID}/schedule")
                .queryParam("term", term)
                .build(osuID)
    }
}
