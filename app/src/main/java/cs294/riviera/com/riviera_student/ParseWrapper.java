package cs294.riviera.com.riviera_student;

import android.content.Context;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Wrapper around Parse backend operations.
 */
public class ParseWrapper {

    public ParseWrapper(Context context) {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(context);

        Parse.initialize(context, "B0GS111ytFTrli3CciVw2gFuI5LENEkXxZ1jiwBD", "RpJoBrqgJfyGtCvxju9nowsdT2tcCpFY4q3TUR0i");
    }

    public void testSave() {
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }

    public void saveStudent(String email, String name, String website, byte[] fileData, int graduationYear) {
        // TODO: Add ACL to role
//        ParseRole role = new ParseRole("Student");

        ParseObject studentData = new ParseObject("StudentData");
        studentData.put("website", website);

        // TODO: Actually use image file
//        ParseFile file = new ParseFile("resume.jpg", fileData);
//        studentData.put("resume", file);
        studentData.put("graduationYear", graduationYear);
        studentData.put("url", UUID.randomUUID());

        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword("pass");
        user.put("name", name);
//        user.put("role", role);

//        user.saveInBackground();
        try {
//            role.save();
            studentData.save();
            user.signUp();
            user.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void saveRecruiter(String email, String company, String website) {
        ParseRole role = new ParseRole("Recruiter");

        ParseObject recruiterData = new ParseObject("RecruiterData");
        recruiterData.put("company", company);
        recruiterData.put("website", website);

        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword("pass");
        user.setEmail(email);
        user.put("role", role);

        user.saveInBackground();
    }

    public ParseUser getStudent(String studentId){
        ParseQuery<ParseUser> query = ParseQuery.getQuery("Student");
        try {
            return query.get(studentId);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ParseUser getRecruiter(String recruiterId) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery("Recruiter");
        try {
            return query.get(recruiterId);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ParseObject getCandidate(String candidateId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Candidate");
        try {
            return query.get(candidateId);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<ParseObject> getEventsForRecruiter(String recruiterId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Recruiter");
        try {
            ParseObject recruiter  = query.get(recruiterId);
            ParseObject recruiterData = query.get("recruiterData");
            ArrayList<ParseObject> events = (ArrayList) recruiterData.get("events");
            return events;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<ParseObject> getCandidatesForEvent(String eventId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        try {
            ParseObject event  = query.get(eventId);
            ArrayList<ParseObject> candidates = (ArrayList) event.get("candidates");
            return candidates;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addCandidateToRecruiterEvent(final ParseObject feedback, final ParseUser student, String eventId) {
        final ParseObject candidate = new ParseObject("Candidate");
        candidate.put("student", student);
        candidate.put("feedback", feedback);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.getInBackground(eventId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ArrayList<ParseObject> candidates = (ArrayList) object.get("candidates");
                    candidates.add(candidate);

                    object.put("candidates", candidates);
                    object.saveInBackground();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addEventToRecruiter(String name, ArrayList<ParseObject> students, String recruiterId) {
        final ParseObject event = new ParseObject("Event");
        event.put("name", name);
        event.put("students", students);

        ParseQuery<ParseUser> query = ParseQuery.getQuery("Recruiter");
        query.getInBackground(recruiterId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    ParseObject recruiterData = object.getParseObject("recruiterData");
                    ArrayList<ParseObject> eventList = (ArrayList) recruiterData.get("events");

                    eventList.add(event);
                    recruiterData.put("events", eventList);
                    recruiterData.saveInBackground();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addFeedbackOptionsToRecruiter(String title, ArrayList<String> options, String recruiterId) {
        final ParseObject feedback = new ParseObject("Feedback");
        feedback.put("title", title);
        feedback.put("options", options);

        ParseQuery<ParseUser> query = ParseQuery.getQuery("Recruiter");
        query.getInBackground(recruiterId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    ParseObject recruiterData = object.getParseObject("recruiterData");
                    ArrayList<ParseObject> feedbackList = (ArrayList) recruiterData.get("feedbacks");

                    feedbackList.add(feedback);
                    recruiterData.put("feedbacks", feedbackList);
                    recruiterData.saveInBackground();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
