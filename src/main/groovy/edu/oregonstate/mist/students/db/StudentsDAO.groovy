package edu.oregonstate.mist.students.db

import edu.oregonstate.mist.contrib.AbstractStudentsDAO
import edu.oregonstate.mist.students.AcademicStatusObject
import edu.oregonstate.mist.students.mapper.AcademicStandingMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.Mapper

public interface StudentsDAO extends Closeable {

    /**
     * Return credit hours enrolled at OSU.
     * @param personID
     * @param term
     * @return
     */
    @SqlQuery(AbstractStudentsDAO.osuCreditsQuery)
    Integer getOSUCreditHours(@Bind("id") String personID,
                              @Bind("term") String term)

    /**
     * Return credit hours enrolled at a non-OSU institution.
     * @param personID
     * @param term
     * @return
     */
    @SqlQuery(AbstractStudentsDAO.dualEnrollmentCreditsQuery)
    Integer getDualEnrollmentCreditHours(@Bind("id") String personID,
                                         @Bind("term") String term)

    /**
     * Return partial AcademicStatusObject with academic standing information
     * @param personID
     * @param term
     * @return
     */
    @Mapper(AcademicStandingMapper)
    @SqlQuery(AbstractStudentsDAO.academicStandingQuery)
    AcademicStatusObject getAcademicStanding(@Bind("id") String personID,
                                             @Bind("term") String term)

    /**
     * Return an internal ID given an OSU ID. Also validates that a person exists.
     * @param osuID
     * @return
     */
    @SqlQuery(AbstractStudentsDAO.personIDQuery)
    String getPersonID(@Bind("osu_id") String osuID)

    @Override
    void close()
}
