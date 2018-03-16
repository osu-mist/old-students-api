package edu.oregonstate.mist.students.mapper

import edu.oregonstate.mist.students.core.AcademicStatusObject
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

public class AcademicStandingMapper implements ResultSetMapper<AcademicStatusObject> {
    public AcademicStatusObject map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new AcademicStatusObject(
                academicStanding: rs.getString("academic_standing"),
                academicProbation: rs.getString("academic_probation_ind") == "Y",
                registrationBlocked: rs.getString("blocks_registration_ind") == "Y"
        )
    }
}
