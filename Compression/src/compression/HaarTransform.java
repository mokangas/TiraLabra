package compression;

import java.util.Arrays;

public class HaarTransform {

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
     * Creates a binary tree of the sums of the data. It is assumed that s :=
     * data.length is a power of 2. The resulting tree is presented as an array
     * with leaves as the first s/2 elements, their parents as the next s/4
     * elements etc. and the last element will be the root, i.e. the sum of all
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

    public static byte[] inversePowerOfTwo(int[] transform) {

        int size = transform.length;
        int[] inverse = new int[size];

        // We'll sum the all wavelets of the same size at one time. Below the
        // variable howMany tells how many wavelets there are of the kind that 
        // is currently summed. The variable pointer points to the index of first
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
     * Converts integers to bytes, but first adds addThis to each of them, and
     * divides by divideByThis. This weird method exists just to serve the needs
     * of the mehtod inversePowerOfTwo.
     *
     * @param data The integers to be manipulated.
     * @param addThis The constant which is added to the integers.
     * @param divideByThis The constant by which each integer is divided.
     * @return
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
     * @param transform The transform that will be inversed.
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
        
        if (remainingSize ==1) {
            inverse[inverse.length-1] = (byte) transform[transform.length -1];
        }

        return inverse;
    }

    /**
     * Copies an array of integers into another array.
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
     * Copies an array of bytes into another array.
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
     * Returns the greatest number that is power of two and less than n.
     *
     * @param n A nonnegative integer.
     * @return sup{ 2^k : 2^k <= n}.
     */
    public static int supPowerOfTwo(int n) {
        int sup = 1;
        while ( sup <= n){
            sup *= 2;
        }
        return sup/2;
    }
    
    
    /**
     * Transforms a #D array of data. The transform is done separately to each
     * data[i][j].
     * @param data The data to be transformed.
     * @return The transform.
     */
    public static int[][][] transform3DArray(byte[][][] data){
        
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
    
    public static byte[][][] inverse3DArray(int[][][] transform){
        
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
}
