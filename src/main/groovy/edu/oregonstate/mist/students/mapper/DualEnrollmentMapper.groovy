package edu.oregonstate.mist.students.mapper

import edu.oregonstate.mist.students.core.DualEnrollment
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

class DualEnrollmentMapper implements ResultSetMapper<DualEnrollment> {
    public DualEnrollment map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new DualEnrollment(
                term: rs.getString("TERM"),
                creditHours: rs.getInt("DUAL_ENROLLMENT_HOURS")
        )
    }
}