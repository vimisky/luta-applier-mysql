package io.github.vimisky.luta.applier.mysql;

public class GenMain {
    public static void main(String[] args){
        CustomThread customThread = new CustomThread();
        customThread.start();
        customThread.interrupt();

    }
}
class CustomThread extends Thread {
    /**
     * If this thread was constructed using a separate
     * {@code Runnable} run object, then that
     * {@code Runnable} object's {@code run} method is called;
     * otherwise, this method does nothing and returns.
     * <p>
     * Subclasses of {@code Thread} should override this method.
     *
     * @see #start()
     * @see #stop()
     * @see #Thread(ThreadGroup, Runnable, String)
     */
    @Override
    public void run() {
//        super.run();
        for (int i = 0; i < 1000000000; i++) {

            System.out.print(" T" + i);
            Thread.holdsLock(null);
            if (this.isInterrupted()){
                break;
            }
        }
    }
}

