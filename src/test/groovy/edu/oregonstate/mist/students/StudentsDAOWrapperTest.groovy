package edu.oregonstate.mist.students

import edu.oregonstate.mist.students.core.Award
import edu.oregonstate.mist.students.core.WorkStudyObject
import edu.oregonstate.mist.students.db.StudentsDAO
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import groovy.mock.interceptor.MockFor
import org.junit.Test

import static org.junit.Assert.assertEquals
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

