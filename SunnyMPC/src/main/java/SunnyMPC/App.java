package SunnyMPC;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class App {
    private App() {
    }
    
    public static void main(String[] args) {
        try {
        UIManager.setLookAndFeel(
            // set UI to reflect the system settings, works rather well for GTK at least
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
        // build window
        GUIBuilder builder = new GUIBuilder();
        builder.build();
    }
}
