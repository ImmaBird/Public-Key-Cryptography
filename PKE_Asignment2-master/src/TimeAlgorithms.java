import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class TimeAlgorithms {
    public static void main(String[] args) {
        int n1 = 400;
        int n2 = 200;
        String password = "test123";
        Stopwatch timer = new Stopwatch();
        int size = 1024 * 100000;
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(new byte[size]);
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream2;

        try {
            /***********************************************************************
             * ENCRYPT AND THEN DECRYPT USING DES
             ***********************************************************************/
            System.out.println("---------------------DES---------------------");
            timer.Reset();
            outputStream1.reset();
            outputStream2.reset();

            DES des = new DES(password, "PBEWithMD5AndDES");

            for (int i = 0; i < n1; i++) {
                for (int j = 0; j < n2; j++) {
                    timer.Start();
                    des.encrypt(inputStream1, outputStream1);
                    timer.Stop();
                    inputStream2 = new ByteArrayInputStream(outputStream1.toByteArray());
                    timer.Start();
                    des.decrypt(inputStream2, outputStream2);
                    timer.Stop();
                    outputStream1.reset();
                    outputStream2.reset();
                }
                long averageTime = timer.GetTime() / n2;
                timer.Reset();
                System.out.printf("DES(nsec): %d\n", averageTime);
            }

            /***********************************************************************
             * ENCRYPT AND THEN DECRYPT USING Triple 3DES
             ***********************************************************************/
            System.out.println("---------------------3DES---------------------");
            timer.Reset();
            outputStream1.reset();
            outputStream2.reset();

            DES des3 = new DES(password, "PBEWithMD5AndTripleDES");

            for (int i = 0; i < n1; i++) {
                for (int j = 0; j < n2; j++) {
                    timer.Start();
                    des3.encrypt(inputStream1, outputStream1);
                    timer.Stop();
                    inputStream2 = new ByteArrayInputStream(outputStream1.toByteArray());
                    timer.Start();
                    des3.decrypt(inputStream2, outputStream2);
                    timer.Stop();
                    outputStream1.reset();
                    outputStream2.reset();
                }
                long averageTime = timer.GetTime() / n2;
                timer.Reset();
                System.out.printf("3DES(nsec): %d\n", averageTime);
            }

            /***********************************************************************
             * ENCRYPT AND THEN DECRYPT USING AES
             ***********************************************************************/
            System.out.println("---------------------AES---------------------");
            timer.Reset();
            outputStream1.reset();
            outputStream2.reset();

            AES aes = new AES(password);

            for (int i = 0; i < n1; i++) {
                for (int j = 0; j < n2; j++) {
                    timer.Start();
                    aes.encrypt(inputStream1, outputStream1);
                    timer.Stop();
                    inputStream2 = new ByteArrayInputStream(outputStream1.toByteArray());
                    timer.Start();
                    aes.decrypt(inputStream2, outputStream2);
                    timer.Stop();
                    outputStream1.reset();
                    outputStream2.reset();
                }
                long averageTime = timer.GetTime() / n2;
                timer.Reset();
                System.out.printf("AES(nsec): %d\n", averageTime);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
