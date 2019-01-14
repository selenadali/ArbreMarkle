import org.apache.commons.codec.binary.Hex;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class markle {

    public static String encode(String key, byte[] data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(data));
    }

    public static String f(byte[] data) throws Exception {
        byte[] concatenated = concatarray("0".getBytes(),data);
        return encode("key", concatenated);
    }

    public static String n(byte[] h1, byte[] h2) throws Exception {
        byte[] concatenated = concatarray("1".getBytes(),concatarray(h1,h2));
        return encode("key", concatenated);
    }

    public static byte[] concatarray(byte[] a, byte[] b){
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static Boolean isData (byte[] data, String[] F, String H_sommet_reel, int index, int n) throws Exception {
        String f_frere = "";
        String h_oncle = "";
        String h_sommet = "";
        String h = "";
        // F[n] F[n-1]

        if(index+1 != n) {
            if (index % 2 == 0) {
                f_frere = F[index + 1];
                h = n(f(data).getBytes(), f_frere.getBytes());

            } else {
                f_frere = F[index - 1];
                h = n(f_frere.getBytes(), f(data).getBytes());

            }

            if (index >= n / 2) {
                if (index % 2 == 0) {
                    h_oncle = n(F[index - 2].getBytes(), F[index - 1].getBytes());
                } else {
                    h_oncle = n(F[index - 3].getBytes(), F[index - 2].getBytes());
                }
                h_sommet = n(h_oncle.getBytes(), h.getBytes());

            } else {
                if (index % 2 == 0) {
                    h_oncle = n(F[index + 2].getBytes(), F[index + 3].getBytes());
                    h_sommet = n(h.getBytes(), h_oncle.getBytes());

                } else {
                    h_oncle = n(F[index + 1].getBytes(), F[index + 2].getBytes());
                    h_sommet = n(h.getBytes(), h_oncle.getBytes());
                }
            }

            if (n % 2 != 0) {
                h_sommet = n(h_sommet.getBytes(), F[n - 1].getBytes());
            }

            if (h_sommet.toString().equals(H_sommet_reel.toString())) {
                return true;
            } else {
                return false;
            }
        }
        else{
            String F_dern = F[n-1];
            String first_sommet = calculHsommet(n-1,Arrays.copyOfRange(F, 0, n-1));
            h_sommet = n(first_sommet.getBytes(),F[index].getBytes());
            if (h_sommet.toString().equals(H_sommet_reel.toString())) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String[] calculH(int n, String[] F) {
        String[] H = new String[n-1];
        if(n%2==0){
            for(int j = 1; 2*j<=n;j++){
                try {
                    H[j-1] = n(F[2*j-2].getBytes(), F[2*j-1].getBytes());
                    System.out.println(("H "+ String.valueOf(j-1) + " = "  + H[j-1]));

                } catch (Exception e) {
                    break;
                }
            }
        }
        else{
           for(int j = 1; 2*j<n;j++){
                    try {
                    H[j-1] = n(F[2*j-2].getBytes(), F[2*j-1].getBytes());
                } catch (Exception e) {
                    break;
                    }
                System.out.println(("H "+ String.valueOf(j-1) + " = "  + H[j-1]));
                if(2*j+1==n){
                    H[j] = F[n-1];
                    System.out.println("F dernier: " + F[n-1]);
                }
            }
        }
        //System.out.println(("H xx"+ String.valueOf(H.length) + " = "  + H[0]));

        return H;
    }

    public static  String calculHsommet(int n, String[] F) throws Exception {
        String H_sommet = "";
        if(n%2==0){
            for(int k= 0; k<(n/2);k++){
                String[] T = calculH(n, F);
                F = T;
                H_sommet = T[0];
            }
            //calculH(n, F);
        }
        else{
            String F_dern ="";
            for(int k= 0; k<(n+1/2)-1;k++){
                String[] T = calculH(n, F);
                //System.out.println("T "+ String.valueOf(T.length) + " = " + T[0] + " " + T[1]+ " " + T[2] );
                if(k == 0){
                    F_dern = T[T.length-2];
                    F = Arrays.copyOf(T, T.length-2);
                    //System.out.println("F first"+ F.length + " " + F[0] + " " + F[1] );
                    n = n-1;
                    F = T;
                }
                if( k + 1 == (n+1)/2){
                    T[1] = F_dern;
                    System.out.println("H last"+  " " + T[0] + " " + T[1]);
                    String[] Tmp = new String[2];
                    Tmp[0] = T[0];
                    Tmp[1] = T[1];
                    F = Tmp;
                }
                else{
                    F = T;
                }
                H_sommet = T[0];
            }
        }
        return H_sommet;
    }


    public static void main(String [] args) throws Exception {
        // data => type byte[]
        //for d in data:
        // encode(n(d , d+1), d+2)
        // n =  nombre datasets
        int nbr = 5;
        int n = nbr;
        byte[][] D = new byte[n][2056];
        D[0]="Text1".getBytes();
        D[1]="Text2".getBytes();
        D[2]="Text3".getBytes();
        D[3]="Text4".getBytes();
        D[4]="Text5".getBytes();
        System.out.println(D[0]);
        String H_sommet = "";
        String[] F = new String[n];
        String[] F_first = new String[n];
        int i = 0;
        for(byte[] data : D){
            F[i] = f(data).toString();
            F_first[i] = f(data).toString();
            System.out.println("F "+ String.valueOf(i) + " = " + F[i]);
            i++;
        }

        H_sommet = calculHsommet( nbr, F );
        System.out.println("H_sommet: " + H_sommet);
        
        byte[] test_this_data = "Text5".getBytes();
        System.out.println(isData(test_this_data, F_first, H_sommet, 4,nbr));
    }
}