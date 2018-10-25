package edu.oregonstate.mist.students.core

import edu.oregonstate.mist.students.db.BackendGeneralInfo

class GeneralInfo {
    String level
    String classification

    static GeneralInfo fromBackendGeneralInfo(BackendGeneralInfo backendGeneralInfo) {
        new GeneralInfo(
            level: backendGeneralInfo.level,
            classification: backendGeneralInfo.classification
        )
    }
}
