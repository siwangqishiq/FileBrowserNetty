package xyz.panyi.browser;

public class Main {
    public static final int PORT = 8910;

    public static void main(String args[]) throws InterruptedException {
        LogUtil.log("browser server start...");
        new FileServer(PORT).runServer();
        LogUtil.log("browser server end...");
    }
}//end class
