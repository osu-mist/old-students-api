package edu.oregonstate.mist.students.core

import edu.oregonstate.mist.students.db.BackendAcademicStanding

class AcademicStatus {
    String academicStanding
    String term
    String termDescription
    List<GPA> gpa

    static AcademicStatus fromBackendAcademicStanding(
            BackendAcademicStanding backendAcademicStanding) {
        new AcademicStatus(
                academicStanding: backendAcademicStanding.academicStandingDescription,
                term: backendAcademicStanding.academicStandingTerm,
                termDescription: backendAcademicStanding.academicStandingTermDescription,
                gpa: backendAcademicStanding.termGPAs.collect {
                    GPA.fromBackendGPA(it)
                }
        )
    }
}
