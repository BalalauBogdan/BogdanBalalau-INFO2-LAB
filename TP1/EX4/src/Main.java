public class Main {
    public static void main(String[] args) {
        System.out.println(countValleys("UDDDUDUU"));
    }

    public static int countValleys(String path) {
        int seaLevel = 0;
        int valleys = 0;
        int currentLevel = 0;

        for (char step : path.toCharArray()) {
            if (step == 'U') {
                currentLevel++;
            } else if (step == 'D') {
                currentLevel--;
            }

            if (currentLevel == seaLevel && step == 'U') {
                valleys++;
            }
        }

        return valleys;
    }
}
