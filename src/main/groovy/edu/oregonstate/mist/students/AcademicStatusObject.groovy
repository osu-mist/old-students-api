package edu.oregonstate.mist.students

class AcademicStatusObject {
    // Number of credit hours currently enrolled at OSU
    Integer osuHours
    // Number of credit hours currently enrolled as part of a dual enrollment program
    Integer dualEnrollmentHours
    // Academic standing
    String academicStanding
    // If true, the current academic standing prevents registration
    Boolean registrationBlocked
    // If true, the current academic standing indicated academic probation
    Boolean academicProbation
}
