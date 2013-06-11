package compression;

import java.util.Arrays;

/**
 *
 * This class contains methods for performing a Haar wavelet transform on an
 * array of data and retrieving the original data from the transform. Some of
 * the methods are particular to image data, and some are more general.
 */

public class HaarTransform {

    
    /**
     * Creates a binary sum tree whose depth is predetermined. It is assumed
     * that the length of the data-array is 2^n for some n. LeafSize tells how
     * many entries of the data-array is summed to be one leaf of the resulting 
     * tree. 
     * @param leafSize The size of leaves in the resulting array. Must be 2^n
     * for some n=0,1,2,..,log_2(data.size).
     * @return The tree with the leaves as first entries, their parents after it
     * and so on, until the root is the last entry.
     */
    public static int[] sumTree(byte[] data, int leafSize){
        
        int leaves = data.length / leafSize;
        int[] tree = new int[2*leaves -1];
        
        // Create the leaves:
        for (int leaf = 0; leaf < leaves; leaf++) {
            for (int i = 0; i < leafSize; i++) {
                tree[leaf] += data[leafSize * leaf +i];
            }
        }
        
        // And the rest of the tree:
        int pointer = leaves; // points to the start of new level in the tree
        for (int i = leaves/2; i >= 1; i = i/2) { // Write one level at time
            for (int j = 0; j < i; j++) {
                tree[pointer+j] = tree[pointer -2*i + 2*j] + tree[pointer -2*i + 2*j+1];;
            }
            pointer += i;
        }
        
        return tree;
    }
 
    public static int[] lossyTransformPowerOfTwo(byte[] data, int levelOfLoss){
        
        int[] sumTree = sumTree(data, pow(2,levelOfLoss));
        int[] transform = new int[(sumTree.length +1)/2];
        
        for (int i = 0; i < transform.length - 1; i++) {
            transform[i] = sumTree[2*i] - sumTree[2*i+1];
        }
        
        // The last entry is the sum of all the data:
        transform[transform.length - 1] = sumTree[sumTree.length -1];
        
        return transform;
    }
    
    /**
     * Performs a transform on the data array. The
     * parameter levelOfLoss tells how much data is lost. If it's 0, the
     * transform is lossless. For 1 the finest level of coefficients will be
     * omitted, for 2 the second finest too etc. 
     * 
     * The data is split into subarrays whose lengths are powers of two and each
     * of them has the maximum length that can be chosen of the remaining array-
     * Then the method lossyTransformPowerOfTwo is applied to the subarrays. The
     * results are given back glued together in the same order in one array.
     *
     * @param data The data to be transformed.
     * @param levelOfLoss How many levels of the coefficients will be omitted.
     * @return The transform.
     */
    public static int[] lossyTransformArbitraryLength(byte[] data, int levelOfLoss) {
        int subSize = supPowerOfTwo(data.length);    // the biggest power of two that can be fitted in the remaingn data length
        int remainingSize = data.length;             // The length of data yet to be transformed.
        int[] transform = null;                       // The result of the transform
        int pointer = 0;                             // Where the untransformed data begins

        // This will create a transform of the biggest array of length 2^n that fits inside 
        // the untransformed part of the data array. It is put at the end of the transform array.
        while (remainingSize > 1 && subSize > 1) {
            byte[] subArray = Arrays.copyOfRange(data, pointer, pointer + subSize);
            int[] partialTransform = lossyTransformPowerOfTwo(subArray, levelOfLoss);
            transform = joinArrays(transform, partialTransform);
            pointer += subSize;
            remainingSize -= subSize;
            subSize = supPowerOfTwo(remainingSize);
        }

        // If the last number is not transformed, it is copied:
        if (remainingSize == 1) {
            transform[transform.length - 1] = data[data.length - 1];
        }

        return transform;
    }
    
