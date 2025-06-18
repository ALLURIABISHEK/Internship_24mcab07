package org.example;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.Models.Student;
import org.example.Models.Course;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        MongoDatabase db = MongoDBUtil.getDatabase();
        MongoCollection<Document> students = db.getCollection("students");
        MongoCollection<Document> courses = db.getCollection("courses");
        MongoCollection<Document> enrollments = db.getCollection("enrollments");

        // Clean up existing data
        students.drop();
        courses.drop();
        enrollments.drop();

        // Step 1: Insert students
        Student s1 = new Student("ABISHEK", "abishek@gmail.com");
        Student s2 = new Student("SAM", "sam@mail.com");

        Document sDoc1 = new Document("name", s1.name).append("email", s1.email);
        Document sDoc2 = new Document("name", s2.name).append("email", s2.email);
        students.insertMany(Arrays.asList(sDoc1, sDoc2));

        // Step 5: Create indexes on students collection
        students.createIndex(new Document("name", 1));
        students.createIndex(new Document("email", 1));

        // Step 1: Insert courses
        Course c1 = new Course("JAVA", "Dr. Abhinav");
        Course c2 = new Course("Python", "Dr. Bharathi");

        Document cDoc1 = new Document("title", c1.title).append("instructor", c1.instructor);
        Document cDoc2 = new Document("title", c2.title).append("instructor", c2.instructor);
        courses.insertMany(Arrays.asList(cDoc1, cDoc2));

        // Step 2: Add Enrollments
        Document embeddedEnrollment = new Document("student", sDoc1)
                .append("course", cDoc1)
                .append("type", "embedded");

        Document referencedEnrollment = new Document("studentId", sDoc2.getObjectId("_id"))
                .append("courseId", cDoc2.getObjectId("_id"))
                .append("type", "referenced");

        enrollments.insertMany(Arrays.asList(embeddedEnrollment, referencedEnrollment));

        // Step 3: Query and print enrollments
        System.out.println("\n--- Embedded Enrollments ---");
        for (Document doc : enrollments.find(new Document("type", "embedded"))) {
            Document student = (Document) doc.get("student");
            Document course = (Document) doc.get("course");
            System.out.println("Student: " + student.getString("name") + " (" + student.getString("email") + ")");
            System.out.println("Course: " + course.getString("title") + " by " + course.getString("instructor"));
        }

        System.out.println("\n--- Referenced Enrollments ---");
        for (Document doc : enrollments.find(new Document("type", "referenced"))) {
            ObjectId studentId = doc.getObjectId("studentId");
            ObjectId courseId = doc.getObjectId("courseId");

            Document student = students.find(new Document("_id", studentId)).first();
            Document course = courses.find(new Document("_id", courseId)).first();

            if (student != null && course != null) {
                System.out.println("Student: " + student.getString("name") + " (" + student.getString("email") + ")");
                System.out.println("Course: " + course.getString("title") + " by " + course.getString("instructor"));
            }
        }

        // Step 4: Update student name (referenced)
        students.updateOne(
                new Document("name", "SAM"),
                new Document("$set", new Document("name", "Nissy"))
        );

        // Show referenced enrollment reflects update
        System.out.println("\n--- Referenced Enrollment After Name Update ---");
        for (Document doc : enrollments.find(new Document("type", "referenced"))) {
            ObjectId studentId = doc.getObjectId("studentId");
            Document updatedStudent = students.find(new Document("_id", studentId)).first();
            if (updatedStudent != null) {
                System.out.println("Updated Student: " + updatedStudent.toJson());
            }
        }

        // Show embedded enrollment is unchanged
        System.out.println("\n--- Embedded Enrollment Still Has Old Name ---");
        for (Document doc : enrollments.find(new Document("type", "embedded"))) {
            System.out.println(doc.toJson());
        }
    }
}
