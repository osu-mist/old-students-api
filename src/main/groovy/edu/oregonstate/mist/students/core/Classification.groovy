package edu.oregonstate.mist.students.core

class Classification {
    String level
    String classification

    static Classification fromMeasure(Measure measure) {
        new Classification(
            level: measure.level,
            classification: measure.classification
        )
    }
}

class Measure {
    String level
    String classification
}