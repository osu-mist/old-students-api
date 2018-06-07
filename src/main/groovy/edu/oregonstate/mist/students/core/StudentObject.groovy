package edu.oregonstate.mist.students.core

class StudentObject {
    List<Degree> degrees
}

class Degree {
    String degree
    String degreeDescription
    String degreeCategoryDescription
    String status
    String majorDescription
    String levelDescription
    String collegeDescription
    Boolean graduated
    String campusDescription
    BigDecimal gpa
    Date graduationApplicationDate
    Date graduationDate
}