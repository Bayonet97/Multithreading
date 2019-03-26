import java.util.ArrayList;

public class AssignmentOne {

    public static void main(String[] args) {
        int assignmentNum = 5;

        switch (assignmentNum) {
            case 1:
                thisCausesOutOfMemoryError();
            case 2:
                thisCausesStackOverFlowError();
            case 3:
                floatTrueFalse();
            case 4:
                onOffBit();
            case 5: onOffBool();
        }
    }

    private static void thisCausesOutOfMemoryError() {
        ArrayList<String> funnyList = new ArrayList<String>();

        while(true)
            funnyList.add("ha");
    }

    private static void thisCausesStackOverFlowError() {

        thisCausesStackOverFlowError();
    }

    private static void floatTrueFalse(){

        int i = 42;
        float f = 1.0f / i;
        System.out.println((i*f==1.0f));

        i = 41;
        f = 1.0f / i;
        System.out.println((i*f==1.0f));
    }

    private static void onOffBit(){
        byte bit = 0;

        System.out.println("Version 1: ");

        for (int i = 0; i < 20; i += 2) {
            System.out.println("Value of i: " + i + ", value of bit: " + bit);
            bit = bit == 0 ? (byte) 1 : 0;
        }

        System.out.println("Version 2: ");

        bit = 0;
        for (int i = 0; i < 20; i += 2) {
            System.out.println("Value of i: " + i + ", value of bit: " + bit);
            bit = (byte)((bit * -1) + 1);
        }

        System.out.println("Version 3: ");

        bit = 0;
        for (int i = 0; i < 20; i += 2) {
            System.out.println("Value of i: " + i + ", value of bit: " + bit);
            bit ^= 1;
        }
    }

    private static void onOffBool(){
        boolean bit = false;

        System.out.println("Version 1: ");

        for (int i = 0; i < 20; i += 2) {
            System.out.println("Value of i: " + i + ", value of bit: " + bit);
            bit = !bit;
        }

        System.out.println("Version 2: ");

        bit = false;
        for (int i = 0; i < 20; i += 2) {
            System.out.println("Value of i: " + i + ", value of bit: " + bit);
            bit = (Boolean.compare(bit, true) == -1);
        }
    }
}
