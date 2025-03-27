package Hw3_autotests;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import application.Question;

/**
 * A comprehensive test suite for the Question and Answer management system.
 * This class contains automated tests for various database operations including
 * question management and answer handling.
 *
 * <p>The test suite includes the following test cases:
 * <ul>
 *   <li>Answer update functionality</li>
 *   <li>Question update functionality</li>
 *   <li>Answer deletion</li>
 *   <li>Resolved answer retrieval</li>
 *   <li>Question retrieval</li>
 * </ul>
 *
 * <p>Each test case is executed independently and reports its success or failure.
 * A summary of all test results is displayed at the end of execution.
 *
 * @see databasePart1.DatabaseHelper
 * @see application.Question
 */
public class Autotests {
    
    /** Counter for successfully passed test cases */
    static int tests_passed = 0;
    
    /** Counter for failed test cases */
    static int tests_failed = 0;
    
    /** Database helper instance for performing database operations */
    static DatabaseHelper dbHelper = new DatabaseHelper();
    
    /**
     * Main entry point for the test suite.
     * Executes all test cases and displays the results.
     *
     * <p>The test execution flow:
     * <ol>
     *   <li>Connects to the database</li>
     *   <li>Executes each test case in sequence</li>
     *   <li>Displays individual test results</li>
     *   <li>Shows final summary of passed and failed tests</li>
     * </ol>
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            dbHelper.connectToDatabase(); // Ensure database is connected

            // Run test cases
            performTestCase(1, "Testing updating an answer", () -> testUpdateAnswer());
            performTestCase(2, "Testing updating a question", () -> updateQuestion_test());
            performTestCase(3, "Testing deleting an answer", () -> testDeleteAnswer());
            performTestCase(4, "Testing getting resolved answer", () -> testGetResolvedAnswer());
            performTestCase(5, "Testing getting questions", () -> getQuestion_test());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Display test summary
        System.out.println("____________________________________________________________________________");
        System.out.println("\nNumber of tests passed: " + tests_passed);
        System.out.println("Number of tests failed: " + tests_failed);
    }
    
    /**
     * Executes a single test case and reports its result.
     *
     * @param testCase the test case number
     * @param inputText description of the test case
     * @param method the test method to execute
     */
    public static void performTestCase(int testCase, String inputText, TestMethod method) {
        
        // Display test case header
        System.out.println("____________________________________________________________________________\n");
        System.out.println("Test case: " + testCase + ": " + inputText);
        
        try {
            boolean result = method.run();
            if(result) {
                System.out.println("Success: " + inputText + " passed.");
                tests_passed++;
            } else {
                System.out.println("Failure: " + inputText + " failed.");
                tests_failed++;
            }
        } catch (Exception e) {
            System.out.println("Exception, the test case found error: " + e.getMessage());
            tests_failed++;
        }
    }

    /**
     * Functional interface for test methods.
     * All test methods must implement this interface.
     */
    @FunctionalInterface
    public interface TestMethod {
        /**
         * Executes the test method.
         *
         * @return true if the test passed, false otherwise
         * @throws SQLException if a database error occurs
         */
        boolean run() throws SQLException;
    }
    
    /**
     * Deletes all questions from the database for test isolation.
     * This method ensures a clean state before running each test.
     *
     * @throws SQLException if a database error occurs
     */
    public static void delete_all_questions() throws SQLException {
        List<Question> questions = dbHelper.getAllQuestions();
        for(Question tmp : questions) {
            dbHelper.deleteQuestion(tmp.getUsername(), tmp.getQuestion());
        }
    }
    
    /**
     * Tests the answer update functionality.
     * Verifies that an answer can be successfully updated in the database.
     *
     * <p>Test steps:
     * <ol>
     *   <li>Creates a test question and answer</li>
     *   <li>Updates the answer with new content</li>
     *   <li>Verifies the answer was updated successfully</li>
     * </ol>
     *
     * @return true if the answer update test passes, false otherwise
     * @throws SQLException if a database error occurs
     */
    public static boolean testUpdateAnswer() throws SQLException {
        String user = "testUser";
        String oldAnswer = "This is the old answer.";
        String newAnswer = "This is the new answer.";
        String question = "What is a good question?";

        dbHelper.addAnswer(question, user, oldAnswer);
        boolean updated = dbHelper.updateAnswer(user, oldAnswer, newAnswer);
        
        List<String> answers = dbHelper.getAnswersForQuestion(question);
        return updated && answers.contains(user + ": " + newAnswer);
    }
    
