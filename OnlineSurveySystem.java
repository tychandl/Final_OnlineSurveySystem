/**
 * @author: Tyrone Chandler
 * Description: Online survey system that allows the user to create a survey,
 * add open ended, multiple choice, and yes/no questions, take a survey
 * anonymously, view results, and export the results to a CSV file.
 */

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class OnlineSurveySystem {

    static ArrayList<Survey> surveys = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            String[] options = {
                    "Create Survey",
                    "Add Questions",
                    "Take Survey",
                    "View Results",
                    "Export CSV",
                    "Exit"
            };

            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Online Survey System",
                    "Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                createSurvey();
            } else if (choice == 1) {
                addQuestions();
            } else if (choice == 2) {
                takeSurvey();
            } else if (choice == 3) {
                viewResults();
            } else if (choice == 4) {
                exportCSV();
            } else {
                break;
            }
        }
    }

    static void createSurvey() {
        String title = JOptionPane.showInputDialog("Enter survey title:");
        if (title == null || title.trim().isEmpty()) {
            return;
        }

        String description = JOptionPane.showInputDialog("Enter survey description:");
        if (description == null) {
            description = "";
        }

        surveys.add(new Survey(title, description));
        JOptionPane.showMessageDialog(null, "Survey created.");
    }

    static void addQuestions() {
        Survey survey = selectSurvey();
        if (survey == null) {
            return;
        }

        while (true) {
            String[] questionTypes = {
                    "Open Ended",
                    "Multiple Choice",
                    "Yes or No",
                    "Stop Adding Questions"
            };

            int typeChoice = JOptionPane.showOptionDialog(
                    null,
                    "Choose the question type:",
                    "Question Type",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    questionTypes,
                    questionTypes[0]
            );

            if (typeChoice == 3 || typeChoice == JOptionPane.CLOSED_OPTION) {
                break;
            }

            String questionText = JOptionPane.showInputDialog("Enter the question:");
            if (questionText == null || questionText.trim().isEmpty()) {
                continue;
            }

            if (typeChoice == 0) {
                survey.questions.add(new Question(questionText, "Open Ended"));
            } else if (typeChoice == 1) {
                ArrayList<String> choices = new ArrayList<>();

                while (true) {
                    String choice = JOptionPane.showInputDialog(
                            "Enter a multiple choice option:\nLeave blank to stop."
                    );

                    if (choice == null || choice.trim().isEmpty()) {
                        break;
                    }

                    choices.add(choice);
                }

                if (choices.size() < 2) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Multiple choice questions need at least two options."
                    );
                } else {
                    survey.questions.add(new Question(questionText, "Multiple Choice", choices));
                }
            } else if (typeChoice == 2) {
                ArrayList<String> yesNoChoices = new ArrayList<>();
                yesNoChoices.add("Yes");
                yesNoChoices.add("No");

                survey.questions.add(new Question(questionText, "Yes or No", yesNoChoices));
            }
        }

        JOptionPane.showMessageDialog(null, "Questions added.");
    }

    static void takeSurvey() {
        Survey survey = selectSurvey();
        if (survey == null) {
            return;
        }

        if (survey.questions.size() == 0) {
            JOptionPane.showMessageDialog(null, "This survey has no questions.");
            return;
        }

        ArrayList<String> response = new ArrayList<>();

        for (Question question : survey.questions) {
            String answer = "";

            if (question.type.equals("Open Ended")) {
                answer = JOptionPane.showInputDialog(question.text);

                if (answer == null) {
                    answer = "";
                }
            } else {
                String[] choices = question.choices.toArray(new String[0]);

                answer = (String) JOptionPane.showInputDialog(
                        null,
                        question.text,
                        question.type,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        choices,
                        choices[0]
                );

                if (answer == null) {
                    answer = "";
                }
            }

            response.add(answer);
        }

        survey.responses.add(response);
        JOptionPane.showMessageDialog(null, "Response submitted anonymously.");
    }

    static void viewResults() {
        Survey survey = selectSurvey();
        if (survey == null) {
            return;
        }

        String result = "Survey: " + survey.title + "\n";
        result += "Description: " + survey.description + "\n\n";
        result += "Questions:\n";

        for (int i = 0; i < survey.questions.size(); i++) {
            Question question = survey.questions.get(i);

            result += (i + 1) + ". " + question.text + " ";
            result += "(" + question.type + ")\n";

            if (question.choices.size() > 0) {
                result += "Options: ";
                for (int j = 0; j < question.choices.size(); j++) {
                    result += question.choices.get(j);

                    if (j < question.choices.size() - 1) {
                        result += ", ";
                    }
                }
                result += "\n";
            }
        }

        result += "\nResponses:\n";

        if (survey.responses.size() == 0) {
            result += "No responses yet.";
        } else {
            for (int i = 0; i < survey.responses.size(); i++) {
                result += "\nResponse " + (i + 1) + ":\n";
                ArrayList<String> response = survey.responses.get(i);

                for (int j = 0; j < response.size(); j++) {
                    result += "Q" + (j + 1) + ": " + response.get(j) + "\n";
                }
            }
        }

        JTextArea textArea = new JTextArea(result, 20, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(
                null,
                scrollPane,
                "Survey Results",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    static void exportCSV() {
        Survey survey = selectSurvey();
        if (survey == null) {
            return;
        }

        if (survey.questions.size() == 0) {
            JOptionPane.showMessageDialog(null, "This survey has no questions.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(
                new File(survey.title.replaceAll("[^a-zA-Z0-9]", "_") + "_responses.csv")
        );

        int choice = fileChooser.showSaveDialog(null);

        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Response #");

                for (Question question : survey.questions) {
                    writer.write("," + csvFormat(question.text));
                }

                writer.write("\n");

                for (int i = 0; i < survey.responses.size(); i++) {
                    writer.write(String.valueOf(i + 1));

                    ArrayList<String> response = survey.responses.get(i);

                    for (String answer : response) {
                        writer.write("," + csvFormat(answer));
                    }

                    writer.write("\n");
                }

                JOptionPane.showMessageDialog(null, "CSV exported successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error exporting CSV.");
            }
        }
    }

    static String csvFormat(String text) {
        if (text == null) {
            return "\"\"";
        }

        text = text.replace("\"", "\"\"");
        return "\"" + text + "\"";
    }

    static Survey selectSurvey() {
        if (surveys.size() == 0) {
            JOptionPane.showMessageDialog(null, "No surveys available.");
            return null;
        }

        String[] surveyTitles = new String[surveys.size()];

        for (int i = 0; i < surveys.size(); i++) {
            surveyTitles[i] = surveys.get(i).title;
        }

        String selectedTitle = (String) JOptionPane.showInputDialog(
                null,
                "Select a survey:",
                "Survey List",
                JOptionPane.QUESTION_MESSAGE,
                null,
                surveyTitles,
                surveyTitles[0]
        );

        if (selectedTitle == null) {
            return null;
        }

        for (Survey survey : surveys) {
            if (survey.title.equals(selectedTitle)) {
                return survey;
            }
        }

        return null;
    }

    static class Survey {
        String title;
        String description;
        ArrayList<Question> questions = new ArrayList<>();
        ArrayList<ArrayList<String>> responses = new ArrayList<>();

        Survey(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    static class Question {
        String text;
        String type;
        ArrayList<String> choices = new ArrayList<>();

        Question(String text, String type) {
            this.text = text;
            this.type = type;
        }

        Question(String text, String type, ArrayList<String> choices) {
            this.text = text;
            this.type = type;
            this.choices = choices;
        }
    }
}
