package edu.oregonstate.mist.students.mapper

import edu.oregonstate.mist.students.core.MeetingTime
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalTime

public class MeetingTimeMapper implements ResultSetMapper<MeetingTime> {
    public MeetingTime map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new MeetingTime(
                startDate: rs.getDate("START_DATE"),
                endDate: rs.getDate("END_DATE"),
                beginTime: parseTime(rs.getString("BEGIN_TIME")),
                endTime: parseTime(rs.getString("END_TIME")),
                buildingCode: parseMeetingDay(rs.getString("BUILDING_CODE")),
                roomCode:  parseMeetingDay(rs.getString("ROOM_CODE")),
                meetsSunday:  parseMeetingDay(rs.getString("SUNDAY_MEETS")),
                meetsMonday:  parseMeetingDay(rs.getString("MONDAY_MEETS")),
                meetsTuesday:  parseMeetingDay(rs.getString("TUESDAY_MEETS")),
                meetsWednesday:  parseMeetingDay(rs.getString("WEDNESDAY_MEETS")),
                meetsThursday:  parseMeetingDay(rs.getString("THURSDAY_MEETS")),
                meetsFriday:  parseMeetingDay(rs.getString("FRIDAY_MEETS")),
                meetsSaturday:  parseMeetingDay(rs.getString("SATURDAY_MEETS")),
        )
    }

    private Boolean parseMeetingDay(String unparsedMeetingDay) {
        unparsedMeetingDay ?: false
    }

    private LocalTime parseTime(String unparsedTime) {
        Integer hour = Integer.parseInt(unparsedTime.substring(0,2))
        Integer minutes = Integer.parseInt(unparsedTime.substring(2))

        LocalTime.of(hour, minutes)
    }
}
