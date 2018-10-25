package edu.oregonstate.mist.students.core

import edu.oregonstate.mist.students.db.BackendGeneralInfo

class GeneralInfo {
    String firstName
    String middleName
    String lastName
    String fullName
    String level
    String classification

    static GeneralInfo fromBackendGeneralInfo(BackendGeneralInfo backendGeneralInfo) {
        new GeneralInfo(
            firstName: backendGeneralInfo.firstName,
            middleName: backendGeneralInfo.middleName,
            lastName: backendGeneralInfo.lastName,
            fullName: backendGeneralInfo.fullName,
            level: backendGeneralInfo.level,
            classification: backendGeneralInfo.classification
        )
    }
}
