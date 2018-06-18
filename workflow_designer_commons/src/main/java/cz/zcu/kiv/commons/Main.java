package cz.zcu.kiv.commons;

import cz.zcu.kiv.commons.GraphUtils.LineEquation;
;import java.io.IOException;

public class Main {
    public static void main(String[]args) throws IOException {
        System.out.println("SUCCESS");
        LineEquation eq = new LineEquation();
        System.out.println(eq.process().getLayout());
    }
}
