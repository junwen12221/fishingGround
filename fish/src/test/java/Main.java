public class Main {
    public static void main(String[] args) throws InterruptedException {

        while (true) {
            int number = new TransClass().getNumber();
            System.out.println(number);
            Thread.sleep(1000);
        }
    }
}