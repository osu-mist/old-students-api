package edu.oregonstate.mist.students.mapper

import edu.oregonstate.mist.students.core.Degree
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

public class DegreeMapper implements ResultSetMapper<Degree> {
    public Degree map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Degree(
                degree: rs.getString("DEGREE"),
                degreeDescription: rs.getString("DEGREE_DESC"),
                degreeCategoryDescription: rs.getString("AWARD_CATEGORY_DESC"),
                status: rs.getString("STATUS_DESC"),
                majorDescription: rs.getString("MAJOR_DESC"),
                levelDescription: rs.getString("STUDENT_LEVEL_DESC"),
                collegeDescription: rs.getString("COLLEGE_DESC"),
                graduated: rs.getString("GRADUATED_IND") == "Y",
                campusDescription: rs.getString("CAMPUS_DESC"),
                gpa: getGpa(rs.getBigDecimal("GPA"))
        )
    }

    private static BigDecimal getGpa(BigDecimal gpa) {
        (gpa == BigDecimal.ZERO) ? null : gpa
    }
}
