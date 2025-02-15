public class Main {
    public static void main(String[] args) {
        System.out.println(hexToInt("1A3"));
    }

    public static int hexToInt(String number) {
        return Integer.parseInt(number, 16);
    }
}
