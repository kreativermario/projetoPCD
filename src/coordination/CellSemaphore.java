package coordination;

public class CellSemaphore {
    private int permits = 0;

    public CellSemaphore(int permits){
        this.permits = permits;
    }

    public synchronized void acquire() throws InterruptedException{
        while(this.permits == 0)
            wait();
        this.permits--;
    }

    public synchronized void release(){
        this.permits++;
        notifyAll();
    }

    @Override
    public String toString(){
        return "Permits: " + this.permits;
    }

}
