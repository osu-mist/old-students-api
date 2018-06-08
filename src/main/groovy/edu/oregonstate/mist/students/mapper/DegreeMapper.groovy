package edu.oregonstate.mist.students.mapper

import edu.oregonstate.mist.students.core.Degree
import edu.oregonstate.mist.students.core.DegreeArea
import edu.oregonstate.mist.students.core.Major
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

public class DegreeMapper implements ResultSetMapper<Degree> {
    static final String majorColumn = "MAJOR"
    static final String minorColumn = "MINOR"
    static final String concentrationColumn = "CONCENTRATION"

    public Degree map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Degree(
                degree: rs.getString("DEGREE"),
                degreeDescription: rs.getString("DEGREE_DESC"),
                degreeCategoryDescription: rs.getString("AWARD_CATEGORY_DESC"),
                status: rs.getString("STATUS_DESC"),
                majors: getMajors(rs),
                levelDescription: rs.getString("STUDENT_LEVEL_DESC"),
                collegeDescription: rs.getString("COLLEGE_DESC"),
                graduated: rs.getString("GRADUATED_IND") == "Y",
                campusDescription: rs.getString("CAMPUS_DESC"),
                gpa: getGpa(rs.getBigDecimal("GPA")),
                graduationApplicationDate: rs.getDate("OUTCOME_APPLICATION_DATE"),
                graduationDate: rs.getDate("OUTCOME_GRADUATION_DATE")
        )
    }

    private List<Major> getMajors(ResultSet rs) {
        List<Major> majors = []

        def addMajor = { DegreeArea major, DegreeArea concentration ->
            Major majorWithConcentration = new Major(
                    major: major,
            )

            if (concentration) {
                majorWithConcentration.concentration = concentration
            }

            majors.add(majorWithConcentration)
        }

//        ["", "SECOND_", "THIRD_", "FOURTH_"].each {
//            String majorID = rs.getString("${it}MAJOR")
//            addMajor(
//                    new DegreeArea(id: rs.getString("${it}MAJOR")),
//                    new DegreeArea(id: rs.getString("${it}C")) )
//        }

        addMajor(new DegreeArea(id: rs.getString("MAJOR"), name: rs.getString("MAJOR_DESC")),
                 new DegreeArea(id: rs.getString("FIRST_CONCENTRATION"),
                         name: rs.getString("FIRST_CONCENTRATION_DESC")))

        addMajor(new DegreeArea(id: rs.getString("SECOND_MAJOR"),
                        name: rs.getString("SECOND_MAJOR_DESC")),
                 new DegreeArea(id: rs.getString("SECOND_CONCENTRATION"),
                        name: rs.getString("SECOND_CONCENTRATION_DESC")))

        addMajor(new DegreeArea(id: rs.getString("THIRD_MAJOR"),
                        name: rs.getString("THIRD_MAJOR_DESC")),
                    new DegreeArea(id: rs.getString("THIRD_CONCENTRATION"),
                        name: rs.getString("THIRD_CONCENTRATION_DESC")))

        addMajor(new DegreeArea(id: rs.getString("FOURTH_MAJOR"),
                        name: rs.getString("FOURTH_MAJOR_DESC")), null)


//        String majorID = rs.getString("${it}MAJOR")
//        if (firstMajor) {
//            String firstConcentration = rs.getString("FIRST_CONCENTRATION")
//        }

        majors
    }

    private List<DegreeArea> getMinors(ResultSet rs) {

    }

    private static BigDecimal getGpa(BigDecimal gpa) {
        (gpa == BigDecimal.ZERO) ? null : gpa
    }
}
