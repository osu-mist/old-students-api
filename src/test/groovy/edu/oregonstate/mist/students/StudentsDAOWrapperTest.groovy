package edu.oregonstate.mist.students

import edu.oregonstate.mist.students.core.AcademicStatusObject
import edu.oregonstate.mist.students.core.Award
import edu.oregonstate.mist.students.core.WorkStudyObject
import edu.oregonstate.mist.students.db.StudentsDAO
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import groovy.mock.interceptor.MockFor
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull

class StudentsDAOWrapperTest {
    /**
     * Check that the DAO wrapper returns null if the DAO returns null.
     */
    @Test
    void personIDShouldBeNullIfDAOReturnsNull() {
        def mockDAO = getMockDAO()
        mockDAO.demand.getPersonID() { null }

        def mockDAOWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance())

        assertNull(mockDAOWrapper.getPersonID("912345678"))
    }

    /**
     * Test the merging logic of the StudentsDAOWrapper.getAcademicStatus() method.
     */
    @Test
    void testFullAcademicStatusObject() {
        Integer osuCredits = 12
        Integer dualEnrollmentCredits = 4
        AcademicStatusObject partialAcademicStatus = new AcademicStatusObject(
                academicStanding: "Suspended",
                registrationBlocked: true,
                academicProbation: true
        )

        def mockDAO = getMockDAO()
        mockDAO.demand.getAcademicStanding() { String id, String term -> partialAcademicStatus }
        mockDAO.demand.getOSUCreditHours() { String id, String term ->
            osuCredits }
        mockDAO.demand.getDualEnrollmentCreditHours() { String id, String term ->
            dualEnrollmentCredits }

        AcademicStatusObject expectedAcademicStatus = new AcademicStatusObject(
                osuHours: osuCredits,
                dualEnrollmentHours: 4,
                academicStanding: partialAcademicStatus.academicStanding,
                registrationBlocked: partialAcademicStatus.registrationBlocked,
                academicProbation: partialAcademicStatus.academicProbation
        )

        def mockDAOWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance())

        assertEquals(expectedAcademicStatus,
                mockDAOWrapper.getAcademicStatus("912345678", "201702"))
    }

    /**
     * Check that if there is no academic standing data, an AcademicStatusObject should be returned.
     */
    @Test
    void nullAcademicStandingShouldStillReturnSomething() {
        def mockDAO = getMockDAO()

        mockDAO.demand.getAcademicStanding() { String id, String term -> null }
        mockDAO.demand.getOSUCreditHours() { String id, String term -> 3 }
        mockDAO.demand.getDualEnrollmentCreditHours() { String id, String term -> 19 }

        def mockDAOWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance())

        AcademicStatusObject academicStatus = mockDAOWrapper.getAcademicStatus(
                "987654321", "201600")

        assertNotNull(academicStatus.osuHours)
        assertNotNull(academicStatus.dualEnrollmentHours)
        assertNull(academicStatus.academicStanding)
        assertNull(academicStatus.registrationBlocked)
        assertNull(academicStatus.academicProbation)
    }

    /**
     * Check that a correct workstudy object is returned from the DAO wrapper
     */
    @Test
    void getWorkStudyTest() {
        List<Award> awards = [new Award(
                effectiveStartDate: new Date(),
                effectiveEndDate: new Date(),
                offerAmount: 2000,
                offerExpirationDate: new Date(),
                acceptedAmount: 1500,
                acceptedDate: new Date(),
                paidAmount: 1000,
                awardStatus: "Accepted"
        )]

        def mockDAO = getMockDAO()
        mockDAO.demand.getWorkStudy() { awards }
        def mockDAOWrapper = new StudentsDAOWrapper(mockDAO.proxyInstance())
        WorkStudyObject workStudy = mockDAOWrapper.getWorkStudy("987654321")

        assertEquals(workStudy.awards, awards)
    }

    private MockFor getMockDAO() {
        new MockFor(StudentsDAO)
    }
}

