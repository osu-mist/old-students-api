package edu.oregonstate.mist.students.db

import edu.oregonstate.mist.contrib.AbstractStudentsDAO
import edu.oregonstate.mist.students.AcademicStatusObject
import edu.oregonstate.mist.students.mapper.AcademicStandingMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.Mapper

public interface StudentsDAO extends Closeable {
    @SqlQuery(AbstractStudentsDAO.osuCreditsQuery)
    Integer getOSUCreditHours(@Bind("id") String personID,
                              @Bind("term") String term)

    @SqlQuery(AbstractStudentsDAO.dualEnrollmentCreditsQuery)
    Integer getDualEnrollmentCreditHours(@Bind("id") String personID,
                                         @Bind("term") String term)

    @Mapper(AcademicStandingMapper)
    @SqlQuery(AbstractStudentsDAO.academicStandingQuery)
    AcademicStatusObject getAcademicStanding(@Bind("id") String personID,
                                             @Bind("term") String term)

    @SqlQuery(AbstractStudentsDAO.personIDQuery)
    String getPersonID(@Bind("osu_id") String osuID)

    @Override
    void close()
}
