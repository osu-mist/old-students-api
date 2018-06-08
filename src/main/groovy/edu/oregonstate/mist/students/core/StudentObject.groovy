package edu.oregonstate.mist.students.core

import com.fasterxml.jackson.annotation.JsonUnwrapped

class StudentObject {
    List<Degree> degrees
}

class Degree {
    String degree
    String degreeDescription
    String degreeCategoryDescription
    String status
    List<Major> majors
    List<DegreeArea> minors
    String levelDescription
    String collegeDescription
    Boolean graduated
    String campusDescription
    BigDecimal gpa
    Date graduationApplicationDate
    Date graduationDate
}

class Major {
    @JsonUnwrapped
    DegreeArea major

    DegreeArea concentration
}

class DegreeArea {
    String id
    String name
}