    /**
     * Performs a transform on a 3D data array. The
     * parameter levelOfLoss tells how much data is lost. If it's 0, the
     * transform is lossless. For 1 the finest level of coefficients will be
     * omitted, for 2 the second finest too etc. 
     *
     * @param data The data to be transformed.
     * @param levelOfLoss How many levels of the coefficients will be omitted.
     * @return The transform.
     */
    public static int[][][] lossyTransfrom(byte[][][] data, int levelOfLoss) {
        int dim0 = data.length;
        int dim1 = data[0].length;
        int dim2 = data[0][0].length;
        int[][][] transform = new int[dim0][dim1][dim2];

        for (int i = 0; i < dim0; i++) {
            for (int j = 0; j < dim1; j++) {
                transform[i][j] = lossyTransformArbitraryLength(data[i][j], levelOfLoss);
            }
        }

        return transform;
    }
    
    /**
     * An auxiliary method to calculate the inverses of a transform. Converts
     * integers to bytes, but first adds addThis to each of them, and divides by
     * divideByThis.
     *
     * @param data The integers to be manipulated.
     * @param addThis The constant that is added to the integers.
     * @param divideByThis The constant by which each integer is divided.
     * @return The manipulated data.
     */
    public static byte[] addDivideConvert(int[] data, int addThis, int divideByThis) {
        byte[] d = new byte[data.length];
        for (int i = 0; i < d.length; i++) {
            d[i] = (byte) ((data[i] + addThis) / divideByThis);
        }
        return d;
    }

    /**
     * Retrieves the (approximate) original data from a lossy transform. It is
     * assumed that the transform is of the form produced by the method lossyTransform.
     * 
     * @param transform The transform to be inverted.
     * @param originalHeight The height of the original picture, or more generally the length
     * of the compressed line before the compression.
     * @param levelOfLoss How many levels of coefficients were lost in the transform.
     * @return The (approximate) original data.
     */
    public static byte[][][] inverseLossyTransform(int[][][] transform, int originalHeight, int levelOfLoss) {

        int dim0 = transform.length;
        int dim1 = transform[0].length;
        int dim2 = originalHeight; // This isn't evident from the size of transform[0][0].
        byte[][][] inverse = new byte[dim0][dim1][dim2];

        for (int i = 0; i < dim0; i++) {
            for (int j = 0; j < dim1; j++) {
                inverse[i][j] = inverseLossyArbitraryLength(transform[i][j], originalHeight, levelOfLoss);
            }
        }

        return inverse;

    }

    /**
     * Retrieves the (approximate) original data of a lossy transform. It is assumed
     * that the original data's length was a power of two (and consequently the tranform's
     * length is also).
     * @param transform The transform.
     * @param levelOfLoss How many levels of coefficients were lost in the transform.
     * @return The (approximate) original data.
     */
    public static byte[] inverseLossyPowerOfTwo(int[] transform, int levelOfLoss) {

        if (transform.length == 1) {
            byte[] result = {(byte) transform[0]};
            return result;
        }

        int size = transform.length;
        int originalSize = transform.length * pow(2, levelOfLoss);
        int[] inverse = new int[originalSize];

        // This goes like the transform in the lossless case (see the method
        // inversePowerOfTwo), with the exception that the steps are bigger.
        int pointer = 0; // 
        for (int howMany = size / 2; howMany >= 1; howMany >>= 1) {
            for (int theSummed = 0; theSummed < howMany; theSummed++) {
                int step = (originalSize) / (2 * howMany);    // On how many number the summed wavelet has the value 1...
                for (int i = 0; i < step; i++) { // ...first we'll sum over that interval...
                    inverse[theSummed * 2 * step + i] += transform[pointer + theSummed] * howMany;
                }
                for (int i = 0; i < step; i++) { // ...and then the latter, with thw wavelet value -1.
                    inverse[theSummed * 2 * step + step + i] -= transform[pointer + theSummed] * howMany;
                }
            }
            pointer += howMany;
        }

        // The factor howMany at the end of the sums is because we didn't normalize the wavelets when doing the transform.

        return addDivideConvert(inverse, transform[transform.length - 1], originalSize);
    }

