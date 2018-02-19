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
    @Path ('{id: \\d+}/academicstatus')
    Response getAcademicStatus(@PathParam("id") String osuID, @QueryParam("term") String term) {
        if (!term?.trim()) {
            return badRequest("term (query parameter) is required.").build()
        }

        String personID = studentsDAOWrapper.getPersonID(osuID)

        if (!personID) {
            return notFound().build()
        }

        ResultObject resultObject = new ResultObject(data: new ResourceObject(
                id: term,
                type: "academicstatus",
                attributes: studentsDAOWrapper.getAcademicStatus(personID, term),
                links: ["self": uriBuilder.academicStatusUri(osuID, term)])
        )

        ok(resultObject).build()
    }
}
