package cs294.riviera.com.riviera_student;

import android.content.Context;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Wrapper around Parse backend operations.
 */
public class ParseWrapper {
    private Context mContext;

    public ParseWrapper(Context context) {
        mContext = context;
    }

    public void testSave() {
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }

    public UUID saveStudent(String email, String name, String website, byte[] fileData, int graduationYear) {
        // TODO: Add ACL to role
//        ParseRole role = new ParseRole("Student");

        ParseObject studentData = new ParseObject("StudentData");
        studentData.put("website", website);

        // TODO: Actually use image file
        ParseFile file = new ParseFile("resume.jpg", fileData);
        studentData.put("resume", file);
        studentData.put("graduationYear", graduationYear);
        UUID randomUUID = UUID.randomUUID();
        studentData.put("url", randomUUID.toString());

        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword("pass");
        user.put("name", name);
        user.put("type", "S");
//        user.put("role", role);

//        user.saveInBackground();
        try {
//            role.save();
            file.save();
            studentData.save();
            user.put("studentData", studentData);
            user.signUp();
            user.save();
            return randomUUID;
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Error signing up student", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public String saveRecruiter(String email, String password, String company) {
//        ParseRole role = new ParseRole("Recruiter");

        ParseObject recruiterData = new ParseObject("RecruiterData");
        recruiterData.put("company", company);

        ParseObject event = new ParseObject("Event");
        event.put("name", "IDD Showcase");
        event.put("date", new Date().toString());
        ArrayList<ParseObject> eventList = new ArrayList<>();
        eventList.add(event);

        recruiterData.put("events", eventList);

        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.put("type", "R");
//        user.put("role", role);
        try {
            event.save();
            recruiterData.save();
            user.put("recruiterData", recruiterData);
            user.signUp();
            user.save();
            return user.getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Error signing up recruiter", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public String logInRecruiter(String email, String password) {
        try {
            ParseUser user = ParseUser.logIn(email, password);
            return user.getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Error logging in", Toast.LENGTH_SHORT).show();
            return null;
        }
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        try {
            query.whereEqualTo("objectId", recruiterId);
            query.include("recruiterData");
            query.include("recruiterData.events");
//            query.include("recruiterData.events.name");
//            query.include("recruiterData.events.createdAt");
            List<ParseObject> recruiters = query.find();
            if (recruiters.size() > 0) {
                ParseObject recruiter = recruiters.get(0);
                ParseObject recruiterData = (ParseObject) recruiter.get("recruiterData");
                ArrayList events = (ArrayList) recruiterData.get("events");
                return events;
            } else {
                Toast.makeText(mContext, "No events found", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<ParseObject> getCandidatesForEvent(String eventId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        try {
            query.whereEqualTo("objectId", eventId);
            query.include("candidates");

            List<ParseObject> events = query.find();
            if (events.size() > 0) {
                ParseObject event = events.get(0);
                ArrayList candidates = (ArrayList) event.get("candidates");
                return candidates;
            } else {
                Toast.makeText(mContext, "No candidates found", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addStudentToRecruiterEvent(boolean reviewed, String name, File resume, String website, int graduationYear, String eventId, String url) {
        final ParseObject student = new ParseObject("StudentData");
        student.put("reviewed", reviewed);
        student.put("name", name);

        // TODO: Do something with resume, save ParseFile
//        student.put("resume", resume);
        student.put("website", website);
        student.put("graduationYear", graduationYear);
        student.put("url", url);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("objectId", eventId);
        query.include("candidates");
        try{
            List<ParseObject> events = query.find();
            if (events.size() > 0) {
                ParseObject event = events.get(0);

                ArrayList candidates = (ArrayList) event.get("candidates");
                if (candidates == null) {
                    candidates = new ArrayList();
                }
                candidates.add(student);
                event.put("candidates", candidates);
                event.saveInBackground();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addEventToRecruiter(String name, String date, String recruiterId) {
        final ParseObject event = new ParseObject("Event");
        event.put("name", name);
        event.put("date", date);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        try {
            query.whereEqualTo("objectId", recruiterId);
            query.include("recruiterData");
            query.include("recruiterData.events");
            List<ParseObject> recruiters = query.find();
            if (recruiters.size() > 0) {
                ParseObject recruiter = recruiters.get(0);
                ParseObject recruiterData = (ParseObject) recruiter.get("recruiterData");
                ArrayList events = (ArrayList) recruiterData.get("events");

                events.add(event);
                recruiterData.put("events", events);
                recruiterData.save();
                recruiter.put("recruiterData", recruiterData);
                recruiter.save();
            } else {
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
