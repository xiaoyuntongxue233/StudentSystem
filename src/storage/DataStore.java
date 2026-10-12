package storage;

import java.io.*;

public class DataStore {

    private static final String DIR = "data";

    public static void save(String fileName, Object obj) {
        File dir = new File(DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.err.println("[DataStore] 保存失败: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T load(String fileName) {
        File file = new File(DIR, fileName);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[DataStore] 读取失败: " + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
    }
}