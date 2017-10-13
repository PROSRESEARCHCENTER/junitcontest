package hep.aida.ref.pdf;

/**
 *
 * @author turri
 */
public class MathUtils {


    public static double erf(double d) {
        double d1 = (0.0D / 0.0D);
        double d2 = Math.abs(d);
        if(d2 <= 1.0D) {
            if(d2 <= erf_sqeps)
                d1 = (2D * d) / 1.7724538509055161D;
            else
                d1 = d * (1.0D + csevl(2D * d * d - 1.0D, erfc_coef));
        } else
            if(d2 <= erf_xbig)
                d1 = sign(1.0D - erfc(d2), d);
            else
                d1 = sign(1.0D, d);
        return d1;
    }


    public static double erfc(double d) {
        double d1 = (0.0D / 0.0D);
        if(d <= erfc_xsml)
            d1 = 2D;
        else
            if(d <= erfc_xmax) {
                double d2 = Math.abs(d);
                if(d2 <= 1.0D) {
                    if(d2 < erf_sqeps)
                        d1 = 1.0D - (2D * d) / 1.7724538509055161D;
                    else
                        d1 = 1.0D - d * (1.0D + csevl(2D * d * d - 1.0D, erfc_coef));
                } else {
                    d2 *= d2;
                    if(d2 <= 4D)
                        d1 = (Math.exp(-d2) / Math.abs(d)) * (0.5D + csevl((8D / d2 - 5D) / 3D, erfc2_coef));
                    else
                        d1 = (Math.exp(-d2) / Math.abs(d)) * (0.5D + csevl(8D / d2 - 1.0D, erfcc_coef));
                    if(d < 0.0D)
                        d1 = 2D - d1;
                }
            } else {
                d1 = 0.0D;
            }
        return d1;
    }

    private static double csevl(double d, double ad[]) {
        int i = ad.length;
        double d2 = 0.0D;
        double d1 = 0.0D;
        double d3 = 0.0D;
        double d4 = 2D * d;
        for(int j = i - 1; j >= 0; j--) {
            d3 = d2;
            d2 = d1;
            d1 = (d4 * d2 - d3) + ad[j];
        }

        return 0.5D * (d1 - d3);
    }

    public static double sign(double d, double d1) {
        double d2 = d >= 0.0D ? d : -d;
        return d1 >= 0.0D ? d2 : -d2;
    }

    private static final double erfc_coef[] = {
        -0.049046121234691806D, -0.14226120510371365D, 0.010035582187599796D, -0.00057687646997674853D,
	2.741993125219606E-005D, -1.1043175507344507E-006D, 3.8488755420345036E-008D, -1.1808582533875466E-009D,
	3.2334215826050907E-011D, -7.9910159470045487E-013D,
        1.7990725113961456E-014D, -3.7186354878186928E-016D, 7.1035990037142532E-018D
    };
    private static final double erfc2_coef[] = {
        -0.069601346602309502D, -0.041101339362620892D, 0.0039144958666896268D,
	-0.00049063956505489791D, 7.1574790013770361E-005D, -1.1530716341312328E-005D,
	1.9946705902019974E-006D, -3.6426664715992229E-007D, 6.9443726100050124E-008D,
	-1.3712209021043659E-008D, 2.7883896610071373E-009D, -5.8141647243311614E-010D,
	1.2389204917527532E-010D, -2.6906391453067435E-011D, 5.9426143508479114E-012D,
	-1.3323867357581197E-012D, 3.0280468061771323E-013D, -6.9666488149410327E-014D,
	1.620854541053923E-014D, -3.8099344652504917E-015D, 9.0404878159788311E-016D,
	-2.1640061950896072E-016D, 5.2221022339958551E-017D, -1.2697296023645554E-017D,
	3.1091455042761977E-018D
    };
    private static final double erfcc_coef[] = {
        0.071517931020292483D, -0.026532434337606717D, 0.0017111539779208558D,
	-0.00016375166345851787D, 1.9871293500552038E-005D, -2.8437124127665552E-006D,
	4.6061613089631305E-007D, -8.227753025879209E-008D, 1.5921418727709012E-008D,
	-3.2950713622528431E-009D, 7.2234397604005558E-010D, -1.6648558133987297E-010D,
	4.0103925882376649E-011D, -1.0048162144257311E-011D, 2.6082759133003339E-012D,
	-6.9911105604040245E-013D, 1.9294923332617072E-013D, -5.4701311887543309E-014D,
	1.5896633097626975E-014D, -4.7268939801975551E-015D, 1.4358733767849847E-015D,
	-4.4495105618173579E-016D, 1.4048108847682335E-016D, -4.5138183877642106E-017D,
	1.4745215410451331E-017D, -4.8926214069457765E-018D, 1.6476121414106467E-018D,
	-5.6268171763294081E-019D, 1.9474433822320786E-019D
    };
    private static final double erf_xbig = Math.sqrt(-Math.log(1.9678190753608168E-016D));
    private static final double erf_sqeps = Math.sqrt(2.2204460492503E-016D);
    private static final double erfc_xsml = -Math.sqrt(-Math.log(1.9678190753608168E-016D));
    private static double erfc_xmax;


}
