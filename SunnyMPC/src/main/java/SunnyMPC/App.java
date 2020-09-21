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
        Runnable runnable = () -> { 
            Communicate.connect("192.168.1.87", 6600); 
        };
        Thread thread = new Thread(runnable);
        thread.start();

        ServerScan ss = new ServerScan();
     //   ss.scan();
        
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
