package coordination;

/**
 * CountDownLatch utilizado para coordenacao de fim de jogo
 */
public class FinishCountDownLatch{
    private int count ;

    /**
     * Metodo construtor
     * @param count int
     */
    public FinishCountDownLatch(int count){
        this.count = count ;
    }

    /**
     * Metodo de espera
     * @throws InterruptedException
     */
    public synchronized void await() throws InterruptedException{
        while(count>0){
            wait();
        }
    }

    /**
     * Metodo que faz countdown da latch
     */
    public synchronized void countDown(){
        count--;
        if(count==0){
            notifyAll();
        }
    }


}
