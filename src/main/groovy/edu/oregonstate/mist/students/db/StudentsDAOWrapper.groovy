package edu.oregonstate.mist.students.db

import edu.oregonstate.mist.students.core.DualEnrollment
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
}
