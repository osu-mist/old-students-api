package edu.oregonstate.mist.students.db

import edu.oregonstate.mist.students.core.AcademicStatus
import edu.oregonstate.mist.students.core.AccountBalance
import edu.oregonstate.mist.students.core.AccountTransactions
import edu.oregonstate.mist.students.core.ClassSchedule
import edu.oregonstate.mist.students.core.Classification
import edu.oregonstate.mist.students.core.DualEnrollment
import edu.oregonstate.mist.students.core.GPALevels
import edu.oregonstate.mist.students.core.Grade
import edu.oregonstate.mist.students.core.Holds
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
    private String getPersonID(String osuID) {
        String personID = studentsDAO.getPersonID(osuID)

        if (!personID) {
            throw new StudentNotFoundException("Student not found")
        }

        personID
    }

    /**
     * Return a list of DualEnrollment objects with dual enrollment credit hours.
     * @param personID
     * @param term
     * @return
     */
    public List<DualEnrollment> getDualEnrollment(String osuID, String term) {
        term = getTerm(term)

        if (term && !studentsDAO.isValidTerm(term)) {
            throw new InvalidTermException("Term: $term is invalid.")
        }

        studentsDAO.getDualEnrollment(getPersonID(osuID), getTerm(term))
    }

    /**
     * Get a workstudy object with work study financial aid awards
     * @param personID
     * @return
     */
    public WorkStudyObject getWorkStudy(String osuID) {
        new WorkStudyObject(awards: studentsDAO.getWorkStudy(getPersonID(osuID)))
    }

    public Classification getClassification(String osuID) {
        httpStudentsDAO.getClassification(osuID)
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
        httpStudentsDAO.getAcademicStatus(osuID, getTerm(term))
    }

    public List<Grade> getGrades(String osuID, String term) {
        httpStudentsDAO.getGrades(osuID, getTerm(term))
    }

    public List<ClassSchedule> getClassSchedule(String osuID, String term) {
        httpStudentsDAO.getClassSchedule(osuID, getTerm(term))
    }

    public Holds getHolds(String osuID) {
        httpStudentsDAO.getHolds(osuID)
    }

    private String getTerm(String term) {
        if (term == "current") {
            studentsDAO.getCurrentTerm()
        } else {
            term
        }
    }
}

@InheritConstructors
class StudentNotFoundException extends Exception {}

@InheritConstructors
class InvalidTermException extends Exception {}

@InheritConstructors
class TermRequiredException extends Exception {}