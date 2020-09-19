package SunnyMPC;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }
    
    public static void main(String[] args) {
        Communicate com = new Communicate();
        com.connect("127.0.0.1", 6600);
        
        try {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
        } 
        catch (UnsupportedLookAndFeelException e) {
            System.out.println(e.getMessage());
        }
        catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch (InstantiationException e) {
            System.out.println(e.getMessage());
        }
        catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
        GUIBuilder builder = new GUIBuilder();
        builder.build();
    }
}
