package edu.oregonstate.mist.students.core

import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class WorkStudyObject {
    List<Award> awards
}

@EqualsAndHashCode
class Award {
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    Date effectiveStartDate

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    Date effectiveEndDate
    BigDecimal offerAmount

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    Date offerExpirationDate
    BigDecimal acceptedAmount

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    Date acceptedDate
    BigDecimal paidAmount
    String awardStatus
}
