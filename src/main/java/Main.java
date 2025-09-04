import matrix.MatrixMultiplication;

public class Main {
    public static void main(String[] args) {
        MatrixMultiplication m = new MatrixMultiplication(1024, 6834723);
        m.multiplySequential();
//        m.multiplyParallel(8);
    }
}