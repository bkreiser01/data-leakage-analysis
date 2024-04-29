package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is meant to represent Strings of the form "$invo1" that are present in the CSV files of the leakage anaylsis tool.
 */
public class Invocation {

    private final String pyCallExpression;
    private final int number;

    /**
     * Checks whether a String is of the form "$invo{number}". If the String is of this form, the {@link  #number} field is
     * set accordingly. If the String is not of the form "$invo{number}", the {@link #number} field is set to 0.
     *
     * @param invocationString A string believed to be of the form "$invo{number}".
     */
    public Invocation(String invocationString) {

        //TODO: test
        Pattern invocationPattern = Pattern.compile("(\\$)(invo)([0-9]+)");
        Matcher matcher = invocationPattern.matcher(invocationString);
        boolean matchFound = matcher.find();

        if (matchFound) {
            String numbersPortion = matcher.group(3);
            number = Integer.parseInt(numbersPortion);

        } else {
            number = 0;
        }
        this.pyCallExpression = getFunctionCallFromInvocation();
    }

    @Override
    public boolean equals(Object obj) {
        return this.number == ((Invocation) obj).number;
    }

    public int getNumber() {
        return number;
    }

    /**
     * Looks through "InvokeLineno.facts" to find a number that corresponds to a particular invocation.
     * The "internal line number" is provided by the leakage analysis tool and shall not be presented to the end user.
     *
     * @param folderPath The location of the leakage analysis tool output that contains "InvokeLineno.facts".
     * @param invocation An {@code Invocation}. This would appear in the leakage analysis tool output as "$invo2", for example.
     * @return An {@code int} representing a number provided by the leakage analysis tool. The number is meaningless to the end user, but may be used by other functions in the plugin code.
     */
    public static int getInternalLineNumberFromInvocation(String folderPath, Invocation invocation) {
        String filePath = Paths.get(folderPath).resolve("InvokeLineno.facts").toString();
//        File file = new File(folderPath + "InvokeLineno.facts");
        File file = new File(filePath);
        try {
            int lineNumber = invocation.getNumber() + 1;
            String[] columns = readFromFileUntilLineNumber(file, lineNumber);

            return Integer.parseInt(columns[1]);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;

    }

    private static String[] readFromFileUntilLineNumber(File file, int lineNumber) throws IOException {
        String line = "";

        BufferedReader reader = new BufferedReader(new FileReader(file));

        for (int i = 1; i <= lineNumber; i++) {
            line = reader.readLine();

        }
        reader.close();

        String[] columns = line.split(("\t"));
        return columns;
    }

    private String getFunctionCallFromInvocation() {
        String filePath = Paths.get(LeakageResult.getFolderPath()).resolve("Invoke.facts").toString();
//        File file = new File(LeakageResult.getFolderPath() + "Invoke.facts");
        File file = new File(filePath);
        try {
            int lineNumber = this.getNumber() + 1;
            String[] columns = readFromFileUntilLineNumber(file, lineNumber);

            var functionCall = columns[1];
            if (functionCall.contains(".")) {
                return functionCall.split("\\.")[1];
            }

            return columns[1];

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPyCallExpression() {
        return pyCallExpression;
    }
}
