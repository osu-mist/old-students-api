package edu.oregonstate.mist.students

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.students.core.AcademicStatus
import edu.oregonstate.mist.students.core.AccountBalance
import edu.oregonstate.mist.students.core.AccountTransactions
import edu.oregonstate.mist.students.core.GPALevels
import edu.oregonstate.mist.students.core.Grade
import edu.oregonstate.mist.students.db.InvalidTermException
import edu.oregonstate.mist.students.db.StudentNotFoundException
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
                            id: getResourceObjectID(osuID, it.term),
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
        AccountBalance accountBalance

        try {
            accountBalance = studentsDAOWrapper.getAccountBalance(osuID)
        } catch (StudentNotFoundException e) {
            return notFound().build()
        }

        ResultObject resultObject = new ResultObject(
                links: getSelfLink(uriBuilder.accountBalanceUri(osuID)),
                data: new ResourceObject(
                        id: osuID,
                        type: "account-balance",
                        attributes: accountBalance
                )
        )

        ok(resultObject).build()
    }

    @Timed
    @GET
    @Path ('{osuID: [0-9a-zA-Z-]+}/account-transactions')
    Response getAccountTransactions(@PathParam("osuID") String osuID) {
        AccountTransactions accountTransactions

        try {
            accountTransactions = studentsDAOWrapper.getAccountTransactions(osuID)
        } catch (StudentNotFoundException e) {
            return notFound().build()
        }

        ResultObject resultObject = new ResultObject(
                links: getSelfLink(uriBuilder.accountTransactionsUri(osuID)),
                data: new ResourceObject(
                        id: osuID,
                        type: "account-transactions",
                        attributes: accountTransactions
                )
        )

        ok(resultObject).build()
    }

    @Timed
    @GET
    @Path ('{osuID: [0-9a-zA-Z-]+}/gpa')
    Response getGPA(@PathParam("osuID") String osuID) {
        GPALevels gpa

        try {
            gpa = studentsDAOWrapper.getGPA(osuID)
        } catch (StudentNotFoundException e) {
            return notFound().build()
        }

        ResultObject resultObject = new ResultObject(
                links: getSelfLink(uriBuilder.gpaUri(osuID)),
                data: new ResourceObject(
                        id: osuID,
                        type: "gpa",
                        attributes: gpa
                )
        )

        ok(resultObject).build()
    }

    @Timed
    @GET
    @Path ('{osuID: [0-9a-zA-Z-]+}/academic-status')
    Response getAcademicStatus(@PathParam("osuID") String osuID, @QueryParam("term") String term) {
        List<AcademicStatus> academicStatus

        try {
            academicStatus = studentsDAOWrapper.getAcademicStatus(osuID, term)
        } catch (StudentNotFoundException e) {
            return notFound().build()
        } catch (InvalidTermException e) {
            return invalidTermResponse()
        }

        ResultObject resultObject = new ResultObject(
                links: getSelfLink(uriBuilder.academicStatusUri(osuID, term)),
                data: academicStatus.collect {
                    new ResourceObject(
                            id: getResourceObjectID(osuID, it.term),
                            type: "academic-status",
                            attributes: it
                    )
                }
        )

        ok(resultObject).build()
    }

    @Timed
    @GET
    @Path ('{osuID: [0-9a-zA-Z-]+}/grades')
    Response getGrades(@PathParam("osuID") String osuID, @QueryParam("term") String term) {
        List<Grade> grades

        try {
            grades = studentsDAOWrapper.getGrades(osuID, term)
        } catch (StudentNotFoundException e) {
            return notFound().build()
        } catch (InvalidTermException e) {
            return invalidTermResponse()
        }

        ResultObject resultObject = new ResultObject(
                links: getSelfLink(uriBuilder.gradesUri(osuID, term)),
                data: grades.collect {
                    new ResourceObject(
                            id: getResourceObjectID(osuID, it.term, it.courseReferenceNumber),
                            type: "grades",
                            attributes: it
                    )
                }
        )

        ok(resultObject).build()
    }

    private def getSelfLink(URI uri) {
        ["self": uri]
    }

    private String getResourceObjectID(String id) {
        id
    }

    private String getResourceObjectID(String id, String term) {
        "$id-$term}"
    }

    private String getResourceObjectID(String id, String term, String courseReferenceNumber) {
        "$id-$term-$courseReferenceNumber"
    }

    private Response invalidTermResponse() {
        badRequest("Term is invalid.").build()
    }
}
