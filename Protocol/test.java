package Protocol;

import java.util.Random;

public class test {

    public String createFileName(String fileName, String userName) {

        // handle user name
        userName = userName.trim();

        // ramdom a string id
        Random generator = new Random();
        String id = String.valueOf(generator.nextInt(1000000));

        // handle file name: bai.tapTuan1.txt => bai.tapTuan1
        String[] file_arr = fileName.split("\\.");

        if (file_arr.length > 1) {
            fileName = file_arr[0];
            int j;
            for (j = 1; j < file_arr.length - 1; j++) {
                fileName = fileName + "." + file_arr[j];
            }
            return fileName + "_" + userName + "_" + id + "." + file_arr[j];
        } else {
            return fileName + "_" + userName + "_" + id;
        }
    }

    public static void main(String[] args) {
        test a = new test();
        String[] arr = "dhg@.@jfgbk-hjgbk".split("@.@");
        System.out.println(arr[0] + arr[1]);
        // for (int i = 0; i < 1; i++)
        //     System.out.println(a.createFileName("by.e.by.e.txt", "trang.Trinh"));
    }
}