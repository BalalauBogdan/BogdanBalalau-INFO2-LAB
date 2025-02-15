public class Main {
    public static void main(String[] args) {
        System.out.println(customString("hEILoWoRLd"));
    }

    public static String customString(String inputString) {
        String newString = "";
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);
            if (c >= 'a' && c <= 'z') {
                newString += c;
            }
        }
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                newString += c;
            }
        }
        return newString;
    }
}
