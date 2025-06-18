package org.example.Models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Enrollment {
    // For embedded enrollment
    private Document student;
    private Document course;

    // For referenced enrollment
    private ObjectId studentId;
    private ObjectId courseId;

    // Constructors
    public Enrollment(Document student, Document course) {
        this.student = student;
        this.course = course;
    }

    public Enrollment(ObjectId studentId, ObjectId courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    // Methods to convert to BSON document for MongoDB
    public Document toEmbeddedDocument() {
        return new Document("student", student).append("course", course);
    }

    public Document toReferencedDocument() {
        return new Document("studentId", studentId).append("courseId", courseId);
    }
}
