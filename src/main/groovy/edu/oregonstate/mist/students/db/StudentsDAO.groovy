package edu.oregonstate.mist.students.db

import edu.oregonstate.mist.contrib.AbstractStudentsDAO
import edu.oregonstate.mist.students.core.AcademicStatusObject
import edu.oregonstate.mist.students.core.Award
import edu.oregonstate.mist.students.mapper.AcademicStandingMapper
import edu.oregonstate.mist.students.mapper.AwardMapper
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

    @Mapper(AwardMapper)
    @SqlQuery(AbstractStudentsDAO.workStudyQuery)
    List<Award> getWorkStudy(@Bind("id") String personID)

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
