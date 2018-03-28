import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Tongtong on 3/27/18.
 */
public class MaxFeeTxHandler extends TxHandler {
    public UTXOPool utxoPool;

    public MaxFeeTxHandler(UTXOPool utxoPool) {
        super(utxoPool);
    }

    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        List<Transaction> transactionList = Arrays.asList(possibleTxs);
        Collections.sort(transactionList, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                return Double.compare(calculateMaxValues(o2), calculateMaxValues(o1));
            }
        });
        return super.handleTxs(transactionList.stream().toArray(Transaction[]::new));
    }

    private double calculateMaxValues(Transaction tx) {
        double totalInput = 0, totalOutput = 0;
        for (Transaction.Input in : tx.getInputs()) {
            UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
            if (!utxoPool.contains(utxo) || !isValidTx(tx)) {
                continue;
            }
            Transaction.Output out = utxoPool.getTxOutput(utxo);
            totalInput += out.value;
        }

        for (Transaction.Output out : tx.getOutputs()) {
            totalOutput += out.value;
        }
        return totalInput - totalOutput;
    }
}
