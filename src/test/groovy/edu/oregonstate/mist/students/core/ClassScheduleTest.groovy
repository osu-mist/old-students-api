package edu.oregonstate.mist.students.core

import edu.oregonstate.mist.students.db.BackendMeetingTime
import org.junit.Test

import static org.junit.Assert.assertEquals

class ClassScheduleTest {
    @Test
    void testWeeklyScheduleMapping() {
        BackendMeetingTime backendMeetingTime = new BackendMeetingTime()

        backendMeetingTime.with {
            monday = wednesday = friday = true
            tuesday = thursday = saturday = sunday = false
        }
        MeetingTime meetingTime = MeetingTime.fromBackendMeetingTime(backendMeetingTime)
        assertEquals(meetingTime.weeklySchedule, ['M', 'W', 'F'])

        backendMeetingTime.with {
            // test that null days aren't included in the array, null should == false
            monday = thursday = null
            tuesday = saturday = sunday = true
            wednesday = friday = false
        }
        meetingTime = MeetingTime.fromBackendMeetingTime(backendMeetingTime)
        assertEquals(meetingTime.weeklySchedule, ['T', 'Sa', 'Su'])
    }

    @Test
    void testWeeklyScheduleAllOrNone() {
        BackendMeetingTime backendMeetingTime = new BackendMeetingTime()

        backendMeetingTime.with {
            monday = tuesday = wednesday = thursday = friday = saturday = sunday = true
        }
        MeetingTime meetingTime = MeetingTime.fromBackendMeetingTime(backendMeetingTime)
        assertEquals(meetingTime.weeklySchedule, ['M', 'T', 'W', 'Th', 'F', 'Sa', 'Su'])

        backendMeetingTime.with {
            monday = tuesday = wednesday = thursday = friday = saturday = sunday = false
        }
        meetingTime = MeetingTime.fromBackendMeetingTime(backendMeetingTime)
        assertEquals(meetingTime.weeklySchedule, [])

        meetingTime = MeetingTime.fromBackendMeetingTime(new BackendMeetingTime())
        assertEquals(meetingTime.weeklySchedule, [])
    }
}
