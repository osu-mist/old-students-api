package edu.oregonstate.mist.students.mapper

import edu.oregonstate.mist.students.AcademicStatusObject
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

public class AcademicStandingMapper implements ResultSetMapper<AcademicStatusObject> {
    public AcademicStatusObject map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new AcademicStatusObject(
                academicStanding: rs.getString("ACADEMIC_STANDING"),
                academicProbation: rs.getString("ACADEMIC_PROBATION_IND") == "Y",
                registrationBlocked: rs.getString("BLOCKS_REGISTRATION_IND") == "Y"
        )
    }
}
