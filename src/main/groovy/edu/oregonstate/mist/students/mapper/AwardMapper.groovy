package edu.oregonstate.mist.students.mapper

import edu.oregonstate.mist.students.core.Award
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

class AwardMapper implements ResultSetMapper<Award> {
    public Award map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Award(
                effectiveStartDate: rs.getDate("award_begin_date"),
                effectiveEndDate: rs.getDate("award_end_date"),
                offerAmount: rs.getBigDecimal("offer_amount"),
                offerExpirationDate: rs.getDate("offer_expiration_date"),
                acceptedAmount: rs.getBigDecimal("accepted_amount"),
                acceptedDate: rs.getDate("accepted_date"),
                paidAmount: rs.getBigDecimal("paid_amount"),
                awardStatus: rs.getString("award_status")
        )
    }
}
