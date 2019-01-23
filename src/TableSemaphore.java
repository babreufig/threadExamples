import java.util.concurrent.Semaphore;

public class TableSemaphore {
    private Elem[][] table;
    private int lines;
    private int columns;
    private Semaphore[] semArr;

    public TableSemaphore(int line, int column){
        this.lines = (line < 1) ? 1 : line;
        this.columns = (column < 1) ? 1 : column;
        this.table = new Elem[this.lines][columns];
        semArr = new Semaphore[this.lines];
        for(int i = 0; i < semArr.length; i++) {
            semArr[i] = new Semaphore(1);
            for(int j = 0; j < column; j++) {
                table[i][j] = new Elem(i, j);
            }
        }
    }
    public int lines() {return this.lines;}
    public int columns() {return this.columns;}
    public Elem readElem(int line, int col) throws InterruptedException {
        if (line < 0 || line >= this.lines || col < 0 || col >= this.columns)
            return null;
        table[line][col].startRead();
        this.table[line][col].incReadCount();
        table[line][col].stopRead();
        return this.table[line][col];
    }
    public Elem setElem(Elem e,int line,int col) throws InterruptedException {
        if (line < 0 || line >= this.lines || col < 0 || col >= this.columns)
            return null;
        semArr[line].acquire();
        table[line][col].startRead();
        Elem current = this.table[line][col];

        e.startRead();
        this.table[line][col] = e;
        e.stopRead();

        table[line][col].stopRead();
        semArr[line].release();
        return current;
    }
}