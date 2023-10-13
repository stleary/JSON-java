public class AddArrays {
    public static void main(String[] args) {
        int[] array1 = {1, 2, 3, 4, 5};
        int[] array2 = {6, 7, 8, 9, 10};

        if (array1.length != array2.length) {
            System.out.println("Arrays must have the same length for element-wise addition.");
            return;
        }

        int[] sumArray = new int[array1.length];

        for (int i = 0; i < array1.length; i++) {
            sumArray[i] = array1[i] + array2[i];
        }

        System.out.println("Resulting array after element-wise addition:");
        for (int element : sumArray) {
            System.out.print(element + " ");
        }
    }
}