    /**
     * Tests the question update functionality.
     * Verifies that a question can be updated and the old version is removed.
     *
     * <p>Test steps:
     * <ol>
     *   <li>Clears all existing questions</li>
     *   <li>Adds an initial question</li>
     *   <li>Updates the question</li>
     *   <li>Verifies the update was successful</li>
     * </ol>
     *
     * @return true if the update test passes, false otherwise
     * @throws SQLException if a database error occurs
     */
    public static boolean updateQuestion_test() throws SQLException {
        delete_all_questions();
    
        String user = "USER";
        String init_question = "INIT QUESTION";
        dbHelper.addQuestion(user, init_question);
        
        String updated_question = "UPDATED QUESTION";
        
        boolean updated = dbHelper.updateQuestion(user, init_question, updated_question);
        
        List<Question> questions = dbHelper.getAllQuestions();
        
        boolean found_updated = false;
        boolean found_init = false;
        
        for(Question tmp : questions) {
            if(tmp.getQuestion().equals(updated_question)) {
                found_updated = true;
            }
            if(tmp.getQuestion().equals(init_question)) {
                found_init = true;
            }
        }
        return updated && found_updated && !found_init;
    }
    
    /**
     * Tests the answer deletion functionality.
     * Verifies that an answer can be successfully deleted from the database.
     *
     * <p>Test steps:
     * <ol>
     *   <li>Creates a test question and answer</li>
     *   <li>Attempts to delete the answer</li>
     *   <li>Verifies the answer was deleted successfully</li>
     * </ol>
     *
     * @return true if the answer deletion test passes, false otherwise
     * @throws SQLException if a database error occurs
     */
    public static boolean testDeleteAnswer() throws SQLException {
        String user = "testUser";
        String answer = "This is an answer.";
        String question = "What is a good question?";

        dbHelper.addAnswer(question, user, answer);
        boolean deleted = dbHelper.deleteAnswer(user, answer);
        
        List<String> answers = dbHelper.getAnswersForQuestion(question);
        return deleted && !answers.contains(user + ": " + answer);
    }
    
    /**
     * Tests the resolved answer retrieval functionality.
     * Verifies that a resolved answer can be successfully retrieved.
     *
     * <p>Test steps:
     * <ol>
     *   <li>Creates a test question and answer</li>
     *   <li>Marks the answer as resolved</li>
     *   <li>Retrieves the resolved answer</li>
     *   <li>Verifies the resolved answer matches</li>
     * </ol>
     *
     * @return true if the resolved answer retrieval test passes, false otherwise
     * @throws SQLException if a database error occurs
     */
    public static boolean testGetResolvedAnswer() throws SQLException {
        String question = "What is a good question?";
        String user = "testUser";
        String answer = "This is the resolved answer.";
        
        dbHelper.addAnswer(question, user, answer);
        dbHelper.markAnswerAsResolved(question, answer);
        
        String resolved = dbHelper.getResolvedAnswer(question);
        return resolved != null && resolved.equals(answer);
    }

    /**
     * Tests the question retrieval functionality.
     * Verifies that questions can be successfully retrieved from the database.
     *
     * <p>Test steps:
     * <ol>
     *   <li>Clears all existing questions</li>
     *   <li>Adds multiple test questions</li>
     *   <li>Retrieves all questions</li>
     *   <li>Verifies all questions are present</li>
     * </ol>
     *
     * @return true if the question retrieval test passes, false otherwise
     * @throws SQLException if a database error occurs
     */
    public static boolean getQuestion_test() throws SQLException {
        delete_all_questions();
        
        int num_found = 0;
        String user = "TEST_USER";
        List<String> test_questions = new ArrayList<>();
        
        String question1 = "Question 1";
        dbHelper.addQuestion(user, question1);
        test_questions.add(question1);
        
        String question2 = "Question 2";
        dbHelper.addQuestion(user, question2);
        test_questions.add(question2);
        
        String question3 = "Question 3";
        dbHelper.addQuestion(user, question3);
        test_questions.add(question3);
        
        List<Question> questions = dbHelper.getAllQuestions();
        
        for(int i = 0; i < questions.size(); i++) {
            if(questions.get(i).getQuestion().equals(test_questions.get(i))) {
                num_found++;
            }
        }
        return num_found == 3;
    }
}
