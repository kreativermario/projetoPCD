package coordination;

public class CellSemaphore {
    private int permits = 0;

    public CellSemaphore(int permits){
        this.permits = permits;
    }

    public synchronized void acquire() throws InterruptedException{
        if(this.permits == 0) return;
        this.permits--;
    }

    public synchronized void release(){
        this.permits++;
    }

    @Override
    public String toString(){
        return "Permits: " + this.permits;
    }

}
