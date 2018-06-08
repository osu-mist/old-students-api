package edu.oregonstate.mist.students.db

import edu.oregonstate.mist.contrib.AbstractStudentsDAO
import edu.oregonstate.mist.students.core.AcademicStatusObject
import edu.oregonstate.mist.students.core.Award
import edu.oregonstate.mist.students.core.Course
import edu.oregonstate.mist.students.core.Degree
import edu.oregonstate.mist.students.core.MeetingTime
import edu.oregonstate.mist.students.mapper.AcademicStandingMapper
import edu.oregonstate.mist.students.mapper.AwardMapper
import edu.oregonstate.mist.students.mapper.CourseMapper
import edu.oregonstate.mist.students.mapper.DegreeMapper
import edu.oregonstate.mist.students.mapper.MeetingTimeMapper
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
     * Get a list of award objects for work study financial aid awards
     * @param personID
     * @return
     */
    @Mapper(AwardMapper)
    @SqlQuery(AbstractStudentsDAO.workStudyQuery)
    List<Award> getWorkStudy(@Bind("id") String personID)

    @Mapper(CourseMapper)
    @SqlQuery(AbstractStudentsDAO.courseQuery)
    List<Course> getCourses(@Bind("id") String personID,
                            @Bind("term") String term)

    @Mapper(MeetingTimeMapper)
    @SqlQuery(AbstractStudentsDAO.meetingTimeQuery)
    List<MeetingTime> getMeetingTimes(@Bind("term") String term,
                                      @Bind("courseID") String courseID)

    /**
     * Return an internal ID given an OSU ID. Also validates that a person exists.
     * @param osuID
     * @return
     */
    @SqlQuery(AbstractStudentsDAO.personIDQuery)
    String getPersonID(@Bind("osu_id") String osuID)

    @Mapper(DegreeMapper)
    @SqlQuery(AbstractStudentsDAO.degreesQuery)
    List<Degree> getDegrees(@Bind("id") String personID)

    @Override
    void close()
}
