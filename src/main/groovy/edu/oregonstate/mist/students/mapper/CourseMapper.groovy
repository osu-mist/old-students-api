package edu.oregonstate.mist.students.mapper

import edu.oregonstate.mist.students.core.Course
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

public class CourseMapper implements ResultSetMapper<Course> {
    public Course map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Course(
                courseID: rs.getString("COURSE_ID"),
                credits: rs.getInt("CREDIT_HOURS"),
                withdrawn: rs.getString("WITHDRAW_IND") != 'N',
                courseSubject: rs.getString("COURSE_SUBJECT"),
                courseNumber: rs.getString("COURSE_NUMBER"),
                courseSection: rs.getString("COURSE_SECTION_NUMBER"),
                courseStartDate: rs.getDate("START_DATE"),
                courseEndDate: rs.getDate("END_DATE"),
                finalLetterGrade: rs.getString("FINAL_GRADE"),
                finalGrade: getFinalGrade(rs.getInt("GRADE_VALUE"))
        )
    }

    private static Integer getFinalGrade(Integer finalGrade) {
        (finalGrade == 0) ? null : finalGrade
    }
}
