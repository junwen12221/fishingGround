package cn.lightFish.staticBlog;

/**
 * Created by karak on 16-10-8.
 */
public class test {

    public static void main(String[] args) {

/*        int[] a = new int[]{0, 1, 2, 0, 3, 7, 5, 24, 7, 11, 6, 1, 5};
        a[11] = a[3] + a[11];
        a[5] = a[1] + a[a[9]];
        while (true) {
            a[4] = a[4] * a[4];
            if (a[5] == a[10]) break;
            a[4] -= 6;
            a[5] += a[2];
        }
        a[8] = a[7] - a[5];
        a[6] = a[4] + a[8];
        out.println(a[6]);
    */
        int[] a = new int[]{0, 0, 0, 0, 0, 0};
        do {
            if (a[4] > a[3]) {

            } else {
                a[1] = a[3];
                a[3] = a[4];
                a[4] = a[1];
            }
            if (a[4] < a[5]) {
                break;
            } else {
                a[1] = a[4];
                a[4] = a[5];
                a[5] = a[1];
            }
        } while (true);


    }
}
