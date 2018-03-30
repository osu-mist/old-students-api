package edu.oregonstate.mist.students.mapper

import edu.oregonstate.mist.students.core.AcademicStatusObject
import org.apache.commons.lang3.text.WordUtils
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

public class AcademicStandingMapper implements ResultSetMapper<AcademicStatusObject> {
    public AcademicStatusObject map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        def rawStanding = rs.getString("academic_standing")
        new AcademicStatusObject(
            academicStanding: WordUtils.capitalizeFully(rawStanding.replace('*', '').trim()),
            academicProbation: rs.getString("academic_probation_ind") == "Y",
            registrationBlocked: rs.getString("blocks_registration_ind") == "Y"
        )
    }
}