    /**
     * Retrieves the (approximate) original data of a lossy transform. It is assumed 
     * that the transform is of the form produced by the method lossyTransformArbitraryLength.
     * 
     * @param transform The transform.
     * @parma originalSize The size of the data array before the compression.
     * @param levelOfLoss How many levels of coefficients were lost in the transform.
     * @return The (approximate) original data.
     */
    public static byte[] inverseLossyArbitraryLength(int[] transform, int originalSize, int levelOfLoss) {

        int subSize = supPowerOfTwo(originalSize);
        int remainingSize = originalSize;
        byte[] inverse = new byte[originalSize];
        int readPointer = 0; // Points to the data that is read.
        int writePointer = 0; // Points to the place where next data will be written.

        while (remainingSize > 1) {
            int keptSize = Math.max(1, subSize / pow(2, levelOfLoss)); // Tells how much of the original data was kept
            byte[] partialInverse = inverseLossyPowerOfTwo(Arrays.copyOfRange(transform, readPointer, readPointer + keptSize), levelOfLoss);
            copyValuesTo(partialInverse, inverse, writePointer);
            readPointer += keptSize;
            writePointer += subSize;
            remainingSize -= subSize;
            subSize = supPowerOfTwo(remainingSize);
        }

        if (remainingSize == 1) {
            inverse[inverse.length - 1] = (byte) transform[transform.length - 1];
        }

        return inverse;
    }
    
    /**
     * An auxiliary method that raises an integer to the power of another. 
     * The power must be nonnegative.
     *
     * @param theNumber The number whose power will be calculated
     * @param power The power to which theNumber will be raised.
     * @return The result: theNumber^power.
     */
    public static int pow(int theNumber, int power) {
        int result = 1;
        for (int i = 0; i < power; i++) {
            result *= theNumber;
        }
        return result;
    }

    /**
     * An auxiliary method that joins two arrays together. 
     * @param array1 The first array to be joined.
     * @param array2 The second array to be joined.
     * @return The two arrays joined as (array1, array2).
     */
    public static int[] joinArrays(int[] array1, int[] array2) {
        if (array1 == null) {
            return array2;
        }
        
        if (array2 == null){
            return array1;
        }

        int length = array1.length + array2.length;
        int[] joined = new int[length];
        
        // An altervative way, takes as long as the currently used.
//        for (int i = 0; i < array1.length; i++) {
//            joined[i] = array1[i];
//        }
//        for (int i = 0; i < array2.length; i++) {
//            joined[array1.length + i] = array2[i];
//        }
        
        System.arraycopy(array1, 0, joined, 0, array1.length);
        System.arraycopy(array2, 0, joined, array1.length, array2.length);
        
        
        return joined;
    }

     /**
     * An auxiliary method that copies an array of integers into another array.
     *
     * @param copyThese The array whose values will be copied to the other one.
     * @param copyHere The array where they will be copied.
     * @param startingIndex The first index of the object array that is written
     * on.
     */
    public static void copyValuesTo(int[] copyThese, int[] copyHere, int startingIndex) {
        for (int i = 0; i < copyThese.length; i++) {
            copyHere[startingIndex + i] = copyThese[i];
        }
    }

    /**
     * An auxiliary method that copies an array of bytes into another array.
     *
     * @param copyThese The array whose values will be copied to the other one.
     * @param copyHere The array where they will be copied.
     * @param startingIndex The first index of the object array that is written
     * on.
     */
    public static void copyValuesTo(byte[] copyThese, byte[] copyHere, int startingIndex) {
        for (int i = 0; i < copyThese.length; i++) {
            copyHere[startingIndex + i] = copyThese[i];
        }
    }

    /**
     * Returns the greatest number that is a power of two and less than n.
     *
     * @param n A nonnegative integer.
     * @return sup{ 2^k : 2^k <= n}.
     */
    public static int supPowerOfTwo(int n) {
        int sup = 1;
        while (sup <= n) {
            sup *= 2;
        }
        return sup / 2;
    }

}
