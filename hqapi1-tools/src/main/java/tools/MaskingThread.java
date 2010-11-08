/**
 * Adapted from Sun's password masking exaples:
 * http://java.sun.com/developer/technicalArticles/Security/pwordmask/
 */
package org.hyperic.hq.hqapi1.tools;

/**
 * This class attempts to erase characters echoed to the console.
 */

class MaskingThread extends Thread {
    private volatile boolean stop;
    private char echochar = '*';

    /**
     * @param prompt The prompt displayed to the user
     */
    public MaskingThread(String prompt) {
        System.out.print(prompt);
    }

    /**
     * Begin masking until asked to stop.
     */
    public void run() {

        int priority = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        try {
            stop = true;
            while (stop) {
                System.out.print("\010" + echochar);
                try {
                    // attempt masking at this rate
                    Thread.currentThread().sleep(1);
                } catch (InterruptedException iex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        } finally { // restore the original priority
            Thread.currentThread().setPriority(priority);
        }
    }

    /**
     * Instruct the thread to stop masking.
     */
    public void stopMasking() {
        this.stop = false;
    }
}

