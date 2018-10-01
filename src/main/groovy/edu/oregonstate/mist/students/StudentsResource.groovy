package edu.oregonstate.mist.students

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("students")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class StudentsResource extends Resource {
    private final StudentsDAOWrapper studentsDAOWrapper
    private StudentsUriBuilder uriBuilder

    StudentsResource(StudentsDAOWrapper studentsDAOWrapper, URI endpointUri) {
        this.studentsDAOWrapper = studentsDAOWrapper
        this.endpointUri = endpointUri
        this.uriBuilder = new StudentsUriBuilder(endpointUri)
    }

    @Timed
    @GET
    Response getHealthCheck() {
        ok(studentsDAOWrapper.healthcheck()).build()
    }

    /**
     * Get academic status for a student, including enrolled credits and academic standing.
     * @param osuID
     * @param term
     * @return
     */
    @Timed
    @GET
    @Path ('{osuID: [0-9a-zA-Z-]+}/dual-enrollment')
    Response getDualEnrollment(@PathParam("osuID") String osuID, @QueryParam("term") String term) {
        String personID = studentsDAOWrapper.getPersonID(osuID)

        if (!personID) {
            return notFound().build()
        }

        ResultObject resultObject = new ResultObject(
                links: getSelfLink(uriBuilder.dualEnrollmentUri(osuID, term)),
                data: studentsDAOWrapper.getDualEnrollment(personID, term).collect {
                    new ResourceObject(
                            id: getStudentAndTermID(osuID, it.term),
                            type: "dual-enrollment",
                            attributes: it,
                    )
                }
        )

        ok(resultObject).build()
    }

    /**
     * Get work study financial aid award information for a student.
     * @param osuID
     * @return
     */
    @Timed
    @GET
    @Path ('{osuID: [0-9a-zA-Z-]+}/work-study')
    Response getWorkStudy(@PathParam("osuID") String osuID) {
        String personID = studentsDAOWrapper.getPersonID(osuID)

        if (!personID) {
            return notFound().build()
        }

        ResultObject resultObject = new ResultObject(
                links: getSelfLink(uriBuilder.workStudyUri(osuID)),
                data: new ResourceObject(
                        id: osuID,
                        type: "work-study",
                        attributes: studentsDAOWrapper.getWorkStudy(personID)
                )
        )

        ok(resultObject).build()
    }

    @Timed
    @GET
    @Path ('{osuID: [0-9a-zA-Z-]+}/account-balance')
    Response getAccountBalance(@PathParam("osuID") String osuID) {
        ResultObject resultObject = new ResultObject(
                links: getSelfLink(uriBuilder.accountBalanceUri(osuID)),
                data: new ResourceObject(
                        id: osuID,
                        type: "account-balance",
                        attributes: studentsDAOWrapper.getAccountBalance(osuID)
                )
        )

        ok(resultObject).build()
    }

    private def getSelfLink(URI uri) {
        ["self": uri]
    }

    private String getStudentAndTermID(String id, String term) {
        "${id}-${term}"
    }
}
