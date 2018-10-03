package edu.oregonstate.mist.students.db

import edu.oregonstate.mist.students.core.AcademicStatus
import edu.oregonstate.mist.students.core.AccountBalance
import edu.oregonstate.mist.students.core.AccountTransactions
import edu.oregonstate.mist.students.core.DualEnrollment
import edu.oregonstate.mist.students.core.GPALevels
import edu.oregonstate.mist.students.core.Grade
import edu.oregonstate.mist.students.core.WorkStudyObject
import groovy.transform.InheritConstructors

class StudentsDAOWrapper {
    private final StudentsDAO studentsDAO
    private final HttpStudentsDAO httpStudentsDAO

    StudentsDAOWrapper(StudentsDAO studentsDAO, HttpStudentsDAO httpStudentsDAO) {
        this.studentsDAO = studentsDAO
        this.httpStudentsDAO = httpStudentsDAO
    }

    /**
     * Return an internal ID given an OSU ID. Also validates that a person exists.
     * @param osuID
     * @return
     */
    public String getPersonID(String osuID) {
        studentsDAO.getPersonID(osuID)
    }

    /**
     * Return a list of DualEnrollment objects with dual enrollment credit hours.
     * @param personID
     * @param term
     * @return
     */
    public List<DualEnrollment> getDualEnrollment(String personID, String term) {
        studentsDAO.getDualEnrollment(personID, term)
    }

    /**
     * Get a workstudy object with work study financial aid awards
     * @param personID
     * @return
     */
    public WorkStudyObject getWorkStudy(String personID) {
        new WorkStudyObject(awards: studentsDAO.getWorkStudy(personID))
    }

    public AccountBalance getAccountBalance(String osuID) {
        httpStudentsDAO.getAccountBalance(osuID)
    }

    public AccountTransactions getAccountTransactions(String osuID) {
        httpStudentsDAO.getAccountTransactions(osuID)
    }

    public GPALevels getGPA(String osuID) {
        httpStudentsDAO.getGPA(osuID)
    }

    public List<AcademicStatus> getAcademicStatus(String osuID, String term) {
        httpStudentsDAO.getAcademicStatus(osuID, term)
    }

    public List<Grade> getGrades(String osuID, String term) {
        httpStudentsDAO.getGrades(osuID, term)
    }
}

@InheritConstructors
class StudentNotFoundException extends Exception {}

@InheritConstructors
class InvalidTermException extends Exception {}
