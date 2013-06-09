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
     * Creates a binary tree of the sums of the data. It is assumed that s :=
     * data.length is a power of 2. The resulting tree is returned as an array
     * with leaves as the first s/2 elements, their parents as the next s/4
     * elements etc. The last element will be the root, i.e. the sum of all
     * the data. The length of the returned array is one less than the length of
     * the data array.
     *
     * @param data The data of which the sums will be calculated.
     * @return A binary sum tree of the data.
     */
    private static int[] sumTree(byte[] data) {
        int size = data.length;
        int[] sums = new int[size - 1];

        // The first elements are the pairwise sums of the data:
        for (int i = 0; i < size / 2; i++) {
            sums[i] = data[2 * i] + data[2 * i + 1];
        }

        // The rest of the elements are calculated from the previous sums:
        int pointer = size / 2;
        for (int i = size / 4; i >= 1; i = i / 2) {
            for (int j = 0; j < i; j++) {
                sums[pointer + j] = sums[pointer - 2 * i + 2 * j] + sums[pointer - 2 * i + 2 * j + 1];
            }
            pointer += i;
        }


        return sums;
    }
    
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
    
    
    /**
     * This creates a Haar transform of a data set whose length is some positive
     * power of two.
     *
     * @param data The data set that will be transformed. It's length has to be
     * some positive power of two.
     * @return The transform. The first half of the coefficients are of the
     * wavelets with the smallest supports, whereas the second last will be of
     * the mother wavelet and the last one will be the sum of all the data.
     */
    public static int[] transformPowerOfTwo(byte[] data) {

        int size = data.length;
        int[] sums = sumTree(data);
        int[] transform = new int[size];


        // First level will be the smallest wavelets. These are calculated
        // from the data:
        for (int i = 0; i < size / 2; i++) {
            transform[i] = data[2 * i] - data[2 * i + 1];
        }

        // The rest of the coefficients are calculated from the sum tree:
        int pointer = size / 2;
        for (int i = size / 4; i >= 1; i >>= 1) {
            for (int j = 0; j < i; j++) {
                transform[pointer + j] = sums[pointer - 2 * i + 2 * j] - sums[pointer - 2 * i + 2 * j + 1];
            }
            pointer += i;
        }

        // The last element is the coefficient of the wavelet (1,1,1,....,1),
        // that is, the sum of all the data:
        transform[size - 1] = sums[sums.length - 1];

        return transform;
    }


    /**
     * Retrieves original data from the transform. It is assumed that the length
     * of the transform (and thus the original data) is a power of two.
     *
     * @param transform The coefficients produced by the transform.
     * @return The original data.
     */
    public static byte[] inversePowerOfTwo(int[] transform) {

        int size = transform.length;
        int[] inverse = new int[size];

        // We'll sum the all wavelets of the same size at one time. Below the
        // variable howMany tells how many wavelets there are of the kind that 
        // are summed at the time. The variable pointer points to the index of first
        // such wavelet's coefficient. Variable theSummed tells, how manyth is
        // the wavelet we are summing, counted from pointer.
        int pointer = 0; // 
        for (int howMany = size / 2; howMany >= 1; howMany >>= 1) {
            for (int theSummed = 0; theSummed < howMany; theSummed++) {
                int step = size / (2 * howMany);    // On how many number the summed wavelet has the value 1...
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

        return addDivideConvert(inverse, transform[transform.length - 1], transform.length);
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
     * Transfroms a data array of an arbitrary length. The data array will be
     * split into parts whose lengths are powers of two, transformed with the
     * method transformPowerOfTwo and then glued together.
     *
     * @param data The data to be transformed.
     * @return The transfrom.
     */
    public static int[] transformArbitraryLength(byte[] data) {
        int subSize = supPowerOfTwo(data.length);    // the biggest power of two that can be fitted in the remaingn data length
        int remainingSize = data.length;    // The length of data yet to be transformed.
        int[] transform = new int[data.length]; // The result of the transform
        int pointer = 0;                    // Where the untransformed data begins

        while (remainingSize > 1 && subSize > 1) {
            int[] partialTransforn = transformPowerOfTwo(Arrays.copyOfRange(data, pointer, pointer + subSize));
            copyValuesTo(partialTransforn, transform, pointer);
            pointer += subSize;
            remainingSize -= subSize;
            subSize = supPowerOfTwo(remainingSize);
        }

        // If the last number is not transformed, it will be copied:
        if (remainingSize == 1) {
            transform[transform.length - 1] = data[data.length - 1];
        }

        return transform;
    }

    /**
     * Calculates the inverse transform of an array with arbitrary length. It is
     * assumed that the input data is of the form produced by the method
     * transformArbitraryLength.
     *
     * @param transform The transform whose inverse will be calculated.
     * @return The original data from which the transform was made.
     */
    public static byte[] inverseArbitraryLength(int[] transform) {
        int size = transform.length;
        int subSize = supPowerOfTwo(size);
        int remainingSize = size;
        byte[] inverse = new byte[size];
        int pointer = 0;

        while (remainingSize > 1) {
            byte[] partialInverse = inversePowerOfTwo(Arrays.copyOfRange(transform, pointer, pointer + subSize));
            copyValuesTo(partialInverse, inverse, pointer);
            pointer += subSize;
            remainingSize -= subSize;
            subSize = supPowerOfTwo(remainingSize);
        }

        if (remainingSize == 1) {
            inverse[inverse.length - 1] = (byte) transform[transform.length - 1];
        }

        return inverse;
    }

   
    /**
     * Transforms a 3D array of data. The transform is done separately to each
     * one dimensional array data[i][j] with the method
     * transformArbitraryLength.
     *
     * @param data The data to be transformed.
     * @return The transform.
     */
    public static int[][][] transform3DArray(byte[][][] data) {

        int dim0 = data.length;
        int dim1 = data[0].length;
        int dim2 = data[0][0].length;
        int[][][] transform = new int[dim0][dim1][dim2];

        for (int i = 0; i < dim0; i++) {
            for (int j = 0; j < dim1; j++) {
                transform[i][j] = transformArbitraryLength(data[i][j]);
            }
        }

        return transform;
    }

    /**
     * Retrieves the original data of a transform of a 3D array. It is assumed
     * that the transform is of the form produced by the method
     * transform3DArray.
     *
     * @param transform The transform.
     * @return The original data.
     */
    public static byte[][][] inverse3DArray(int[][][] transform) {

        int dim0 = transform.length;
        int dim1 = transform[0].length;
        int dim2 = transform[0][0].length;
        byte[][][] inverse = new byte[dim0][dim1][dim2];

        for (int i = 0; i < dim0; i++) {
            for (int j = 0; j < dim1; j++) {
                inverse[i][j] = inverseArbitraryLength(transform[i][j]);
            }
        }

        return inverse;
    }

    
    
    public static int[] lossyTransformPowerOf2(byte[] data, int levelOfLoss){
        
        // The finest wavelets have 2* 2^{levelOfLoss} nonzero elements, of
        // which the fist half is ones, and the second -1s. To calculate it
        // we need a sum tree with the leaf size 2^{levelOfLoss}.
        
        int[] sumTree = sumTree(data, pow(2,levelOfLoss));
        int sizeOfLevelOne = (sumTree.length +1) / 4; // Tells how many wavelets
        // of the finest level we have.
        int[] transform = new int[2 * sizeOfLevelOne];
        
        int levelPointer = 0;
        for (int i = sizeOfLevelOne; i >= 1 ; i = i/2) { // transform on level at time
            for (int j = 0; j < i; j++) {
                int sumIndex = 2 * (levelPointer + j);
                transform[levelPointer + j] = sumTree[sumIndex] -sumTree[sumIndex+1];
            }
            levelPointer += i;
        }
        
        // The last entry is the sum of all the data:
        transform[transform.length - 1] = sumTree[sumTree.length -1];
        
        return transform;
    }
    
    /**
     * Performs a transform on the data array, but loses some of the data. The
     * parameter levelOfLoss tells how much data is lost. If it's 0, the
     * transform is lossless. For 1 the finest level of coefficients will be
     * omitted, for 2 the second finest too etc. This isn't very efficient
     * method, since it does all the work the lossless transform does and a
     * little more.
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
        // the untransformed part of the data array. Then it joins the coefficients that
        // will be kept after the losing to the array transform.
        while (remainingSize > 1 && subSize > 1) {
            int[] partialTransform = transformPowerOfTwo(Arrays.copyOfRange(data, pointer, pointer + subSize));
            int keepSize = Math.max(1, subSize / pow(2, levelOfLoss));
            partialTransform = Arrays.copyOfRange(partialTransform, subSize - keepSize, subSize);
            // An alternative way to do it, it takes as much time as the one used.
            // int[] partialTransform = lossyTransformPowerOf2(Arrays.copyOfRange(data, pointer, pointer + subSize), levelOfLoss);
            transform = joinArrays(transform, partialTransform);
            pointer += subSize;
            remainingSize -= subSize;
            subSize = supPowerOfTwo(remainingSize);
        }

        // If the last number is not transformed, it will be copied:
        if (remainingSize == 1) {
            transform[transform.length - 1] = data[data.length - 1];
        }

        return transform;
    }

    /**
     * Performs a transform on a 3D data array and loses some of the data. The
     * parameter levelOfLoss tells how much data is lost. If it's 0, the
     * transform is lossless. For 1 the finest level of coefficients will be
     * omitted, for 2 the second finest too etc. This isn't very efficient
     * method, since it does all the work the lossless transform does and a
     * little more.
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
     * Retrieves the (approximate) original data from a lossy transform. It is
     * assumed that the transform is of the form produced by the method lossyTransform.
     * 
     * @param transform The transform to be inverted.
     * @param originalHeight The height of the original picture, or more generally the length
     * of the compressed line before the compression.
     * @param levelOfLoss How many levels of coefficients were lost in the transform.
     * @return The (approximate) original data.
     */
    public static byte[][][] inverseLossy3DArray(int[][][] transform, int originalHeight, int levelOfLoss) {

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
