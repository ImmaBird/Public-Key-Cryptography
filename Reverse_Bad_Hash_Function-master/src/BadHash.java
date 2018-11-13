public class BadHash {

    public static void main(String[] args) {
        // these are the messages
        int[] test1 = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        int[] expected1 = new int[] { 0, 129, 3, 129, 7, 129, 3, 129, 15, 129, 3, 129, 7, 129, 3, 129 };
        
        int[] test2 = new int[] { 0, 1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144, 169, 196, 225 };
        int[] expected2 = new int[] { 0, 129, 5, 141, 25, 137, 61, 149, 113, 145, 53, 157, 233, 185, 109, 165 };

        int[] test3 = new int[] { 71, 111, 111, 100, 32, 74, 111, 98, 33 };
        int[] expected3 = new int[] { 199, 168, 128, 11, 68, 106, 165, 13, 195 };

        test(test1, expected1);
        test(test2, expected2);
        test(test3, expected3);
    }

    public static void test(int[] message, int[] expected) {
        int[] hash = compute(message);
        int[] reversedHash = reverse(hash);

        printArray(message);
        printArray(reversedHash);
        printArray(expected);
        printArray(hash);
        System.out.println();
    }

    public static void printArray(int[] array) {
        System.out.print("[ ");
        for (int i = 0; i < array.length - 1; i++) {
            System.out.printf("%d, ", array[i]);
        }
        System.out.printf("%d ]\n", array[array.length - 1]);
    }

    public static int[] compute(int[] message) {
        int[] digest = new int[message.length];
        for (int i = 0; i < message.length; i++) {
            digest[i] = ((129 * message[i]) ^ (i == 0 ? 0 : message[i - 1])) % 256;
        }
        return digest;
    }

    // this works because the domain and range of this function "x = (129 * x) mod 256" are reversable
    // such that f(2) = 2 and f(1) = 129 and f(129) = 1
    // or in general y = f(x) and x = f(y) where the function f stays the same
    // in this case the function f(x) = (129 * x) mod 256
    public static int[] reverse(int[] digest) {
        int[] message = new int[digest.length];
        for (int i = 0; i < digest.length; i++) {
            message[i] = (129 * (digest[i] ^ (i == 0 ? 0 : message[i - 1])) % 256);
        }
        return message;
    }

}