public class Main {
    public static void main(String[] args) {
        System.out.println(checkFriendlyNumbers(220, 284));
    }

    public static boolean checkFriendlyNumbers(int firstNumber, int secondNumber) {
        return (divisorSum(firstNumber) == secondNumber) && (divisorSum(secondNumber) == firstNumber);
    }

    public static int divisorSum(int number) {
        int sum = 0;
        for (int i = 1; i < number; i++) {
            if (number % i == 0) {
                sum += i;
            }
        }
        return sum;
    }
}
