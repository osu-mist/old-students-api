package edu.oregonstate.mist.students.core

import com.fasterxml.jackson.annotation.JsonFormat
import edu.oregonstate.mist.students.db.BackendHold

import java.time.LocalDate

class Holds {
    List<Hold> holds

    static fromBackendHolds(List<BackendHold> backendHolds) {
        new Holds(
                holds: backendHolds.collect {
                    it.with {
                        new Hold(
                                fromDate: fromDate,
                                toDate: toDate,
                                description: holdTypeDescription,
                                processesAffected: processAffectedDescription,
                                reason: reason
                        )
                    }
                }
        )
    }
}

class Hold {
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalDate fromDate
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalDate toDate
    String description
    List<String> processesAffected
    String reason
}
