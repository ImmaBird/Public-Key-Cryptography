
public class Stopwatch{

    private long elapsedTime;
    private long startTime;
    private boolean isRunning;

    public Stopwatch(){
        isRunning = false;
        elapsedTime = 0;
        startTime = 0;
    }

    public void Start(){
        if (!isRunning){
            startTime = System.nanoTime();
            isRunning = true;
        }
    }

    public void Stop(){
        if(isRunning){
            elapsedTime += System.nanoTime() - startTime;
            isRunning = false;
        }
    }

    public void Reset(){
        if(!isRunning){
            elapsedTime = 0;
        }
    }

    public long GetTime(){
        if (!isRunning){
            return elapsedTime;
        } else{
            return elapsedTime + System.nanoTime() - startTime;
        }
    }
}