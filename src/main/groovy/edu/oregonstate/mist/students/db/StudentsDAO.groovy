package edu.oregonstate.mist.students.db

import edu.oregonstate.mist.contrib.AbstractStudentsDAO
import edu.oregonstate.mist.students.core.Award
import edu.oregonstate.mist.students.core.DualEnrollment

import edu.oregonstate.mist.students.mapper.AwardMapper
import edu.oregonstate.mist.students.mapper.DualEnrollmentMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.Mapper

public interface StudentsDAO extends Closeable {
    /**
     * Return credit hours enrolled at a non-OSU institution.
     * @param personID
     * @param term
     * @return
     */
    @Mapper(DualEnrollmentMapper)
    @SqlQuery(AbstractStudentsDAO.dualEnrollmentCreditsQuery)
    List<DualEnrollment> getDualEnrollment(@Bind("id") String personID,
                                           @Bind("term") String term)

    /**
     * Get a list of award objects for work study financial aid awards
     * @param personID
     * @return
     */
    @Mapper(AwardMapper)
    @SqlQuery(AbstractStudentsDAO.workStudyQuery)
    List<Award> getWorkStudy(@Bind("id") String personID)

    /**
     * Return an internal ID given an OSU ID. Also validates that a person exists.
     * @param osuID
     * @return
     */
    @SqlQuery(AbstractStudentsDAO.personIDQuery)
    String getPersonID(@Bind("osuID") String osuID)

    @Override
    void close()
}
