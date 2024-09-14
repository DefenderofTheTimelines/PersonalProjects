public class Timer
{
    private static final long SNS = 1000000000; //Conversion factor to get to/from nanoseconds
    private long lastTime;

    public Timer()
    {
        this.lastTime = System.nanoTime();
    }

    ///Returns elapsed time in seconds and resets the timer
    public double getElapsedTime()
    {
        long curTime = System.nanoTime();
        double t = (double)(curTime - lastTime) / (double)SNS;
        lastTime = curTime;
        return t;
    }
}
