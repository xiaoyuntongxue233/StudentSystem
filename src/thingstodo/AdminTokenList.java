package thingstodo;

import java.io.Serializable;
import java.util.ArrayList;

public class AdminTokenList implements Serializable {

    private static final long serialVersionUID = 9L;

    private ArrayList<String> tokenList = new ArrayList<>();

    public boolean addToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        String value = token.trim();
        if (contains(value)) {
            return false;
        }
        tokenList.add(value);
        return true;
    }

    public boolean removeToken(String token) {
        if (token == null) {
            return false;
        }
        return tokenList.remove(token);
    }

    public String getToken(int index) {
        if (index < 0 || index >= tokenList.size()) {
            return null;
        }
        return tokenList.get(index);
    }

    public boolean contains(String token) {
        if (token == null) {
            return false;
        }
        for (String t : tokenList) {
            if (t.equals(token)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        String key = keyword.trim();
        ArrayList<String> result = new ArrayList<>();
        for (String t : tokenList) {
            if (t.contains(key)) {
                result.add(t);
            }
        }
        return result;
    }

    public ArrayList<String> getAll() {
        return new ArrayList<>(tokenList);
    }

    public int size() {
        return tokenList.size();
    }

    public boolean isEmpty() {
        return tokenList.isEmpty();
    }

    public void clear() {
        tokenList.clear();
    }

    public void printAll() {
        for (String t : tokenList) {
            System.out.println("[AdminTokenList] " + t);
        }
    }
}
