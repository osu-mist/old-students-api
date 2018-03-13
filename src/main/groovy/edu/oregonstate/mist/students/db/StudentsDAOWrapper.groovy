package edu.oregonstate.mist.students.db

import edu.oregonstate.mist.students.core.AcademicStatusObject
import edu.oregonstate.mist.students.core.WorkStudyObject

class StudentsDAOWrapper {
    private final StudentsDAO studentsDAO

    StudentsDAOWrapper(StudentsDAO studentsDAO) {
        this.studentsDAO = studentsDAO
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
     * Return a complete AcademicStatusObject with credits and academic standing.
     * @param personID
     * @param term
     * @return
     */
    public AcademicStatusObject getAcademicStatus(String personID, String term) {
        AcademicStatusObject academicStatus = studentsDAO.getAcademicStanding(personID, term)

        if (!academicStatus) {
            academicStatus = new AcademicStatusObject()
        }

        academicStatus.osuHours = studentsDAO.getOSUCreditHours(personID, term)
        academicStatus.dualEnrollmentHours = studentsDAO.getDualEnrollmentCreditHours(personID,
                term)

        academicStatus
    }

    /**
     * Get a workstudy object with work study financial aid awards
     * @param personID
     * @return
     */
    public WorkStudyObject getWorkStudy(String personID) {
        new WorkStudyObject(awards: studentsDAO.getWorkStudy(personID))
    }
}
