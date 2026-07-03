package app.service.impl.anomalies;

import app.model.AnomalyResult;
import app.model.DataRecord;
import app.service.AnomaliesDetectionService;
import smile.anomaly.IsolationForest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quantum-inspired anomaly detection service.
 *
 * <p>The public API is intentionally identical to the classical implementation:
 * {@link #trainIsolationForest(List, int)} still receives historical {@link DataRecord}
 * values and returns a Smile {@link IsolationForest}; {@link #searchForAnomaly(IsolationForest, List, double)}
 * still receives that model, the target records and the UI threshold.</p>
 *
 * <p>The difference is the representation used internally. Before fitting/scoring, each OHLCV row is encoded
 * into a simulated 5-qubit quantum feature map:</p>
 *
 * <ol>
 *     <li>Open, High, Low, Close and Volume are standardized against the training distribution.</li>
 *     <li>Each standardized feature is angle-encoded into a qubit rotation.</li>
 *     <li>A small variational-like circuit is simulated with Ry rotations, controlled-phase entanglement and
 *     a second mixing layer.</li>
 *     <li>The 32 computational-basis measurement probabilities are used as the feature vector for Isolation Forest.</li>
 * </ol>
 *
 * <p>This is a local simulator, not a call to quantum hardware. It is useful when the project must stay dependency-free
 * and compatible with the existing controller/factory, while still using quantum-style angle encoding, superposition,
 * entanglement phases and interference before the isolation step.</p>
 */
public class QuantumAnomaliesDetectionService implements AnomaliesDetectionService {

    private static final int ORIGINAL_FEATURE_COUNT = 5;
    private static final int QUBITS = ORIGINAL_FEATURE_COUNT;
    private static final int QUANTUM_STATE_SIZE = 1 << QUBITS;
    private static final double EPSILON = 1.0e-12;
    private static final String[] FEATURE_NAMES = {"Open", "High", "Low", "Close", "Volume"};

    /**
     * Keeps the scaler and training context associated with each trained forest.
     * The existing interface only passes the IsolationForest back to searchForAnomaly, so this map preserves
     * the quantum preprocessing state required to encode future rows consistently.
     */
    private final Map<IsolationForest, QuantumTrainingContext> trainingContextMap = new HashMap<>();

    /**
     * Trains an Isolation Forest over simulated quantum measurement probabilities instead of raw OHLCV values.
     *
     * @param data historical records used as normal baseline
     * @param treesNumber number of trees to build during the anomaly detection process
     * @return trained IsolationForest over quantum-encoded records
     */
    @Override
    public IsolationForest trainIsolationForest(List<DataRecord> data, int treesNumber) {
        validateTrainingData(data);

        QuantumTrainingContext context = QuantumTrainingContext.from(data);
        double[][] quantumEncodedTrainingData = context.encode(parseData(data));

        double sampleRate = Math.min(0.9, 256.0 / data.size());
        int maxDepth = Math.max(1, (int) Math.ceil(Math.log(data.size() * sampleRate) / Math.log(2)));

        IsolationForest isolationForest = IsolationForest.fit(
                quantumEncodedTrainingData,
                treesNumber,
                maxDepth,
                sampleRate,
                1
        );

        trainingContextMap.put(isolationForest, context);

        return isolationForest;
    }

    /**
     * Scores target records through the same quantum feature map used during training.
     *
     * <p>The method returns one {@link AnomalyResult} per input record, preserving the current GUI behavior where
     * the threshold is drawn as a chart marker and the full score series is displayed.</p>
     *
     * @param isolationForest model returned by {@link #trainIsolationForest(List, int)}
     * @param data target records to score
     * @param threshold UI threshold; validated to keep API semantics, but not used to remove chart points
     * @return scored records with feature contribution percentages
     */
    @Override
    public List<AnomalyResult> searchForAnomaly(IsolationForest isolationForest, List<DataRecord> data, double threshold) {
        if (isolationForest == null)
            throw new IllegalArgumentException("IsolationForest cannot be null.");

        if (!Double.isFinite(threshold) || threshold <= 0 || threshold >= 1)
            throw new IllegalArgumentException("Threshold must be a finite number between 0 and 1.");

        QuantumTrainingContext context = trainingContextMap.get(isolationForest);
        if (context == null)
            throw new IllegalArgumentException("The provided IsolationForest was not trained by QuantumAnomaliesDetectionService.");

        if (data == null || data.isEmpty())
            return List.of();

        double[][] rawTargetData = parseData(data);
        double[][] quantumEncodedTargetData = context.encode(rawTargetData);

        List<AnomalyResult> results = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            double score = isolationForest.score(quantumEncodedTargetData[i]);

            results.add(new AnomalyResult(
                    data.get(i),
                    score,
                    calculateContributions(isolationForest, rawTargetData[i], context)
            ));
        }

        return results;
    }

    /**
     * Estimates local feature contributions in the original OHLCV space.
     * Each feature is injected one at a time into an average baseline record, then the modified record is passed
     * through the quantum encoder and scored by the forest.
     */
    private Map<String, Double> calculateContributions(IsolationForest isolationForest,
                                                       double[] rawRecord,
                                                       QuantumTrainingContext context) {
        double[] baseline = context.meanRawValues().clone();
        double baselineScore = isolationForest.score(context.encode(baseline));

        double[] rawImpacts = new double[ORIGINAL_FEATURE_COUNT];
        double totalImpact = 0.0;

        for (int i = 0; i < ORIGINAL_FEATURE_COUNT; i++) {
            double[] testPoint = baseline.clone();
            testPoint[i] = rawRecord[i];

            double scoreWithFeature = isolationForest.score(context.encode(testPoint));
            rawImpacts[i] = Math.abs(scoreWithFeature - baselineScore);
            totalImpact += rawImpacts[i];
        }

        Map<String, Double> contributions = new HashMap<>();
        for (int i = 0; i < ORIGINAL_FEATURE_COUNT; i++) {
            double percentage = totalImpact > EPSILON ? (rawImpacts[i] / totalImpact) * 100.0 : 0.0;
            contributions.put(FEATURE_NAMES[i], percentage);
        }

        return contributions;
    }

    private static void validateTrainingData(List<DataRecord> data) {
        if (data == null || data.isEmpty())
            throw new IllegalArgumentException("Training data cannot be empty.");
    }

    private static double[][] parseData(List<DataRecord> data) {
        double[][] parsedData = new double[data.size()][ORIGINAL_FEATURE_COUNT];

        for (int i = 0; i < data.size(); i++) {
            parsedData[i][0] = data.get(i).getOpen();
            parsedData[i][1] = data.get(i).getHigh();
            parsedData[i][2] = data.get(i).getLow();
            parsedData[i][3] = data.get(i).getClose();
            parsedData[i][4] = data.get(i).getVolume();
        }

        return parsedData;
    }

    /**
     * Immutable preprocessing state for a trained quantum feature map.
     */
    private record QuantumTrainingContext(double[] means, double[] standardDeviations) {

        static QuantumTrainingContext from(List<DataRecord> data) {
            double[][] rawData = parseData(data);
            double[] means = calculateMeans(rawData);
            double[] standardDeviations = calculateStandardDeviations(rawData, means);

            return new QuantumTrainingContext(means, standardDeviations);
        }

        double[] meanRawValues() {
            return means.clone();
        }

        double[][] encode(double[][] rawRows) {
            double[][] encoded = new double[rawRows.length][QUANTUM_STATE_SIZE];

            for (int i = 0; i < rawRows.length; i++)
                encoded[i] = encode(rawRows[i]);

            return encoded;
        }

        double[] encode(double[] rawRow) {
            double[] angles = toQuantumAngles(rawRow);
            return simulateCircuitAndMeasure(angles);
        }

        /**
         * Maps standardized OHLCV values to [0, PI] rotation angles.
         * tanh keeps extreme market moves finite while preserving their direction and relative magnitude.
         */
        private double[] toQuantumAngles(double[] rawRow) {
            double[] angles = new double[QUBITS];

            for (int i = 0; i < QUBITS; i++) {
                double zScore = (rawRow[i] - means[i]) / Math.max(standardDeviations[i], EPSILON);
                double squashed = Math.tanh(zScore);
                angles[i] = (squashed + 1.0) * Math.PI / 2.0;
            }

            return angles;
        }

        /**
         * Simulates a compact quantum feature map:
         * |00000> -> Ry(angle_i) -> all-pairs controlled phase -> Ry(mixed angles) -> measurement probabilities.
         */
        private double[] simulateCircuitAndMeasure(double[] angles) {
            double[] real = new double[QUANTUM_STATE_SIZE];
            double[] imaginary = new double[QUANTUM_STATE_SIZE];
            real[0] = 1.0;

            for (int qubit = 0; qubit < QUBITS; qubit++)
                applyRy(real, imaginary, qubit, angles[qubit]);

            for (int first = 0; first < QUBITS; first++) {
                for (int second = first + 1; second < QUBITS; second++) {
                    double phase = (angles[first] * angles[second]) / Math.PI;
                    applyControlledPhase(real, imaginary, first, second, phase);
                }
            }

            for (int qubit = 0; qubit < QUBITS; qubit++) {
                double mixedAngle = (angles[qubit] + angles[(qubit + 1) % QUBITS]) / 2.0;
                applyRy(real, imaginary, qubit, mixedAngle);
            }

            double[] probabilities = new double[QUANTUM_STATE_SIZE];
            double probabilitySum = 0.0;

            for (int basisState = 0; basisState < QUANTUM_STATE_SIZE; basisState++) {
                probabilities[basisState] = real[basisState] * real[basisState]
                        + imaginary[basisState] * imaginary[basisState];
                probabilitySum += probabilities[basisState];
            }

            if (probabilitySum > EPSILON) {
                for (int basisState = 0; basisState < QUANTUM_STATE_SIZE; basisState++)
                    probabilities[basisState] /= probabilitySum;
            }

            return probabilities;
        }

        private static double[] calculateMeans(double[][] rawData) {
            double[] means = new double[ORIGINAL_FEATURE_COUNT];

            for (double[] rawRow : rawData) {
                for (int i = 0; i < ORIGINAL_FEATURE_COUNT; i++)
                    means[i] += rawRow[i];
            }

            for (int i = 0; i < ORIGINAL_FEATURE_COUNT; i++)
                means[i] /= rawData.length;

            return means;
        }

        private static double[] calculateStandardDeviations(double[][] rawData, double[] means) {
            double[] variances = new double[ORIGINAL_FEATURE_COUNT];

            for (double[] rawRow : rawData) {
                for (int i = 0; i < ORIGINAL_FEATURE_COUNT; i++) {
                    double deviation = rawRow[i] - means[i];
                    variances[i] += deviation * deviation;
                }
            }

            for (int i = 0; i < ORIGINAL_FEATURE_COUNT; i++)
                variances[i] = Math.sqrt(variances[i] / rawData.length);

            return variances;
        }

        private static void applyRy(double[] real, double[] imaginary, int qubit, double angle) {
            double cos = Math.cos(angle / 2.0);
            double sin = Math.sin(angle / 2.0);
            int mask = 1 << qubit;

            for (int basisState = 0; basisState < QUANTUM_STATE_SIZE; basisState++) {
                if ((basisState & mask) == 0) {
                    int pairedState = basisState | mask;

                    double realZero = real[basisState];
                    double imaginaryZero = imaginary[basisState];
                    double realOne = real[pairedState];
                    double imaginaryOne = imaginary[pairedState];

                    real[basisState] = cos * realZero - sin * realOne;
                    imaginary[basisState] = cos * imaginaryZero - sin * imaginaryOne;
                    real[pairedState] = sin * realZero + cos * realOne;
                    imaginary[pairedState] = sin * imaginaryZero + cos * imaginaryOne;
                }
            }
        }

        private static void applyControlledPhase(double[] real,
                                                 double[] imaginary,
                                                 int controlQubit,
                                                 int targetQubit,
                                                 double phase) {
            double cos = Math.cos(phase);
            double sin = Math.sin(phase);
            int controlMask = 1 << controlQubit;
            int targetMask = 1 << targetQubit;

            for (int basisState = 0; basisState < QUANTUM_STATE_SIZE; basisState++) {
                if ((basisState & controlMask) != 0 && (basisState & targetMask) != 0) {
                    double currentReal = real[basisState];
                    double currentImaginary = imaginary[basisState];

                    real[basisState] = currentReal * cos - currentImaginary * sin;
                    imaginary[basisState] = currentReal * sin + currentImaginary * cos;
                }
            }
        }
    }
}
