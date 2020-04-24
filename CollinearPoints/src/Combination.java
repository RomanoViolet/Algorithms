public class Combination {

    /*
     * https://www.geeksforgeeks.org/print-all-possible-combinations-of-r-elements-
     * in-a-given-array-of-size-n/ arr[] ---> Input Array data[] ---> Temporary
     * array to store current combination start & end ---> Staring and Ending
     * indexes in arr[] index ---> Current index in data[] r ---> Size of a
     * combination to be printed
     * 
     * https://stackoverflow.com/a/16256122 https://stackoverflow.com/a/12992511
     */
    static void combinationUtil(int arr[], int data[], int start, int end, int index, int r) {
        // Current combination is ready to be printed, print it
        if (index == r) {
            for (int j = 0; j < r; j++)
                System.out.print(data[j] + " ");
            System.out.println("");
            return;
        }

        // replace index with all possible elements. The condition
        // "end-i+1 >= r-index" makes sure that including one element
        // at index will make a combination with remaining elements
        // at remaining positions
        for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i + 1, end, index + 1, r);
        }
    }

    // The main function that prints all combinations of size r
    // in arr[] of size n. This function mainly uses combinationUtil()
    static void printCombination(int arr[], int n, int r) {
        // A temporary array to store all combination one by one
        int data[] = new int[r];

        // Print all combination using temprary array 'data[]'
        combinationUtil(arr, data, 0, n - 1, 0, r);
    }

    static void combinations2(int[] arr, int len, int startPosition, int[] result) {
        if (len == 0) {
            for (int j = 0; j < result.length; j++)
                System.out.print(result[j] + " ");
            System.out.println(" ");
            return;
        }
        for (int i = startPosition; i <= arr.length - len; i++) {
            result[result.length - len] = arr[i];
            combinations2(arr, len - 1, i + 1, result);
        }
    }

    /* Driver function to check for above function */
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        int k = 3;
        int n = arr.length;
        int result[] = new int[k];
        printCombination(arr, n, k);
        System.out.println("Alternative: \n\n");
        combinations2(arr, k, 0, result);
    }
}
