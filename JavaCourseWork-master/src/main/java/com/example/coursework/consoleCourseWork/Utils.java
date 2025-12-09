package com.example.coursework.consoleCourseWork;


import com.example.coursework.model.User;

import java.io.*;

public class Utils {
    public static void writeUserInfo(User user) {
        ObjectOutputStream out = null;
        try (var file = new FileOutputStream("o.txt")) {
            out = new ObjectOutputStream((new BufferedOutputStream(file)));
            out.writeObject(user);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static void writeWoltToFile(Wolt wolt) {
        ObjectOutputStream out = null;

        try {
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("database.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Wolt readWoltFromFile() {
        ObjectInputStream in = null;
        Wolt wolt = null;
        try {
            in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("database.txt")));
            wolt = (Wolt) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return wolt;
    }

}
